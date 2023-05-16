package plus.jone.android_idea.base.ui.iconfont

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class IconFontButton : MaterialButton {

    public  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int )
    :super(context,attrs,defStyleAttr)
    {
        var typeface :Typeface? = null;
        try{
            typeface = Typeface.createFromAsset(context.assets,"font/iconfont.ttf")
        }catch (e:java.lang.Exception){
            e.printStackTrace()
        }
        if(typeface!=null){
            setTypeface(typeface)
        }
    }

    public constructor(context: Context,attrs: AttributeSet?):this  (context,attrs,-1)

    public constructor(context: Context):this(context,null)


}