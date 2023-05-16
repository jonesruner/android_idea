package plus.jone.android_idea.socket

import jone.plus.socket.plus.base.*
import jone.plus.socket.plus.model.Msg
import plus.jone.android_idea.socket.socket.JClient
import plus.jone.android_idea.socket.socket.JServer
import kotlin.concurrent.thread

class JSocketDelegate {

    private var jChat: JChat? = null

    fun connectToServer(remoteAddress: String, remotePort: Int): JChat {
        return JClient(remoteAddress, remotePort).apply {
            jChat = this
        }
    }

    fun createChatRoom(roomPort: Int): JChat {
        return JServer(roomPort).apply {
            jChat = this
        }
    }

    fun start() {
        try {
            thread {
                jChat?.start()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun getMsgDelegate(): OnMsgDelegate? {
        return jChat?.getMsgDelegate()
    }

    fun getStateDelegate(): OnStateDelegate? {
        return jChat?.getStateDelegate()
    }

    fun addOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        jChat?.addOnMsgReceivedListener(msgReceiver)
    }

    fun removeOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        jChat?.removeOnMsgReceivedListener(msgReceiver)
    }

    fun addOnStateListener(onStateListener: OnSocketStateListener) {
        jChat?.addOnStateListener(onStateListener)
    }

    fun removeOnStateListener(onStateListener: OnSocketStateListener) {
        jChat?.removeOnStateListener(onStateListener)
    }

    fun sendMsg(msg: Msg) {
        jChat?.sendMsg(msg)
    }

    fun stop() {
        try {
             thread {
                 jChat?.stop()
             }
        }catch (e:java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    fun getLocalAddress(): String? {
        return jChat?.getLocalAddress()
    }


    fun getServerAddress(): String? {
        return jChat?.getServerAddress()
    }

    fun getRemoteAddress(): String? {
        return jChat?.getRemoteAddress()
    }

    fun isServer(): Boolean {
        return jChat is JServer
    }
}