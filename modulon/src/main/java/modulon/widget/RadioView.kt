package modulon.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.view.View
import androidx.annotation.Keep
import modulon.extensions.view.dp
import modulon.extensions.compat.*
import modulon.extensions.view.*

import modulon.union.toUnion
import modulon.union.UnionContext

class RadioView(context: Context) : View(context), UnionContext by context.toUnion() {

    companion object {
        private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 2.dpf
            style = Paint.Style.STROKE
        }

        private var eraser: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = 0
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }

        private var checkedPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private var bitmap: Bitmap? = null
    private lateinit var bitmapCanvas: Canvas
    private var checkedColor = 0
    private var color = 0
    private var checkAnimator: ObjectAnimator? = null
    private var attachedToWindow = false
    var isChecked = false
        private set
    private var size: Int = 24.dp

    init {
        try {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444)
            bitmapCanvas = Canvas(bitmap!!)
        } catch (e: Throwable) {
        }
        isForceDarkAllowedCompat = false
    }

    private var progress = 0f
        @Keep set(value) {
            if (progress == value) return
            field = value
            invalidate()
        }

    fun setSize(value: Int) {
        if (size == value) {
            return
        }
        size = value
    }

    fun setColors(normal: Int, checked: Int) {
        color = normal
        checkedColor = checked
        invalidate()
    }

    override fun setBackgroundColor(color1: Int) {
        color = color1
        invalidate()
    }

    fun setCheckedColor(color2: Int) {
        checkedColor = color2
        invalidate()
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
        }
    }


    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1f else 0f)
        checkAnimator!!.duration = 200
        checkAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow && animated) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            progress = if (checked) 1f else 0f
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (bitmap == null || bitmap!!.width != measuredWidth) {
            if (bitmap != null) {
                bitmap!!.recycle()
                bitmap = null
            }
            try {
                bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                bitmapCanvas = Canvas(bitmap!!)
            } catch (e: Throwable) {
            }
        }
        val circleProgress: Float
        if (progress <= 0.5f) {
            paint.color = color
            checkedPaint.color = color
            circleProgress = progress / 0.5f
        } else {
            circleProgress = 2.0f - progress / 0.5f
            val r1 = Color.red(color)
            val rD = ((Color.red(checkedColor) - r1) * (1.0f - circleProgress)).toInt()
            val g1 = Color.green(color)
            val gD = ((Color.green(checkedColor) - g1) * (1.0f - circleProgress)).toInt()
            val b1 = Color.blue(color)
            val bD = ((Color.blue(checkedColor) - b1) * (1.0f - circleProgress)).toInt()
            val c = Color.rgb(r1 + rD, g1 + gD, b1 + bD)
            paint.color = c
            checkedPaint.color = c
        }
        if (bitmap != null) {
            bitmap!!.eraseColor(0)
            val rad: Float = size / 2f - 2 * (1 + circleProgress).dpf
            bitmapCanvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, rad, paint)
            if (progress <= 0.5f) {
                bitmapCanvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, rad - 1.dpf, checkedPaint)
                bitmapCanvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, (rad - 1.dpf) * (1f - circleProgress), eraser)
            } else {
                bitmapCanvas.drawCircle(measuredWidth / 2f, measuredHeight / 2f, size / 4f + (rad - 1.dpf - size / 4f) * circleProgress + 1.dpf, checkedPaint)
            }
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
        }
    }

}
