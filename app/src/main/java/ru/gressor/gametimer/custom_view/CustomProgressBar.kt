package ru.gressor.gametimer.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ru.gressor.gametimer.R
import kotlin.math.abs

class CustomProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defAttrs: Int = 0
) : View(context, attrs, defAttrs) {
    private lateinit var paint: Paint
    private var color: Int = Color.CYAN
        set(value) {
            field = value
            paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 20f
                this.color = value
            }
        }
    var startValue: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    var finishValue: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    var currentValue: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, 0, 0)
            .apply {
                try {
                    startValue = getFloat(R.styleable.CustomProgressBar_startValue, startValue)
                    finishValue = getFloat(R.styleable.CustomProgressBar_startValue, finishValue)
                    currentValue = getFloat(R.styleable.CustomProgressBar_startValue, currentValue)
                    color = getColor(R.styleable.CustomProgressBar_tint, color)
                } finally {
                    recycle()
                }
            }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            val sweepAngle = abs(360 * (currentValue - startValue) / (finishValue - startValue))
            drawArc(25f, 25f, width.toFloat() - 25, height.toFloat() - 25,
                -90f, sweepAngle, false, paint)
        }
    }
}