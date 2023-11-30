package ru.dima.aston_intensiv_2

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import kotlin.math.abs
import kotlin.random.Random

class RainbowDrumView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private val sectorColors = arrayOf(
        context.resources.getColor(R.color.red, context.theme),
        context.resources.getColor(R.color.orange, context.theme),
        context.resources.getColor(R.color.yellow, context.theme),
        context.resources.getColor(R.color.green, context.theme),
        context.resources.getColor(R.color.blue, context.theme),
        context.resources.getColor(R.color.dk_blue, context.theme),
        context.resources.getColor(R.color.purple, context.theme),
    )

    var spinning = false
        private set
    var currentAngle = 0f
        private set

    private var textToShow: String? = null
    private var bitmapToShow: Bitmap? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    companion object {
        const val MAX_ANGLE = 2000

        val BLUE_SECTOR = 11f..62f
        val YELLOW_SECTOR = 115f..166f
        val RED_SECTOR = 219f..270f
        val PURPLE_SECTOR = 271f..322f
    }

    fun startSpinning(
        endAngle: Float = abs((Random.nextInt() % MAX_ANGLE)).toFloat() + 360,
        textToDraw: String? = null,
        pictureToDraw: Bitmap? = null,
        onText: (String?) -> Unit = {},
        onPicture: (Bitmap?) -> Unit = {}
    ) {
        if (!spinning) {
            spinning = true
            animator?.cancel()
            animator = ValueAnimator.ofFloat(currentAngle, endAngle)
            animator?.apply {
                duration = 2000
                interpolator = DecelerateInterpolator()
                addUpdateListener { animation ->
                    currentAngle = animation.animatedValue as Float
                    invalidate()
                }
                start()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(p0: Animator) {}
                    override fun onAnimationCancel(p0: Animator) {}
                    override fun onAnimationRepeat(p0: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        onStopDrum(textToDraw, pictureToDraw, onText, onPicture)
                    }
                })
            }
        }
    }

    private fun onStopDrum(
        textToDraw: String?,
        pictureToDraw: Bitmap?,
        onText: (String?) -> Unit = {},
        onPicture: (Bitmap?) -> Unit = {}
    ) {
        currentAngle %= 360
        spinning = false

        when (currentAngle) {
            in BLUE_SECTOR, in YELLOW_SECTOR, in RED_SECTOR, in PURPLE_SECTOR -> {
                textToShow = textToDraw
                onText(textToDraw)
            }

            else -> {
                bitmapToShow = pictureToDraw
                onPicture(pictureToDraw)
            }
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val radius = width.coerceAtMost(height) / 2f

        paint.color = Color.GRAY
        canvas.drawCircle(width / 2, height / 2, radius, paint)

        val angle = 360f / sectorColors.size
        for (i in sectorColors.indices) {
            paint.color = sectorColors[i % sectorColors.size]
            canvas.drawArc(
                0f, 0f, width, height,
                currentAngle + i * angle, angle, true, paint
            )
        }

        textToShow?.let {
            paint.color = Color.BLACK
            paint.textSize = 120f
            paint.textAlign = Paint.Align.CENTER

            val textX = width / 2
            val textY = height / 2 - (paint.descent() + paint.ascent()) / 2
            canvas.drawText(it, textX, textY, paint)
        }

        bitmapToShow?.let {
            val imageX = (width - it.width) / 2f
            val imageY = (height - it.height) / 2f
            canvas.drawBitmap(it, imageX, imageY, paint)
        }
    }

    fun reset() {
        textToShow = null
        bitmapToShow = null
        invalidate()
    }

    fun setupDrum(
        angle: Float = 0f,
        isSpinning: Boolean = false,
        bitmap: Bitmap? = null,
        text: String? = null,
    ) {
        currentAngle = angle
        if (isSpinning)
            startSpinning(
                textToDraw = text,
                pictureToDraw = bitmap
            )
        else {
            textToShow = text
            bitmapToShow = bitmap
            invalidate()
        }
    }
}
