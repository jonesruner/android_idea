package plus.jone.android_idea.anim

import android.animation.AnimatorSet
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import plus.jone.android_idea.R
import plus.jone.android_idea.databinding.ActivityAnimaBinding

class AnimaActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityAnimaBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val alpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha)
        val rotate = AnimationUtils.loadAnimation(this, R.anim.anim_rotate)
        val translate = AnimationUtils.loadAnimation(this, R.anim.anim_translate)
        val scale = AnimationUtils.loadAnimation(this, R.anim.anim_scale)

        binding.animAlpha.setOnClickListener {
            startAnim(alpha)
        }
        binding.animRotate.setOnClickListener {
            startAnim(rotate)
        }
        binding.animTranslate.setOnClickListener {
            startAnim(translate)
        }
        binding.animScale.setOnClickListener {
            startAnim(scale)
        }
        binding.animSet.setOnClickListener {
            val anim = AnimationSet(false).apply {
                addAnimation(alpha)
                addAnimation(rotate)
                addAnimation(translate)
                addAnimation(scale)
            }
            binding.animImg.animation?.cancel()
            binding.animImg.startAnimation(anim)
        }
    }

    private fun startAnim(alpha: Animation) {
        binding.animImg.animation?.cancel()
        binding.animImg.startAnimation(alpha)
    }
}