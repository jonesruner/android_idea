package plus.jone.android_idea.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import plus.jone.android_idea.R
import plus.jone.android_idea.base.exts.requestPermissions
import plus.jone.android_idea.base.exts.setLetterTrafficClickListener
import plus.jone.android_idea.base.exts.showToast
import plus.jone.android_idea.base.utills.LoadingUtils
import plus.jone.android_idea.databinding.ActivityBthBinding
import plus.jone.android_idea.databinding.BthDeviceItemBinding


class BthActivity : AppCompatActivity() {
    private val TAG = "TAG_BTH_ACTIVITY_"
    private val BLUTE_TOOTH_REQ =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d(TAG, "it.resultdata: ${it.data} ${it.describeContents()}")
            if (it.resultCode == RESULT_OK) {
                getPairedDevices()
                showToast("Opened Successfully")
            } else {
                showToast("Opened Failure!")
            }
        }

    private val binding by lazy {
        ActivityBthBinding.inflate(layoutInflater)
    }

    private var bluetoothAdapter: BluetoothAdapter? = null
    private val pairDevice = mutableMapOf<String, BluetoothDevice>()
    private val unPairDevice = mutableMapOf<String, BluetoothDevice>()
    private val connectedDevice = mutableMapOf<String, BluetoothDevice>()
    private val devices = mutableListOf<BluetoothDevice>()

    var dialog :Dialog? = null
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        requestPermissions(NEED_PERM) {
            it.forEach { (t, u) ->
                if (!u) {
                    showToast("unset permission!")
                    return@requestPermissions
                }
            }
        }

        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (bluetoothAdapter == null) {
            showToast("您的设备不支持蓝牙。。。")
            return
        }

        binding.bthDeviceList.layoutManager = LinearLayoutManager(this)
        binding.bthDeviceList.adapter = adapater
        binding.bthBtnOpen.isChecked = bluetoothAdapter?.isEnabled == true
        binding.bthBtnOpen.setLetterTrafficClickListener {
            if (!bluetoothAdapter!!.isEnabled) {
                BLUTE_TOOTH_REQ.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                Log.d(TAG, "onCreate: Blue Tooth has not been opened...")
            } else {
                bluetoothAdapter!!.disable()
//                getPairedDevices()
            }
        }

        binding.bthBtnOpenScan.setLetterTrafficClickListener {
            if (bluetoothAdapter?.isDiscovering == true) {
                bluetoothAdapter?.cancelDiscovery()
            }
            dialog = LoadingUtils.showLoading(this)
            if (bluetoothAdapter?.startDiscovery() == true) {
                Log.d(TAG, "onCreate: open bluetooth discovery...")
                unPairDevice.clear()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getPairedDevices() {
        bluetoothAdapter?.bondedDevices?.forEach { device ->
            addPairDevice(device)
        }
    }

    private fun isConnected(device: BluetoothDevice?): Boolean {
        var is_connected = false
        try {
            //使用反射调用被隐藏的方法
            val isConnectedMethod =
                BluetoothDevice::class.java.getDeclaredMethod(
                    "isConnected"
                )
            isConnectedMethod.isAccessible = true
            is_connected = isConnectedMethod.invoke(device) as Boolean
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return is_connected
    }

    private fun addPairDevice(device: BluetoothDevice) {
        val deviceHardwareAddress = device.address // MAC address
        when (isConnected(device)) {
            true -> connectedDevice[deviceHardwareAddress] = device
            false -> pairDevice[deviceHardwareAddress] = device
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive: ${intent.action}")
            when ("${intent.action}") {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                BluetoothDevice.EXTRA_DEVICE,
                                BluetoothDevice::class.java
                            )
                        } else {
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        }
                    device?.run {
                        addUnpairedDevice(this)
                    }
                }
                else -> updateUI()
            }
        }
    }

    private fun addUnpairedDevice(bluetoothDevice: BluetoothDevice) {
        val deviceHardwareAddress = bluetoothDevice.address // MAC address
        unPairDevice[deviceHardwareAddress] = bluetoothDevice
    }

    @SuppressLint("MissingPermission", "NotifyDataSetChanged")
    private fun updateUI() {
        devices.clear()
        connectedDevice.forEach { t, u ->
            devices.add(u)
            Log.d(TAG, "updateUI: connected device: ${u.name} ${u.type}  ${u.address}")
        }
        pairDevice.forEach { t, u ->
            devices.add(u)
            Log.d(TAG, "updateUI: paired device: ${u.name} ${u.type}  ${u.address}")
        }
        unPairDevice.forEach { t, u ->
            devices.add(u)
            Log.d(TAG, "updateUI: unpaired device: ${u.name} ${u.type}  ${u.address}")
        }
        adapater.notifyDataSetChanged()
        dialog?.dismiss()
    }


    private val adapater = object : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(this@BthActivity)
                .inflate(R.layout.bth_device_item, parent, false)
            return object : ViewHolder(view) {}
        }

        override fun getItemCount(): Int {
            return devices.size
        }

        @SuppressLint("MissingPermission")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (BthDeviceItemBinding.bind(holder.itemView)).run {
                val device: BluetoothDevice = devices[position]
                bthDeviceName.text = device.name
                if (position < connectedDevice.size) {
                    bthDeviceState.text = "connected"
                } else if (position < (connectedDevice.size + pairDevice.size)) {
                    bthDeviceState.text = "paired"
                } else {
                    bthDeviceState.text = "unpaired"
                    bthDeviceState.visibility = View.GONE
                }

                var icon: Int = -1

                val bluetoothClass: BluetoothClass = device.bluetoothClass
                val deviceClass = bluetoothClass.deviceClass //设备类型（音频、手机、电脑、音箱等等）
                val majorDeviceClass =
                    bluetoothClass.majorDeviceClass //具体的设备类型（例如音频设备又分为音箱、耳机、麦克风等等）
                if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
                    //音箱
                    icon = R.drawable.headset
                } else if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_MICROPHONE) {
                    //麦克风
                    icon = R.drawable.mic
                } else if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES) {
                    //耳机
                    icon = R.drawable.headset
                } else if (majorDeviceClass == BluetoothClass.Device.Major.COMPUTER) {
                    //电脑
                    icon = R.drawable.pc
                } else if (majorDeviceClass == BluetoothClass.Device.Major.PHONE) {
                    //手机
                    icon = R.drawable.mobile
                } else {
                    icon = R.drawable.bluetooth
                }
                bthDevicesIcon.setImageResource(icon)

//                else if (majorDeviceClass == BluetoothClass.Device.Major.HEALTH) {
//                    //健康类设备
//
//                    //其它蓝牙设备
//
//                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    companion object {
        val NEED_PERM = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }.toTypedArray()
    }
}