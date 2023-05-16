package jone.plus.socket.plus.base

import jone.plus.socket.plus.model.Msg

interface OnMsgReceiverListener {
    fun onMsgReceived(msg:Msg)
}