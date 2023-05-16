package plus.jone.android_idea.service

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import plus.jone.android_idea.R
import plus.jone.android_idea.service.model.Song
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream


object LocalMusicUtils {
    //定义一个集合，存放从本地读取到的内容
    var list: MutableList<Song> = mutableListOf()
    var song: Song? = null

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
                    name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)),
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                    singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)),
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
                            val str = name!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
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

    //    转换歌曲时间的格式
    fun formatTime(time: Int): String {
        if (time / 1000 % 60 < 10) {
            return (time / 1000 / 60).toString() + ":0" + (time / 1000 % 60)
        } else {
            return (time / 1000 / 60).toString() + ":" + (time / 1000 % 60)
        }
    }

    /**
     * 获取专辑封面位图对象
     * @param context
     * @param song_id
     * @param album_id
     * @param allowdefalut
     * @return
     */
    fun getArtwork(
        context: Context,
        song_id: Long,
        album_id: Long,
        allowdefalut: Boolean,
        small: Boolean
    ): Bitmap? {
        if (album_id < 0) {
            if (song_id < 0) {
                val bm = getArtworkFromFile(context, song_id, -1)
                if (bm != null) {
                    return bm
                }
            }
            return if (allowdefalut) {
                getDefaultArtwork(context, small)
            } else null
        }
        val res: ContentResolver = context.getContentResolver()
        val uri: Uri = ContentUris.withAppendedId(albumArtUri, album_id)
        if (uri != null) {
            var `in`: InputStream? = null
            try {
                `in` = res.openInputStream(uri)
                val options = BitmapFactory.Options()
                //先制定原始大小
                options.inSampleSize = 1
                //只进行大小判断
                options.inJustDecodeBounds = true
                //调用此方法得到options得到图片的大小
                BitmapFactory.decodeStream(`in`, null, options)
                /** 我们的目标是在你N pixel的画面上显示。 所以需要调用computeSampleSize得到图片缩放的比例  */
                /** 这里的target为800是根据默认专辑图片大小决定的，800只是测试数字但是试验后发现完美的结合  */
                if (small) {
                    options.inSampleSize = computeSampleSize(options, 40)
                } else {
                    options.inSampleSize = computeSampleSize(options, 600)
                }
                // 我们得到了缩放比例，现在开始正式读入Bitmap数据
                options.inJustDecodeBounds = false
                options.inDither = false
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                `in` = res.openInputStream(uri)
                return BitmapFactory.decodeStream(`in`, null, options)
            } catch (e: FileNotFoundException) {
                var bm = getArtworkFromFile(context, song_id, album_id)
                if (bm != null) {
                    if (bm.config == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false)
                        if (bm == null && allowdefalut) {
                            return getDefaultArtwork(context, small)
                        }
                    }
                } else if (allowdefalut) {
                    bm = getDefaultArtwork(context, small)
                }
                return bm
            } finally {
                try {
                    if (`in` != null) {
                        `in`.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * 从文件当中获取专辑封面位图
     * @param context
     * @param songid
     * @param albumid
     * @return
     */
    private fun getArtworkFromFile(context: Context, songid: Long, albumid: Long): Bitmap? {
        var bm: Bitmap? = null
        if (albumid < 0 && songid < 0) {
            throw IllegalArgumentException("Must specify an album or a song id")
        }
        try {
            val options = BitmapFactory.Options()
            var fd: FileDescriptor? = null
            if (albumid < 0) {
                val uri: Uri = Uri.parse(
                    "content://media/external/audio/media/"
                            + songid + "/albumart"
                )
                val pfd: ParcelFileDescriptor? =
                    context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            } else {
                val uri: Uri = ContentUris.withAppendedId(albumArtUri, albumid)
                val pfd: ParcelFileDescriptor? =
                    context.contentResolver.openFileDescriptor(uri, "r")
                if (pfd != null) {
                    fd = pfd.fileDescriptor
                }
            }
            options.inSampleSize = 1
            // 只进行大小判断
            options.inJustDecodeBounds = true
            // 调用此方法得到options得到图片大小
            BitmapFactory.decodeFileDescriptor(fd, null, options)
            // 我们的目标是在800pixel的画面上显示
            // 所以需要调用computeSampleSize得到图片缩放的比例
            options.inSampleSize = 100
            // 我们得到了缩放的比例，现在开始正式读入Bitmap数据
            options.inJustDecodeBounds = false
            options.inDither = false
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            //根据options参数，减少所需要的内存
            bm = BitmapFactory.decodeFileDescriptor(fd, null, options)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bm
    }

    /**
     * 获取默认专辑图片
     * @param context
     * @return
     */
    @SuppressLint("ResourceType")
    fun getDefaultArtwork(context: Context, small: Boolean): Bitmap? {
        val opts = BitmapFactory.Options()
        opts.inPreferredConfig = Bitmap.Config.RGB_565
        if (small) {    //返回小图片
            //return
            BitmapFactory.decodeStream(
                context.resources.openRawResource(R.drawable.music),
                null,
                opts
            )
        }
        //return BitmapFactory.decodeStream(context.getResources().openRawResource(R.drawable.defaultalbum), null, opts);
        return null
    }

    /**
     * 对图片进行合适的缩放
     * @param options
     * @param target
     * @return
     */
    fun computeSampleSize(options: BitmapFactory.Options, target: Int): Int {
        val w = options.outWidth
        val h = options.outHeight
        val candidateW = w / target
        val candidateH = h / target
        var candidate = Math.max(candidateW, candidateH)
        if (candidate == 0) {
            return 1
        }
        if (candidate > 1) {
            if ((w > target) && (w / candidate) < target) {
                candidate -= 1
            }
        }
        if (candidate > 1) {
            if ((h > target) && (h / candidate) < target) {
                candidate -= 1
            }
        }
        return candidate
    }

    //获取歌曲封面信息
    fun getAlbumBitmap(context: Context, album_id: Int): Bitmap? {
        //Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), album_id);
        val uri = "content://media/external/audio/albums/" + Integer.toString(album_id)
        var album_art: String? = null
        var bm: Bitmap? = null
        val album_cursor = context.contentResolver.query(
            Uri.parse(uri), arrayOf("album_art"), null, null, null
        ) //contentUri
        if (album_cursor!!.count > 0 && album_cursor.columnCount > 0) {
            album_cursor.moveToNext()
            album_art = album_cursor.getString(0)
        }
        album_cursor.close()
        bm = if (album_art != null) {
            BitmapFactory.decodeFile(album_art)
        } else {
            //bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_cover);
            return null
        }
        return bm
    }


    /**
     * 根据专辑ID获取专辑封面图
     * @param album_id 专辑ID
     * @return
     */
    fun getAlbumArt(context: Context, album_id: Long): String? {
        val mUriAlbums = "content://media/external/audio/albums"
        val projection = arrayOf("album_art")
        val cur: Cursor? = context.contentResolver.query(
            Uri.parse(mUriAlbums + "/" + java.lang.Long.toString(album_id)),
            projection,
            null,
            null,
            null
        )
        var album_art: String? = null
        if (cur != null) {
            if (cur.count > 0 && cur.columnCount > 0) {
                cur.moveToNext()
                album_art = cur.getString(0)
            }
            cur.close()
        }
        var path: String? = null
        if (album_art != null) {
            path = album_art
        } else {
            //path = "drawable/music_no_icon.png";
            //bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_cover);
        }
        return path
    }


    @SuppressLint("Range")
    fun getAlbumArtOver10(context: Context, albumId: Long): Bitmap? {
        val albumArtUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            albumId
        )
        var inputStream: InputStream? = null
        try {
            inputStream = context.contentResolver.openInputStream(albumArtUri)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return if (inputStream != null) {
            BitmapFactory.decodeStream(inputStream)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.music)
        }
    }
}