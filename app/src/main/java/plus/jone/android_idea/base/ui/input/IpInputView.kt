package plus.jone.android_idea.base.ui.input

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import plus.jone.android_idea.base.exts.dp

class IpInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private val inputViews: List<EditText>
    init {
        orientation = HORIZONTAL

        // Create and add four EditText views for each digit of the IP address
        inputViews = (1..4).map {
            EditText(context).apply {
                layoutParams = LayoutParams(0,WRAP_CONTENT).apply {
                    weight = 1f
                }
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setEms(3) // Set the width of each EditText to accommodate three digits
                gravity = Gravity.CENTER
                filters = arrayOf(InputFilter.LengthFilter(3)) // Limit each input to three digits
                setText("0")
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        if (s != null && s.isNotEmpty()) {
                            val num = s.toString().toInt()
                            if (num > 255) {
                                setText("255")
                                setSelection(text.length)
                            }
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                    }
                })
            }
        }

        // Add the EditText views to the layout with dot separators
        addView(inputViews[0])
        addView(createSeparatorView())
        addView(inputViews[1])
        addView(createSeparatorView())
        addView(inputViews[2])
        addView(createSeparatorView())
        addView(inputViews[3])
    }

    // Create a dot separator view between EditText views
    private fun createSeparatorView(): View {
        return TextView(context).apply {
            text = "."
            textSize = 24f
            setTextColor(ContextCompat.getColor(context, android.R.color.black))
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                setMargins(16.dp(context), 0, 16.dp(context), 0) // Add margin between each dot
            }
        }
    }

    fun setIpAddress(str:String){
        val split = str.split(".")
        if(split.size != 4){
            return
        }
        inputViews.forEachIndexed { index, editText ->
            editText.setText(split[index])
        }
    }


    // Get the IP address string
    fun getIpAddress(): String {
        return inputViews.joinToString(".") {
            it.text.toString()
        }
    }
}
