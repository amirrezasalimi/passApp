package ir.amirsalimi.passapp.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.input_view.view.*


class passwordCheck
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    var circle: Paint
    var statePoint: Paint
    var state: String = "ضعیف"
    var stateColor: String = "#F63E50"
        set(value){
            circle.color= Color.parseColor(value)
        }

    init {
        val params = LinearLayout.LayoutParams(68, 68)
        //params.setMargins(3,3,3,30)
        params.leftMargin = 5
        layoutParams = params
        circle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor(stateColor)
            style = Paint.Style.FILL
        }
        statePoint = TextPaint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            textSize = 17f
            textAlign = Paint.Align.CENTER
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
            val x = width / 2f
            val y = (((height / 2) - ((statePoint.descent() + statePoint.ascent()) / 2)))
            canvas.drawCircle(x, height / 2f, 30f, circle)
            canvas.drawText(state, x, y, statePoint)
    }
}