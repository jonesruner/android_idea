package jone.plus.socket.plus.model

import java.util.Date

data class Msg(
    var content:Any? = null,
    var sender:User? = null,
    var receiver:User? = null,
    var allUser:Boolean = false
)
