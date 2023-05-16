package plus.jone.android_idea.socket.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import plus.jone.android_idea.base.exts.setLetterTrafficClickListener
import plus.jone.android_idea.base.utills.NetworkUtils
import plus.jone.android_idea.databinding.FragmentAcSkEnterBinding
import plus.jone.android_idea.socket.SkChatMainActivity
import java.net.Socket

class SkChatEnterFragment : Fragment() {
    private val TAG = "TAG_SK_CHAT_: "
    private val binding by lazy {
        FragmentAcSkEnterBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ipAddress = NetworkUtils.getLocalIpAddress()
        binding.skChatLocalIp.setText(ipAddress)
        binding.acSkIpInput.setIpAddress("${ipAddress}")
        binding.acSkJoinRoom.setLetterTrafficClickListener {
            Log.d(TAG, "onViewCreated:${Runtime.getRuntime().availableProcessors()} ")
            try {
                val remoteAddress = binding.acSkIpInput.getIpAddress()
                var remotePort = "${binding.skChatServerPort.text}".toInt()
                if (lagacyPort(remotePort)) {
                    remotePort = 6666
                    binding.skChatServerPort.setText("6666")
                }
                activity().run {
                    Log.d("TAG___", "onViewCreated: ready to connect server")
                    delegate.connectToServer(remoteAddress, remotePort)
                    addListener()
                    delegate.start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        binding.acSkCreateRoom.setLetterTrafficClickListener {
            var port = "${binding.skChatLocalPort.text}".toInt()
            if (lagacyPort(port)) {
                port = 6666
                binding.skChatLocalPort.setText("6666")
            }
            activity().run {
                delegate.createChatRoom(port)
                addListener()
                delegate.start()
            }
        }
    }

    private fun  addListener() {
        activity().delegate.run {
            addOnStateListener(activity())
        }
    }

    private fun lagacyPort(remotePort: Int) = remotePort > 65535 || remotePort < 0
    fun activity() = (requireActivity() as SkChatMainActivity)


}