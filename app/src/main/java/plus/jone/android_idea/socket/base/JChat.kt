package jone.plus.socket.plus.base

import jone.plus.socket.plus.model.Msg
import java.net.InetAddress

interface JChat {
    fun start()
    fun stop()
    fun sendMsg(msg:Msg)
    fun getMsgDelegate():OnMsgDelegate
    fun getStateDelegate():OnStateDelegate
    fun addOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener)
    fun removeOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener)
    fun addOnStateListener(onStateListener: OnSocketStateListener)
    fun removeOnStateListener(onStateListener: OnSocketStateListener)
    fun getLocalAddress(): String?
    fun getRemoteAddress(): String?
    fun getServerAddress(): String?
}