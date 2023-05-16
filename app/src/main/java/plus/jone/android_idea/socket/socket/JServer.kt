package plus.jone.android_idea.socket.socket

import android.util.Log
import jone.plus.socket.plus.base.*
import jone.plus.socket.plus.model.Msg
import plus.jone.android_idea.base.utills.JsonUtils
import plus.jone.android_idea.base.utills.NetworkUtils
import java.io.InputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.concurrent.thread

class JServer(val port: Int) : JChat {
    private var serverSocket: ServerSocket? = null
    private var excutor: ExecutorService? = null
    private var mStateDelegate = OnStateDelegate()
    private var mMsgDelegate = OnMsgDelegate()

    override fun start() {
        thread {
            mStateDelegate.prepare()
            try {
                serverSocket = ServerSocket(port)
                mStateDelegate.connect()
                excutor =
                    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2)
                listener()
            } catch (e: Exception) {
                e.printStackTrace()
                mStateDelegate.disConnect()
            }
        }
    }

    private fun listener() {
        excutor?.execute {
            while (mStateDelegate.isConnect() && serverSocket?.isClosed == false) {
                try {
                    val accept = serverSocket!!.accept()
                    mMsgDelegate.addSocket(accept)
                    mStateDelegate.onNewConnect(accept)
                    readSocketData(accept)
                } catch (e: Exception) {
                    e.printStackTrace()
                    mStateDelegate.disConnect(e)
                }
            }
        }
        excutor?.execute {
            while (mStateDelegate.isConnect()) {
                sendMsg()
            }
        }
    }


    fun readSocketData(accept: Socket) {
        excutor?.execute {
            while (mStateDelegate.isConnect()) {
                try {
                    if (accept.isClosed) {
                        mMsgDelegate.removeSocket(accept)
                        break
                    }
                    val inputStream: InputStream = accept.getInputStream()
                    val buffer = ByteArray(10 * 1024)
                    var temp = 0
                    val strBuffer = StringBuffer()
                    while (inputStream.read(buffer).also { temp = it } != -1) {
                        strBuffer.append(String(buffer, 0, temp))

                        if (temp < buffer.size) {
                            break
                        }
                    }
                    val toObj = JsonUtils.convertJsonToObj("$strBuffer", Msg::class.java)
                    if (toObj == null) {
                        Log.e("TAG__Server_receiver_", "readSocketData: $strBuffer")
                        continue
                    }
                    mMsgDelegate.receiverMsg(toObj)

                } catch (e: Exception) {
                    e.printStackTrace()
                    mStateDelegate.disConnect(e)
                }
//                    Msg(
//                        Date(),
//                        User(accept.inetAddress.hostAddress),
//                        User(accept.inetAddress.hostAddress)
//                    )
            }
        }
    }

    @Synchronized
    fun sendMsg() {
        val popMsg = mMsgDelegate.popMsg() ?: return
        mMsgDelegate.sendSocketMsg(popMsg)
    }

    override fun getMsgDelegate(): OnMsgDelegate {
        return mMsgDelegate
    }

    override fun getStateDelegate(): OnStateDelegate {
        return mStateDelegate
    }

    override fun sendMsg(msg: Msg) {
        mMsgDelegate.sendMsg(msg)
    }

    override fun stop() {
        if (mStateDelegate.isConnect()) {
            if (excutor?.isShutdown == false) {
                excutor?.shutdown()
            }
            if (excutor?.awaitTermination(3, TimeUnit.SECONDS) == false) {
                excutor?.shutdownNow()
            }
            serverSocket?.close()
            mStateDelegate.disConnect()
        }
    }

    override fun addOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        mMsgDelegate.addOnMsgReceivedListener(msgReceiver)
    }

    override fun removeOnMsgReceivedListener(msgReceiver: OnMsgReceiverListener) {
        mMsgDelegate.removeOnMsgReceivedListener(msgReceiver)
    }

    override fun addOnStateListener(onStateListener: OnSocketStateListener) {
        mStateDelegate.addOnStateListener(onStateListener)
    }

    override fun removeOnStateListener(onStateListener: OnSocketStateListener) {
        mStateDelegate.removeOnStateListener(onStateListener)
    }

    override fun getLocalAddress(): String? {
        return NetworkUtils.getLocalIpAddress()
    }

    override fun getRemoteAddress(): String? {
        return null
    }

    override fun getServerAddress(): String? {
        return getLocalAddress()
    }
}