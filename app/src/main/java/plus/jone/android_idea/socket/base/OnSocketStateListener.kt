package jone.plus.socket.plus.base

import java.lang.Exception
import java.net.Socket

interface OnSocketStateListener {
    fun prepare()
    fun connect()
    fun onNewConnect(socket: Socket)
    fun disConnect(exception: Exception?)
}