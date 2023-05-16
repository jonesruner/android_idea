package plus.jone.android_idea.service.model

import android.os.Parcel
import android.os.Parcelable

data class Song(
    var name    : String? = null,//歌曲名
    var singer: String? = null,  //歌手
    var size : Long = 0,//歌曲所占空间大小
    var duration: Int = 0, //歌曲时间长度
    var path  : String? = null,//歌曲地址
    var albumId  : Long = 0,//图片id
    var id   : Long = 0,//歌曲id
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(singer)
        parcel.writeLong(size)
        parcel.writeInt(duration)
        parcel.writeString(path)
        parcel.writeLong(albumId)
        parcel.writeLong(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }


}