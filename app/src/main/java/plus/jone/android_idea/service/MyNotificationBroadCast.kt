package plus.jone.android_idea.service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * 接收 music notification 发送的指令 转发给 music service
 */
class MyNotificationBroadcaster : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG_", "onReceive: ${intent?.action}")
        context?.let { ctx ->
            intent?.let { i ->
                // 发送指令到服务
                ctx.startService(Intent(ctx, MusicService::class.java).apply {
                    action = i.action
                })
            }
        }
    }
}