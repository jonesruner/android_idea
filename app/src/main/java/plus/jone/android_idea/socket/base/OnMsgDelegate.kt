package jone.plus.socket.plus.base

import android.util.Log
import jone.plus.socket.plus.model.Msg
import jone.plus.socket.plus.model.User
import plus.jone.android_idea.base.utills.JsonUtils
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.Socket
import java.security.KeyStore.Entry
import java.util.*

class OnMsgDelegate {
    private var socketMap = mutableMapOf<String, Socket>()
    private val msgReceiverListenerList = mutableListOf<OnMsgReceiverListener>()
    private val msgList = LinkedList<Msg>()

    private fun getSocketAddress(socket: Socket): String? = socket.inetAddress.hostAddress

    fun addSocket(socket: Socket) {
        socketMap["${getSocketAddress(socket)}"] = socket
    }
    fun removeSocket(socket: Socket) {
        if (socketMap.containsKey(getSocketAddress(socket))) {
            socketMap.remove(getSocketAddress(socket))
        }
    }
    @Synchronized
    fun sendMsg(msg: Msg) {
        if(msg.content == null || msg.content == "")return
        Log.d("TAG_OnMsgDelegate:__ ", "sendMsg: msg: $msg")
        msgList.push(msg)
    }
    @Synchronized
    fun popMsg(): Msg? {
        if (msgList.size > 0) {
            return msgList.pop().apply {
                Log.d("popMsg:__ ", "sendMsg: msg: $this")
            }
        }
        return null
    }
    @Synchronized
    fun sendSocketMsg(msg: Msg) {
        if(msg.content == null || msg.content == "")return
        var soc :Map.Entry<String,Socket> ? = null
        try {
            if (msg.allUser ) {
                socketMap.onEachIndexed { index, entry ->
                     soc =entry
                    if (entry.value.isConnected && entry.key!=msg.sender?.myIp) {
                        val outputStream = entry.value.getOutputStream()
                        val objToString = "${JsonUtils.convertObjToString(msg)}"
                        outputStream.write(objToString.toByteArray())
                        outputStream.flush()

                        Log.d("TAG___OnMsgDelegat__", "sendSocketMsg: ${entry.key} msg: $objToString")
                    } else {
                        removeSocket(entry.value)
                    }
                }
            } else {
                if (socketMap.containsKey(msg.sender?.myIp)) {
                    socketMap[msg.sender?.myIp]?.run {
                        if(isClosed)return
                        val outputStream = getOutputStream()
                        outputStream.write(msg.toString().toByteArray())
                        outputStream.flush()
                    }
                }
            }
        }catch (e:java.lang.Exception){
            e.printStackTrace()
            if(soc?.value?.isClosed == true){
                socketMap.remove(soc!!.key)
            }
        }
    }

    fun receiverMsg(msg: Msg) {
        Log.d("TAG_msg_delegate", "receiverMsg: ")
        for (onMsgReceiverListener in msgReceiverListenerList) {
            Log.d("TAG_msg_delegate", "receiverMsg: ")
            onMsgReceiverListener.onMsgReceived(msg)
        }
    }

    fun addOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        msgReceiverListenerList.add(msgReceiver)
    }

    fun removeOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        msgReceiverListenerList.remove(msgReceiver)
    }

    fun releaseAllSocket() {
        socketMap.forEach { t, u ->
            try{
                u.getInputStream().close()
                u.getOutputStream().close()
                u.close()
                socketMap.remove(t)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        msgReceiverListenerList.clear()
    }
}