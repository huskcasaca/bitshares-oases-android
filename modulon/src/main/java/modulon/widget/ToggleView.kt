package modulon.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.StateSet
import android.view.View
import androidx.annotation.Keep
import modulon.R
import modulon.extensions.compat.*
import modulon.extensions.view.*

import modulon.union.toUnion
import modulon.union.UnionContext

class ToggleView(context: Context) : View(context), UnionContext by context.toUnion() {

    private val rectF: RectF = RectF()

    var isChecked = false
        private set

    @Keep
    var progress = 0f
        @Keep set(value) {
            if (progress == value) return
            field = value
            invalidate()
        }

    @Keep
    var iconProgress = 1f
        @Keep set(value) {
            if (this.iconProgress == value) return
            field = value
            invalidate()
        }

    private var checkAnimator: ObjectAnimator? = null
    private var iconAnimator: ObjectAnimator? = null

    private var attachedToWindow: Boolean = false

    private var activeColor = 0xffffffff.toInt()
    private var inactiveColor = 0xff888888.toInt()

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paint2: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var drawIconType = 0

    private var onCheckedChangeListener: ((ToggleView, Boolean) -> Unit)? = null

    private var iconDrawable: Drawable? = null
    private var lastIconColor: Int = 0

    private var drawRipple: Boolean = false
    private var rippleDrawable: RippleDrawable? = null
    private var ripplePaint: Paint? = null
    private val pressedState = intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed)
    private var colorSet: Int = 0

    private var bitmapsCreated: Boolean = false

    private var overlayBitmap: Array<Bitmap>? = null
    private var overlayCanvas: Array<Canvas>? = null
    private var overlayMaskBitmap: Bitmap? = null
    private var overlayMaskCanvas: Canvas? = null
    private var overlayEraserPaint: Paint? = null
    private var overlayMaskPaint: Paint? = null
    private var overlayCx = 0f
    private var overlayCy = 0f
    private var overlayRad = 0f

    private var overrideColorProgress = 0


    init {
        paint2.style = Paint.Style.STROKE
        paint2.strokeCap = Paint.Cap.ROUND
        paint2.strokeWidth = 4.dpf
        isForceDarkAllowedCompat = false
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
            checkAnimator = null
        }
    }

    private fun cancelIconAnimator() {
        if (iconAnimator != null) {
            iconAnimator!!.cancel()
            iconAnimator = null
        }
    }

    fun setDrawIconType(type: Int) {
        drawIconType = type
    }

    fun setDrawRipple(value: Boolean) {
        drawRipple = value
        if (rippleDrawable == null) {
            ripplePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            ripplePaint!!.color = -0x1
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(0)
            )
            rippleDrawable = RippleDrawable(colorStateList, null, null)
            rippleDrawable!!.radius = context.resources.getDimensionPixelSize(R.dimen.switch_ripple_radius)
            rippleDrawable!!.callback = this
        }
        if (isChecked && colorSet != 2 || !isChecked && colorSet != 1) {
            val color: Int = if (isChecked) activeColor else inactiveColor
            /*if (Build.VERSION.SDK_INT < 28) {
                color = Color.argb(Color.alpha(color) * 2, Color.red(color), Color.green(color), Color.blue(color));
            }*/
            val colorStateList = ColorStateList(
                arrayOf(StateSet.WILD_CARD),
                intArrayOf(color)
            )
            rippleDrawable!!.setColor(colorStateList)
            colorSet = if (isChecked) 2 else 1
        }
        if (Build.VERSION.SDK_INT >= 28 && value) {
            rippleDrawable!!.setHotspot(
                (if (isChecked) 0 else 100.dp).toFloat(),
                18.dpf
            )
        }
        rippleDrawable!!.state = if (value) pressedState else StateSet.NOTHING
        invalidate()
    }

    override fun verifyDrawable(who: Drawable): Boolean {
        return super.verifyDrawable(who) || rippleDrawable != null && who === rippleDrawable
    }

    fun setColors(active: Int, inactive: Int) {
        activeColor = active
        inactiveColor = inactive
        invalidate()
    }


