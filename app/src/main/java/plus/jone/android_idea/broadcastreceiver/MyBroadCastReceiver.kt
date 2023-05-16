package plus.jone.android_idea.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class MyBroadCastReceiver : BroadcastReceiver() {
    interface OnReceiver{
        fun onReceive(context: Context?, intent: Intent?)
    }
    private var onReceiver: OnReceiver? = null

    fun setOnReceiver(onReceiver: OnReceiver?){
        this.onReceiver = onReceiver
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        onReceiver?.onReceive(context,intent)
    }
}