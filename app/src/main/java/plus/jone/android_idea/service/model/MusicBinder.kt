package plus.jone.android_idea.service.model

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import plus.jone.android_idea.IMusicRemoteServiceAidl
import plus.jone.android_idea.service.utils.MusicSateStore

/**
 * 用于管理基本的 mediaplayer 方法
 */
class MusicBinder() : IMusicRemoteServiceAidl.Stub() {
    interface OnMusicChangedListener {
        fun onMusicChanged()
    }
    companion object {
        private var audio:MediaPlayer? = null
    }
    private var state:MusicState = MusicState.INITIALIZED
    private var currentListName = ""
    private val songList = mutableListOf<JSong>()
    private var pos = -1
    private val listenerList  = mutableListOf<OnMusicChangedListener>()
    private var isCircle = true
    fun isCircle () = isCircle
    fun setIsCircle(circle:Boolean){
        if(circle==isCircle)return
        isCircle = circle
        dispatchChange()
    }
    fun  addChangeListener(onmusicChangedListener:OnMusicChangedListener){
        listenerList.add(onmusicChangedListener)
    }
    fun  removeChangeListener(onmusicChangedListener:OnMusicChangedListener){
        listenerList.remove(onmusicChangedListener)
    }
    fun dispatchChange(){
        listenerList.forEach {
            it.onMusicChanged()
        }
    }
    fun isCurrrentSongList(name: String): Boolean {
        return name == currentListName
    }
    fun songListToggle(listName:String , songs:List<JSong>,togglePos:Int = 0){
        pause()
        stop()
        songList.clear()
        songList.addAll(songs)
        this.currentListName = listName
        toggle(togglePos)
    }
    override fun play() {
        if (audio?.isPlaying  == false){
            audio!!.start()
            state = MusicState.PLAYING
            dispatchChange()
        }
    }
    override fun pause() {
        if (audio?.isPlaying  == true) {
            audio!!.pause()
            state = MusicState.PAUSED
            dispatchChange()
        }
    }
    fun isPlaying(): Boolean = state.ordinal == MusicState.PLAYING.ordinal
    override fun state():Int= state.ordinal
    override fun toggle(position:Int ){
        // 定位
        if (position == -1 || songList.size == 0 ) return
        if(position >= songList.size)return
        pos = position
        stop()
        val jSong = songList[pos]
        audio = MediaPlayer()
        audio!!.setOnCompletionListener {
            dispatchChange()
            stop()
            if(isCircle){
                next()
            }
        }
        audio!!.setAudioAttributes(AudioAttributes.Builder().apply { this.setLegacyStreamType(AudioManager.STREAM_MUSIC) }.build())
        //TODO: 需要适配 Android Q 带来的存储空间的变化
        audio!!.setDataSource(jSong.path)
        audio!!.prepareAsync()
        audio!!.setOnPreparedListener {
            play()
        }
        Log.d("TAG__", "toggle: ")
    }
    override fun stop() {
       try{
           if (audio?.isPlaying == true) {
               audio?.pause()
           }
           audio?.reset()
           audio?.stop()
           audio?.release()
       }catch (e:IllegalStateException){
           e.printStackTrace()
       }
        state = MusicState.INITIALIZED
        dispatchChange()
    }
    override fun next() {
        if(songList.size == 0 )return
        pos = (pos+1) % songList.size
        toggle(pos)
    }
    override fun prev() {
        if(songList.size == 0 )return
        pos = (songList.size+pos-1) % songList.size
        toggle(pos)
    }
    override fun volumeUp() {}
    override fun volumeDown() {}
    fun currentSong():JSong? {
        if(pos<0 || pos>songList.size || songList.size == 0)return null
        return songList[pos]
    }
}