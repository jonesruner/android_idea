package plus.jone.android_idea.base.exts

import android.view.View
import android.view.View.OnClickListener

fun View.setLetterTrafficClickListener(onClickListener: OnClickListener) {
    var time = 0L
    setOnClickListener {
        if (System.currentTimeMillis().minus(time) > 500) {
            onClickListener.onClick(it)
        }
        time = System.currentTimeMillis()
    }
}