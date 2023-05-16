package plus.jone.android_idea.service.utils

import android.content.Context

object MusicSateStore {

    private const val PS_MUSIC_ = "music_player_"
    private const val MUSIC_LIST_PLAYER_MODE = "play_mode_"

    fun setMusicListPlayModel(ctx: Context, model: Boolean) {
        val preferences = ctx.getSharedPreferences(PS_MUSIC_, Context.MODE_PRIVATE)
        val edit = preferences.edit()
        edit.putBoolean(MUSIC_LIST_PLAYER_MODE, model)
        edit.apply()
    }



    fun getMusicListPlayMode(ctx: Context, defaultMode: Boolean =  true): Boolean {
        val preferences = ctx.getSharedPreferences(PS_MUSIC_, Context.MODE_PRIVATE)
        return preferences.getBoolean(MUSIC_LIST_PLAYER_MODE, defaultMode)
    }
}