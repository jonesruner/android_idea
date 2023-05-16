## Music Service

## 1. [从本地获取 音乐](https://www.jianshu.com/p/64172861f9ef)

> 从本地文件中获取 **Song** 是音乐开始的地方。

```kotlin
//获取专辑封面的Uri
private val albumArtUri: Uri = Uri.parse("content://media/external/audio/albumart")
fun getmusic(context: Context): List<Song> {
    val cursor: Cursor? = context.contentResolver.query(
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        null,
        null,
        null,
        MediaStore.Audio.Media.DEFAULT_SORT_ORDER
    )
    if (cursor != null) {
        while (cursor.moveToNext()) {
            song = Song(
                name =  cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                singer =  cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)),
                duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)),
                size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)),
                albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))
            )
            //list.add(song);
            //把歌曲名字和歌手切割开
            //song.setName(name);
            song?.run {
                if (size > 1000 * 800) {
                    if (name!!.contains("-")) {
                        val str = name!!.split("-".toRegex()).dropLastWhile { it.isEmpty() } .toTypedArray()
                        singer = str[0]
                        name = str[1] 
                    } else {
                        song!!.name = name
                    }
                    list.add(song!!)
                }
            }
        }
    }
    cursor?.close()
    return list
}
```

需要声明文件存储操作权限

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```



## 2. 新建 Service

> 使得音乐可以一直保持在前台服务，用户可以退出该页面，音乐也不会停止

在Service中得创建一个 Notification, 用于展现播放状态，以及提供基本的音频切换操作。

```kotlin
	/**
	* 其中重要的就是 remoteview 的一个设置，初始了点击事件
	* 自定义 notification 布局样式
	*/
	@SuppressLint("UnspecifiedImmutableFlag")
    private fun createNotification() {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MUSIC_CHANNEL_ID,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT
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
            remoteViews!!.setOnClickPendingIntent( R.id.iv_prev, createPendingIntent(COMMOND_PREVIOUS))
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
        builder.setSmallIcon(R.drawable.music).setCustomContentView(remoteViews).setOngoing(true)
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
            remoteViews?.setImageViewBitmap(  R.id.image, 		LocalMusicUtils.getAlbumArtOver10(this@MusicService, albumId)  )
            remoteViews?.setImageViewResource(   R.id.iv_play,  if (binder.isPlaying()) R.drawable.pause else R.drawable.play )

            val managerCompat = NotificationManagerCompat.from(this@MusicService)
            managerCompat.notify(mNotificationId, notification!!)
           }
    }
```

需要声明前台服务许可

```
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```



## 新建 BroadcastReceiver

> 新建该广播主要用于去监听 notification 发出的事件，转发给服务进行处理

```kotlin
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
```

**记得在Manifest 声明 receiver** 

```xml
<receiver android:name=".service.MyNotificationBroadcaster" />
```

