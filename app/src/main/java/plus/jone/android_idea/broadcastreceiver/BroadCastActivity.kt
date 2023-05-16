package plus.jone.android_idea.broadcastreceiver

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import plus.jone.android_idea.databinding.ActivityBroadCastBinding


class BroadCastActivity : AppCompatActivity() ,MyBroadCastReceiver.OnReceiver{
    private val ACTION_BC = "plus.jone.android_idea.broadcastreceiver"
   private val binding  by lazy {
       ActivityBroadCastBinding.inflate(layoutInflater)
   }

    private val bc = MyBroadCastReceiver()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        filter.addAction(ACTION_BC)
        registerReceiver(bc,filter)



        // 发送广播
        binding.bcBtnSend.setOnClickListener {
            val intent = Intent(ACTION_BC)
            intent.putExtra("data","${binding.bcInp.text}")
            sendBroadcast(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        bc.setOnReceiver(this)
    }

    override fun onStop() {
        super.onStop()
        bc.setOnReceiver(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bc)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val str = StringBuilder().apply {
            append("action: ${intent?.action} \t")
            append("data: ${intent?.data} \t")
            val extras = intent?.extras ?: return
            append("extras: \n \t")
            extras.keySet().forEach {
                append(it)
                append(":")
                append(extras.get(it))
                append("\n\t")
            }

        }.toString()
        binding.bcToContent.text = "${binding.bcToContent.text } \n $str"
        Log.d("TAG_bc_: ", "onReceive: $str")
    }
}