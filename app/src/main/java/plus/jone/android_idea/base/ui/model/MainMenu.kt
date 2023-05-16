package plus.jone.android_idea.base.ui.model

import android.app.Activity

data class MainMenu(
    val title: String,
    val action: Class<out Activity>,
    val brief:String,
    val icon:Int,
)
    