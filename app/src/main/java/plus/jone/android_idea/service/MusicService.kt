package plus.jone.android_idea.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import plus.jone.android_idea.R
import plus.jone.android_idea.service.model.MusicBinder
import plus.jone.android_idea.service.utils.MusicSateStore


class MusicService : Service(), MusicBinder.OnMusicChangedListener {
    companion object {
        const val MUSIC_CHANNEL_ID = "CHANNEL_DEFAULT_IMPORTANCE"
        const val COMMOND_NEXT = "music.service.to.next"
        const val COMMOND_TOGGLE = "music.service.to.toggle"
        const val COMMOND_PREVIOUS = "music.service.to.prev"
        const val COMMOND_PAUSE = "music.service.to.pause"
        const val COMMOND_START = "music.service.to.start"
    }

    private val binder = MusicBinder()
    private val mNotificationId = 1000
    private var remoteViews: RemoteViews? = null
    private var notification: Notification? = null
    override fun onBind(intent: Intent?): IBinder = binder


    @SuppressLint("RemoteViewLayout", "MissingPermission" )
    override fun onCreate() {
        super.onCreate()
        binder.setIsCircle(MusicSateStore.getMusicListPlayMode(this))

        createNotification()
        startForeground(mNotificationId, notification)
        binder.addChangeListener(this)
    }

    /**
     * create a music notification , it's used to interact with users
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MUSIC_CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_MIN
            )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
            NotificationCompat.Builder(this, MUSIC_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }
        remoteViews = RemoteViews(packageName, R.layout.notification_music)
        try {
            updateNotification()
            remoteViews!!.setOnClickPendingIntent(R.id.iv_next, createPendingIntent(COMMOND_NEXT))
            remoteViews!!.setOnClickPendingIntent(R.id.iv_play, createPendingIntent(COMMOND_TOGGLE))
            remoteViews!!.setOnClickPendingIntent(
                R.id.iv_prev,
                createPendingIntent(COMMOND_PREVIOUS)
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val toActivity = PendingIntent.getActivity(
            this,
            0x123,
            Intent(this, MusicServiceActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setSmallIcon(R.drawable.music).setCustomContentView(remoteViews).setOngoing(true).setSound(null)
            .setContentIntent(toActivity)
        notification = builder.build()
    }


    /**
     * create intent for notification view
     */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, MyNotificationBroadcaster::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * create the notification
     */
    @SuppressLint("MissingPermission")
    private fun updateNotification() {
        binder.currentSong()?.run {
            remoteViews?.setTextViewText(R.id.title, name)
            remoteViews?.setTextViewText(R.id.text, singer)
            remoteViews?.setImageViewBitmap(  R.id.image, LocalMusicUtils.getAlbumArtOver10(this@MusicService, albumId)  )
            remoteViews?.setImageViewResource(   R.id.iv_play,  if (binder.isPlaying()) R.drawable.pause else R.drawable.play )

            val managerCompat = NotificationManagerCompat.from(this@MusicService)
            managerCompat.notify(mNotificationId, notification!!)
           }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scHandler(intent)
        return START_STICKY
    }

    /**
     * 处理指令
     */
    private fun scHandler(intent: Intent?) {
        intent?.run {
            Log.d("TAG__", "onStartCommand: ${intent.action}")
            when (action) {
                COMMOND_NEXT -> binder.next()
                COMMOND_TOGGLE -> {
                    if (binder.isPlaying()) {
                        binder.pause()
                    } else {
                        binder.play()
                    }
                }
                COMMOND_PREVIOUS -> binder.prev()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        binder.removeChangeListener(this)
    }

    override fun onMusicChanged() {
        updateNotification()
    }
}