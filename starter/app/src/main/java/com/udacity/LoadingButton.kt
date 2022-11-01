package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressValue = 0

    private var customButtonText = ""
    private var buttonBackgroundColor = Color.BLACK
    private var buttonLoadingBackgroundColor = Color.BLUE

    private var valueAnimator = ValueAnimator()
    private var paint = Paint()
    private val bounds = Rect()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        customButtonText = if (new == ButtonState.Loading) {
            valueAnimator.start()
            resources.getString(R.string.button_loading)
        } else {
            valueAnimator.cancel()
            progressValue = 0
            resources.getString(R.string.button_name)
        }
        invalidate()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton, defStyleAttr, 0) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, 0)
            buttonLoadingBackgroundColor =
                getColor(R.styleable.LoadingButton_buttonLoadingBackgroundColor, 0)
        }
        buttonState = ButtonState.Completed
        invalidate()

        // initializing value animator
        valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(4000).apply {
            addUpdateListener {
                progressValue = it.animatedValue as Int
                invalidate()
            }

            interpolator = LinearInterpolator()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
        }

        paint.isAntiAlias = true
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)


        paint.color = buttonLoadingBackgroundColor
        canvas?.drawRect(0f, 0f, width * progressValue / 360f, height.toFloat(), paint)

        paint.color = Color.WHITE
        canvas?.drawArc(
            width-100f,
            height/2f - 40f,
            width-20f,
            height/2f + 40f,
            0f,
            progressValue.toFloat(),
            true,
            paint
        )

        // get bounds of the text to center it
        paint.getTextBounds(customButtonText, 0, customButtonText.length, bounds)

        paint.textSize = 60f
        canvas?.drawText(customButtonText, widthSize / 2f, height / 2f + bounds.height() / 2f - bounds.bottom
            , paint)
    }
}