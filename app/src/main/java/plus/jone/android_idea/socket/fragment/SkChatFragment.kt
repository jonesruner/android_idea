package plus.jone.android_idea.socket.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import jone.plus.socket.plus.base.OnMsgReceiverListener
import jone.plus.socket.plus.model.Msg
import jone.plus.socket.plus.model.User
import plus.jone.android_idea.R
import plus.jone.android_idea.base.exts.setLetterTrafficClickListener
import plus.jone.android_idea.base.utills.LoadingUtils
import plus.jone.android_idea.base.utills.NetworkUtils
import plus.jone.android_idea.databinding.FragmentAcSkChatBinding
import plus.jone.android_idea.socket.SkChatMainActivity
import plus.jone.android_idea.socket.SkChatMainActivity.Companion.FRAG_LOGIN


class SkChatFragment : Fragment(), OnMsgReceiverListener {

    private val binding by lazy {
        FragmentAcSkChatBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    private val TAG = "TAG_SK_CHAT_FRAG___"
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.acScBtmBtnSend.setLetterTrafficClickListener {
            activity().delegate.run {
                val msg = "${binding.acScBtmInputMsg.text}"
                if (msg.trim().isNotEmpty()) {
                    val sendMsg = Msg(
                        msg,
                        sender = User("${getLocalAddress()}", NetworkUtils.localDevicesName),
                        receiver = User(getRemoteAddress()),
                        allUser = true
                    )
                    activity().delegate.sendMsg(sendMsg)
                    onMsgReceived(sendMsg)
                    binding.acScBtmInputMsg.setText("")
                }
            }
         }
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        binding.recyclerView2.adapter = adapter
    }

    fun activity() = (requireActivity() as SkChatMainActivity)

    private val msgList = mutableListOf<Msg>()
    private val localHostName = NetworkUtils.getLocalIpAddress()
    override fun onMsgReceived(msg: Msg) {
        Log.d(TAG, "onMsgReceived: ${msg}")
        binding.root.post {
            msgList.add(msg)
            adapter.notifyItemInserted(msgList.size)
            binding.recyclerView2.scrollToPosition(msgList.size-1)
        }
    }
    private val TYPE_SERVER = 0x12
    private val TYPE_CLIENT = 0x11
    private val adapter = object : RecyclerView.Adapter<ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
           val view = when(viewType) {
                TYPE_CLIENT -> {
                    LayoutInflater.from(requireContext()).inflate(R.layout.chat_item_client,parent,false)
                }
                else -> {
                    LayoutInflater.from(requireContext()).inflate(R.layout.chat_item_server,parent,false)
                }
            }
            return object : ViewHolder(view){}
        }

        override fun getItemCount(): Int = msgList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val msg = msgList[position]
            holder.itemView.findViewById<MaterialTextView>(R.id.ci_msg).setText("${msg.content}")
            holder.itemView.findViewById<MaterialTextView>(R.id.ci_name).setText("${msg.sender?.nickName}")
            holder.itemView.findViewById<ImageView>(R.id.ci_avatar).run {
                Glide.with(requireContext()).load( if(getItemViewType(position) == TYPE_CLIENT) R.drawable.music else R.drawable.play).into(this)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return if (msgList[position].sender?.myIp == localHostName) {
                 TYPE_SERVER
            }else{
                TYPE_CLIENT
            }
        }
    }
}