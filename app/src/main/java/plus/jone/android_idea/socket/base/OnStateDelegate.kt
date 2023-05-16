package jone.plus.socket.plus.base

import java.lang.Exception
import java.net.Socket

class OnStateDelegate {
    enum class SocketState{
        PREPARE,CONNECT,DISCONNECT
    }

    private var currentState = SocketState.PREPARE
    private val onStateListenerList = mutableListOf<OnSocketStateListener>()

    fun addOnStateListener(onStateListener:OnSocketStateListener){
        onStateListenerList.add(onStateListener)
    }
    fun removeOnStateListener(onStateListener:OnSocketStateListener){
        onStateListenerList.remove(onStateListener)
    }

    fun prepare(){
        onStateListenerList.forEach {
            it.prepare()
            currentState= SocketState.PREPARE
        }
    }
    fun connect(){
        onStateListenerList.forEach {
            it.connect()
            currentState= SocketState.CONNECT
        }
    }

    fun onNewConnect(socket: Socket){
        onStateListenerList.forEach {
            it.onNewConnect(socket)
            currentState= SocketState.CONNECT
        }
    }
    fun disConnect(exception: Exception? = null){
        onStateListenerList.forEach {
            it.disConnect(exception)
            currentState= SocketState.DISCONNECT
        }
    }
    fun isConnect() = currentState == SocketState.CONNECT
}