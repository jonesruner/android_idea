package plus.jone.android_idea.service.model

data class JSong(
    var name    : String? = null,//歌曲名
    var singer: String? = null,  //歌手
    var size : Long = 0,//歌曲所占空间大小
    var duration: Int = 0, //歌曲时间长度
    var path  : String? = null,//歌曲地址
    var albumId  : Long = 0,//图片id
    var id   : Long = 0,//歌曲id
    var url : String? = null,//
    var songType:Int = 0,//本地 获取 远程
)
