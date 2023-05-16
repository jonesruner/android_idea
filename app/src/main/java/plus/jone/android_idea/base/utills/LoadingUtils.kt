package plus.jone.android_idea.base.utills

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import plus.jone.android_idea.R
import plus.jone.android_idea.databinding.LoadingLayoutBinding

object LoadingUtils {

    fun showLoading(context:Activity): Dialog {
        val dialog = Dialog(context)
        dialog.run {
            val layoutBinding = LoadingLayoutBinding.inflate(context.layoutInflater)
            setContentView(layoutBinding.root)
            window?.setLayout(WRAP_CONTENT,WRAP_CONTENT)
            setCancelable(false)
            window?.attributes?.windowAnimations = R.style.animation

            val animation = AnimationUtils.loadAnimation(context, R.anim.anim_loading)
            layoutBinding.root.animation = animation
            animation.start()
        }
        dialog.show()
        return dialog
    }
}