//    fun setColors(track: String, trackChecked: String, thumb: String, thumbChecked: String) {
//        trackColorKey = track
//        trackCheckedColorKey = trackChecked
//        thumbColorKey = thumb
//        thumbCheckedColorKey = thumbChecked
//    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1f else 0f)
        checkAnimator!!.duration = 240L
        checkAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                checkAnimator = null
            }
        })
        checkAnimator!!.start()
    }

    private fun animateIcon(newCheckedState: Boolean) {
        iconAnimator = ObjectAnimator.ofFloat(this, "iconProgress", if (newCheckedState) 1f else 0f)
        iconAnimator!!.duration = 240L
        iconAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                iconAnimator = null
            }
        })
        iconAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    fun setOnCheckedChangeListener(listener: ((ToggleView, Boolean) -> Unit)?) {
        onCheckedChangeListener = listener
    }

    // FIXME: 22/1/2022 no animated
    fun setChecked(checked: Boolean, animated: Boolean) {
        setChecked(checked, drawIconType, animated)
    }

    fun setChecked(checked: Boolean, iconType: Int, animated: Boolean) {
        if (checked != isChecked) {
            isChecked = checked
            if (attachedToWindow && animated) {
                animateToCheckedState(checked)
            } else {
                cancelCheckAnimator()
                progress = if (checked) 1.0f else 0.0f
            }
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener!!.invoke(this, checked)
            }
        }
        if (drawIconType != iconType) {
            drawIconType = iconType
            if (attachedToWindow && animated) {
                animateIcon(iconType == 0)
            } else {
                cancelIconAnimator()
                iconProgress = if (iconType == 0) 1.0f else 0.0f
            }
        }
    }

