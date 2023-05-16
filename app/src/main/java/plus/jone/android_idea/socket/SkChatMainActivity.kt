package plus.jone.android_idea.socket

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import jone.plus.socket.plus.base.OnSocketStateListener
import plus.jone.android_idea.R
import plus.jone.android_idea.base.exts.showToast
import plus.jone.android_idea.base.utills.LoadingUtils
import plus.jone.android_idea.base.utills.NetworkUtils
import plus.jone.android_idea.databinding.ActivitySkChatBinding
import plus.jone.android_idea.socket.fragment.SkChatEnterFragment
import plus.jone.android_idea.socket.fragment.SkChatFragment
import java.net.Socket
import kotlin.concurrent.thread

class SkChatMainActivity : AppCompatActivity(), OnSocketStateListener {

    private val binding by lazy {
        ActivitySkChatBinding.inflate(layoutInflater)
    }

    private var jSocketDelegate: JSocketDelegate = JSocketDelegate()
    val delegate = jSocketDelegate

    private var currentFrag = FRAG_CHAT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        jSocketDelegate.addOnStateListener(this)
        NetworkUtils.getNetName()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(binding.acSkContainer.id, SkChatEnterFragment(), FRAG_LOGIN)
        val skChatFragment = SkChatFragment()
        transaction.add(binding.acSkContainer.id, skChatFragment, FRAG_CHAT)
        transaction.hide(skChatFragment)
        transaction.commit()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val showLoading = LoadingUtils.showLoading(this@SkChatMainActivity)
                run {
                    delegate.stop()
                    if(supportFragmentManager.findFragmentByTag(FRAG_CHAT)?.isVisible == true)
                    {
                        toggleFrag(FRAG_LOGIN)
                    }else {
                        finishAfterTransition()
                    }
                    showLoading.dismiss()
                }
            }
        });
    }


    fun toggleFrag(frag: String) {
        Log.d("TAG__", "toggleFrag:$frag ")
        val beginTransaction = supportFragmentManager.beginTransaction()
        when (frag) {
            FRAG_CHAT -> {
                beginTransaction.show(supportFragmentManager.findFragmentByTag(FRAG_CHAT)!!)
                beginTransaction.hide(supportFragmentManager.findFragmentByTag(FRAG_LOGIN)!!)
            }
            FRAG_LOGIN -> {
                beginTransaction.show(supportFragmentManager.findFragmentByTag(FRAG_LOGIN)!!)
                beginTransaction.hide(supportFragmentManager.findFragmentByTag(FRAG_CHAT)!!)
            }
        }
        beginTransaction.commit()
        currentFrag = frag
    }

    companion object {
        const val FRAG_CHAT = "chat_frag"
        const val FRAG_LOGIN = "connect_frag"
    }


    override fun onDestroy() {
        super.onDestroy()
        delegate.removeOnStateListener(this)
        delegate.stop()
    }

    private val TAG = "TAG_SC_CHAT_MAIN_"
    private var loading: Dialog? = null
    override fun prepare() {
        binding.root.post {
            loading = LoadingUtils.showLoading(this)
        }
        Log.d(TAG, "prepare: ")
    }

    override fun connect() {
        delegate.addOnMsgReceivedListener(supportFragmentManager.findFragmentByTag(FRAG_CHAT) as SkChatFragment)
        binding.root.post {
            loading?.dismiss()
            showToast("Connect Success !")
            toggleFrag(FRAG_CHAT)
            supportActionBar?.title =
                "${resources.getString(R.string.app_name)}[${delegate.getLocalAddress()}]"
        }
        Log.d(TAG, "connect: ${NetworkUtils.getLocalIpAddress()}:")
    }

    override fun onNewConnect(socket: Socket) {
        Log.d(TAG, "onNewConnect: ${socket.inetAddress.hostName}")
    }

    override fun disConnect(exception: Exception?) {
        binding.root.post {
            loading?.dismiss()
            showToast("Connect Break !")
            toggleFrag(FRAG_LOGIN)
            supportActionBar?.title = "${resources.getString(R.string.app_name)}"
        }
        delegate.removeOnMsgReceivedListener(supportFragmentManager.findFragmentByTag(FRAG_CHAT) as SkChatFragment)
        Log.d(TAG, "disConnect: ${exception?.message}")
    }
}