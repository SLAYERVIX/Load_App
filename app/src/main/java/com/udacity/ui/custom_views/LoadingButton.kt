package com.udacity.ui.custom_views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.ButtonState
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textWidth = 0f

    private var textSize: Float = resources.getDimension(R.dimen.default_text_size)
    private var circleXOffset = textSize / 2

    private var buttonTitle: String

    private var progressWidth = 0f
    private var progressCircle = 0f

    private var buttonColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var loadingColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
    private var circleColor = ContextCompat.getColor(context, R.color.colorAccent)

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Clicked -> {
                buttonTitle = "Clicked"
                invalidate()
            }
            ButtonState.Loading -> {
                buttonTitle = resources.getString(R.string.button_loading)
                valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
                valueAnimator.duration = 5000
                valueAnimator.addUpdateListener { animation ->
                    progressWidth = animation.animatedValue as Float
                    progressCircle = (widthSize.toFloat()/365)*progressWidth
                    invalidate()
                }
                valueAnimator.addListener(object : AnimatorListenerAdapter(){
                    override fun onAnimationEnd(animation: Animator) {
                        progressWidth = 0f
                        if(buttonState == ButtonState.Loading){
                            buttonState = ButtonState.Loading
                        }
                    }
                })
                valueAnimator.start()

            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                progressWidth = 0f
                progressCircle = 0f
                buttonTitle = resources.getString(R.string.button_download)
                invalidate()
            }
        }

    }

    private val paint = Paint().apply {
        isAntiAlias = true
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    init {
        buttonTitle = "Download"
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingColor = getColor(R.styleable.LoadingButton_buttonLoadingColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBackgroundColor(canvas)
        drawProgressBackground(canvas)
        drawTitle(canvas)
        drawCircleProgress(canvas)
    }


    // Draw button background color
    private fun drawBackgroundColor(canvas: Canvas?) {
        paint.color = buttonColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    // Draw Progress background
    private fun drawProgressBackground(canvas: Canvas?) {
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, progressWidth, heightSize.toFloat(), paint)
    }

    // Draw Circular progress
    private fun drawCircleProgress(canvas: Canvas?) {
        canvas?.save()
        canvas?.translate(widthSize / 2 + textWidth / 2 + circleXOffset, heightSize / 2 - textSize / 2)
        paint.color = circleColor
        canvas?.drawArc(RectF(0f, 0f, textSize, textSize), 0F, progressCircle * 0.365f, true,  paint)
        canvas?.restore()
    }

    // Draw title
    private fun drawTitle(canvas: Canvas?) {
        paint.color = Color.WHITE
        textWidth = paint.measureText(buttonTitle)
        canvas?.drawText(buttonTitle, widthSize / 2 - textWidth / 2, heightSize / 2 - (paint.descent() + paint.ascent()) / 2, paint)
    }

    // Measure view to determine its width and height
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
}