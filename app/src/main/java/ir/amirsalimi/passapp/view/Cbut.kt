package ir.amirsalimi.passapp.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import ir.amirsalimi.passapp.R
import kotlinx.android.synthetic.main.input_view.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.View.OnLayoutChangeListener


class Cbut(context: Context, attrs: AttributeSet) : RelativeLayout(context, attrs) {
    var sides: MutableList<Any> = mutableListOf()
    var bigBorder = 0
    var bgColor: String? = null

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Cbut)
        attributes.getString(R.styleable.Cbut_sides).let {
            val _sides = it.split("|")
            if (_sides.isNotEmpty()) {
                _sides.forEachIndexed { i, element ->
                    val _side = _sides[i].split(":")
                    try {
                        val key = _side[0]
                        val vals = _side[1].split(",")
                        val wid:Int=vals[0].toInt()
                        Log.d("test:","index $i width is $wid")
                        if(wid>bigBorder){
                            bigBorder=wid
                        }
                        sides.add(arrayListOf(key, vals))
                    } catch (ex: Exception) {
                    }
                }

            }


        }
        bgColor = attributes.getString(R.styleable.Cbut_bg)
        var _bigBorder = attributes.getInt(R.styleable.Cbut_border, 0)
        if(_bigBorder>bigBorder){
            bigBorder=_bigBorder
        }
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override
            fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                removeOnLayoutChangeListener(this)

                var baseWidth=width-(bigBorder)
                var baseHeigh=height-(bigBorder)
                Log.d("test:", "bigger border:$bigBorder basewidth:$baseWidth ,baseHeight:$baseHeigh ,width:$width ,height:$height")


                sides.forEach { _side ->
                    val onSide = _side as List<*>
                    val key = onSide[0].toString()
                    val vals = onSide[1] as List<*>
                    val ws: Int = vals[0].toString().toInt()
                    val color = vals[1] as String
                    if(ws>0) {
                        val back_layer=RelativeLayout(context)
                        addView(back_layer)
                        back_layer.setBackgroundColor(Color.parseColor(color))
                        val params = back_layer.layoutParams as RelativeLayout.LayoutParams
                        params.width=baseWidth
                        params.height=baseHeigh
                        params.topMargin=bigBorder
                        params.leftMargin=bigBorder

                        if(key=="bottom"){
                            params.topMargin+=ws
                        }
                        if(key=="top"){
                            params.bottomMargin+=ws
                        }
                        if(key=="left"){
                            params.rightMargin+=ws
                        }
                        if(key=="right"){
                            params.leftMargin+=ws
                        }


                        Log.d("test:",params.toString())

                        Log.d("test:","create border size:"+ws.toString()+" | color"+color+" side:"+key)
                        back_layer.layoutParams=params
                    }
                }

                /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                     baseParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
                 }
                 baseParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                 */


                val base = RelativeLayout(context)
                base.setBackgroundColor(Color.parseColor(bgColor))
             //   setBackgroundColor(Color.parseColor("#00AAA3"))


                val nw = width + bigBorder
                val nh = height + bigBorder
                val me = layoutParams
                me.width = nw
                me.height = nh
                layoutParams = me

                addView(base)
                val baseParams = base.layoutParams as RelativeLayout.LayoutParams
                baseParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
                baseParams.width = baseWidth
                baseParams.height = baseHeigh
                base.layoutParams = baseParams

                Log.d("test:", width.toString())
                Log.d("test:", height.toString())
            }
        })

        attributes.recycle()
    }


}