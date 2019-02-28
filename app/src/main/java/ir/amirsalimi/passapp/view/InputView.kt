package ir.amirsalimi.passapp.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import ir.amirsalimi.passapp.R
import kotlinx.android.synthetic.main.input_view.view.*
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.MotionEvent
import ir.amirsalimi.passapp.R.attr.type
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager
import android.support.v4.content.ContextCompat.getSystemService
import android.os.SystemClock
import android.view.Gravity


class InputView(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    var activeTitle: Boolean = true

    init {
        inflate(context, R.layout.input_view, this)
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.InputView)
//        imageView.setImageDrawable(attributes.getDrawable(R.styleable.BenefitView_image))

        attributes.getString(R.styleable.InputView_text).let {
            input.setText(it)
        }
        attributes.getString(R.styleable.InputView_title).let {
            title.text = it
        }
        attributes.getString(R.styleable.InputView_hint).let {
            input.hint=it
        }
        attributes.getString(R.styleable.InputView_type).let {
            if (it == "password") {
                passwordMod(true)
            }
        }
        attributes.getString(R.styleable.InputView_message).let {
            message.visibility = View.VISIBLE
            message.text = it
        }
        attributes.getString(R.styleable.InputView_title_background)?.let {
            title.setBackgroundColor(Color.parseColor(it))
        }


        val fixedTitle:Boolean=attributes.getBoolean(R.styleable.InputView_fixed_title,false)
        val rtl:Boolean=attributes.getBoolean(R.styleable.InputView_rtl,true)

        if(rtl){
            input.gravity = Gravity.START
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                input.textAlignment= View.TEXT_ALIGNMENT_VIEW_END
            }
        }else{
            input.gravity = Gravity.END
        }
        if(fixedTitle) {
            val params = title.layoutParams as ViewGroup.MarginLayoutParams
            title.textSize=13f
            title.setTextColor(Color.parseColor("#414141"))
            params.topMargin = 0
            title.layoutParams = params
        }else{
            input.setOnTouchListener { arg0, arg1 ->
                if (input.text.isEmpty()) {
                    toggleTitle(true)
                }
                false
            }
            input.setOnFocusChangeListener { v: View, type: Boolean ->
                input.isCursorVisible = type
                if (!type) {
                    if (input.text.isEmpty()) {
                        toggleTitle(false)
                    }
                }
            }
            title.setOnClickListener {
                if (!activeTitle) {
                    input.requestFocus()
                    input.dispatchTouchEvent(
                        MotionEvent.obtain(
                            SystemClock.uptimeMillis(),
                            SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_UP,
                            0f,
                            0f,
                            0
                        )

                    )

                    toggleTitle(true)
                }
            }
            if (input.text.isEmpty()) {
                activeTitle = false
                val params = title.layoutParams as ViewGroup.MarginLayoutParams
                params.topMargin = 31
                title.layoutParams = params
            }
        }
        attributes.recycle()
    }
    fun setMessage(text:String,color:Int?=null){
        if(text==""){
            message.text = text
            //  message.visibility = View.GONE
        }else{
            color.let{
                message.setTextColor(color as Int)
            }
          //  message.visibility = View.VISIBLE
            message.text = text
        }


    }

    fun passwordMod(ye: Boolean) {
        if (ye) {
            input?.transformationMethod = PasswordTransformationMethod.getInstance()
        } else {
            input?.transformationMethod = null
        }
    }

    fun setType(type: Int) {
        input?.inputType = type
    }

    fun addView(view: View, type: String) {
        val parent: ViewGroup = if (type == "right") {
            rightItems as ViewGroup
        } else {
            leftItems as ViewGroup
        }
        parent.addView(view)
    }

    fun text(): String {
        return input.text.toString()
    }

    fun toggleTitle(active: Boolean) {
        if (activeTitle && active) {
            return
        }
        activeTitle = active
        if(active){
            title.textSize=13f
            title.setTextColor(Color.parseColor("#414141"))
        }else{
            title.setTextColor(Color.parseColor("#5E5E5E"))
            title.textSize=15f
        }
        ValueAnimator.ofInt(if (active) 31 else 0, if (active) 0 else 31).apply {
            addUpdateListener {
                val params = title.layoutParams as ViewGroup.MarginLayoutParams
                // interpolate the proper value
                params.topMargin = it.animatedValue as Int
                title.layoutParams = params
            }
            duration = 150L
            interpolator = LinearInterpolator()
            start()
        }

    }

}