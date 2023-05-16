package plus.jone.android_idea.base.utills


import android.content.Context
import plus.jone.android_idea.R


object DisplayUtils {

    fun getStatusBarHeightCompat(context: Context): Int {
        var result = 0
        val resId: Int =
            context.getResources().getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            result = context.resources.getDimensionPixelOffset(resId)
        }
        if (result <= 0) {
            result = context.resources.getDimensionPixelOffset(R.dimen.dimen_25dp)
        }
        return result
    }

}