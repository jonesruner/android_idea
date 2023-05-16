package plus.jone.android_idea.socket.socket

import android.util.Log
import com.google.gson.Gson
import jone.plus.socket.plus.base.*
import jone.plus.socket.plus.model.Msg
import plus.jone.android_idea.base.utills.JsonUtils
import plus.jone.android_idea.base.utills.NetworkUtils
import java.io.InputStream
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.Exception
import kotlin.concurrent.thread

class JClient(val remoteHost: String, val remotePort: Int) : JChat {
    private var socket: Socket? = null
    private var excutor: ExecutorService? = null
    private var mStateDelegate = OnStateDelegate()
    private var mMsgDelegate = OnMsgDelegate()

    override fun start() {
        val thread = thread {
            mStateDelegate.prepare()
            try {
                socket = Socket(remoteHost, remotePort)
                socket!!.soTimeout = 200
                excutor = Executors.newCachedThreadPool()
                mStateDelegate.connect()
                listener()
            } catch (e: Exception) {
                e.printStackTrace()
                mStateDelegate.disConnect()
            }
        }
        Thread.sleep(3000)
        if (socket == null || socket?.isConnected == false) {
            thread.interrupt()
            mStateDelegate.disConnect()
        }
    }

    private fun listener() {
        mMsgDelegate.addSocket(socket!!)
        thread {
            while (mStateDelegate.isConnect()) {
                readSocketData()
            }
        }
        excutor?.execute {
            while (mStateDelegate.isConnect()) {
                sendMsg()
            }
        }
    }

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

    private fun readSocketData() {
        try {
            val inputStream: InputStream = socket!!.getInputStream()
            val buffer = ByteArray(10 * 1024)
            var temp = 0
            val strBuffer = StringBuffer()
            // 设置读取超时时间为 3 秒
            while (inputStream.read(buffer).also { temp = it } != -1) {
                strBuffer.append(String(buffer, 0, temp))
                if (temp < buffer.size) {
                    break
                }
            }
            val toObj = JsonUtils.convertJsonToObj("$strBuffer", Msg::class.java)
            println(toObj)
            if (toObj == null) {
                Log.d( "TAG__Server_receiver_"," readSocketData: $strBuffer")
            } else {
                mMsgDelegate.receiverMsg(toObj)
            }
        }catch (_:Exception){}
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
            mMsgDelegate.releaseAllSocket()
            socket?.close()
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
        return remoteHost
    }

    override fun getServerAddress(): String? {
        return remoteHost
    }
}