//    fun setIcon(icon: Int) {
//        if (icon != 0) {
//            iconDrawable = resources.getDrawable(icon).mutate()
//            if (iconDrawable != null) {
//                lastIconColor = if (isChecked) trackCheckedColor else trackColor
//                iconDrawable!!.colorFilter = PorterDuffColorFilter(
//                    lastIconColor,
//                    PorterDuff.Mode.MULTIPLY
//                )
//            }
//        } else {
//            iconDrawable = null
//        }
//    }

    fun hasIcon(): Boolean {
        return iconDrawable != null
    }

    fun setOverrideColor(override: Int) {
        if (overrideColorProgress == override) return
        if (overlayBitmap == null) {
            try {
                overlayBitmap = arrayOf()
                overlayCanvas = arrayOf()
                for (a in 0..1) {
                    overlayBitmap!![a] = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                    overlayCanvas!![a] = Canvas(overlayBitmap!![a])
                }
                overlayMaskBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                overlayMaskCanvas = Canvas(overlayMaskBitmap!!)

                overlayEraserPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                overlayEraserPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

                overlayMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                overlayMaskPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
                bitmapsCreated = true
            } catch (e: Throwable) {
                return
            }

        }
        if (!bitmapsCreated) return
        overrideColorProgress = override
        overlayCx = 0f
        overlayCy = 0f
        overlayRad = 0f
        invalidate()
    }

    fun setOverrideColorProgress(cx: Float, cy: Float, rad: Float) {
        overlayCx = cx
        overlayCy = cy
        overlayRad = rad
        invalidate()
    }

    val trackSize = context.resources.getDimensionPixelSize(R.dimen.switch_track_width)
    val thumbSize = context.resources.getDimension(R.dimen.switch_thumb_radius)

    override fun onDraw(canvas: Canvas) {
        if (visibility != VISIBLE) return
//        val height = context.resources.getDimensionPixelSize(R.dimen.switch_track_height)
//        val shift = context.resources.getDimension(R.dimen.switch_thumb_position_shift)

        val x = (measuredWidth - trackSize) / 2f
        val y = measuredHeight / 2f
        var tx: Float = x + trackSize * progress
        var ty: Float = measuredHeight / 2f


        var color1: Int
        var color2: Int
        var colorProgress: Float
        var r1: Int
        var r2: Int
        var g1: Int
        var g2: Int
        var b1: Int
        var b2: Int
        var a1: Int
        var a2: Int
        var red: Int
        var green: Int
        var blue: Int
        var alpha: Int
        var color: Int

        for (a in 0..1) {
            if (a == 1 && overrideColorProgress == 0) {
                continue
            }
            val canvasToDraw = if (a == 0) canvas else overlayCanvas!![0]

            if (a == 1) {
                overlayBitmap!![0].eraseColor(0)
                paint.color = -0x1000000
                overlayMaskCanvas!!.drawRect(
                    0f,
                    0f,
                    overlayMaskBitmap!!.width.toFloat(),
                    overlayMaskBitmap!!.height.toFloat(),
                    paint
                )
                overlayMaskCanvas!!.drawCircle(
                    overlayCx - getX(),
                    overlayCy - getY(),
                    overlayRad,
                    overlayEraserPaint!!
                )

            }
            colorProgress = when (overrideColorProgress) {
                1 -> (if (a == 0) 0 else 1).toFloat()
                2 -> (if (a == 0) 1 else 0).toFloat()
                else -> progress
            }
            color1 = inactiveColor
            color2 = activeColor
            if (a == 0 && iconDrawable != null && lastIconColor != (if (isChecked) color2 else color1)) {
                lastIconColor = if (isChecked) color2 else color1
                iconDrawable!!.colorFilter =
                    PorterDuffColorFilter(
                        lastIconColor,
                        PorterDuff.Mode.MULTIPLY
                    )
            }

            r1 = Color.red(color1)
            r2 = Color.red(color2)
            g1 = Color.green(color1)
            g2 = Color.green(color2)
            b1 = Color.blue(color1)
            b2 = Color.blue(color2)
            a1 = Color.alpha(color1)
            a2 = Color.alpha(color2)

            red = (r1 + (r2 - r1) * colorProgress).toInt()
            green = (g1 + (g2 - g1) * colorProgress).toInt()
            blue = (b1 + (b2 - b1) * colorProgress).toInt()
            alpha = (a1 + (a2 - a1) * colorProgress).toInt()
            color = alpha and 0xff shl 24 or (red and 0xff shl 16) or (green and 0xff shl 8) or (blue and 0xff)
            paint.color = color
            paint.strokeWidth = 2.dpf
            paint.style = Paint.Style.STROKE
            paint2.color = color


            if (progress <= (trackSize - thumbSize / 2) / trackSize) {
                canvasToDraw.drawLine(x + thumbSize + (trackSize) * progress, y, (x + trackSize + thumbSize / 2), y, paint)
            }

            if (progress >= 1 - (trackSize - thumbSize / 2) / trackSize) {
                canvasToDraw.drawLine(x - thumbSize / 2, y, x + trackSize - thumbSize - (trackSize) * (1 - progress), y, paint)
            }


            // Draw Circle
            canvasToDraw.drawCircle(tx, ty, thumbSize, paint)
            if (a == 0 && rippleDrawable != null) {
                rippleDrawable!!.setBounds(
                    (tx - 18.dpf).toInt(),
                    (ty - 18.dpf).toInt(), (tx + 18.dpf).toInt(), (ty + 18.dpf).toInt()
                )
                rippleDrawable!!.draw(canvasToDraw)
            } else if (a == 1) {
                canvasToDraw.drawBitmap(overlayMaskBitmap!!, 0f, 0f, overlayMaskPaint)
            }
        }
        if (overrideColorProgress != 0) {
            canvas.drawBitmap(overlayBitmap!![0], 0f, 0f, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(trackSize + thumbSize.toInt() * 2 + 4.dp, thumbSize.toInt() * 2 + 2.dp)
    }
}
