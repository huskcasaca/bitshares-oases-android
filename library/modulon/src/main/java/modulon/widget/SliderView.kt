package modulon.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.LayerDrawable
import android.util.StateSet
import android.widget.SeekBar
import modulon.R
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.toUnion
import modulon.union.UnionContext
import kotlin.math.roundToInt

@SuppressLint("AppCompatCustomView")
class SliderView(context: Context) : SeekBar(context), UnionContext by context.toUnion() {

    private lateinit var progressChangedListener: ProgressChangedListener
    private lateinit var percentChangedListener: PercentageChangedListener
    private lateinit var stepChangedListener: StepChangedListener
    private lateinit var trackingTouchChangedListener: TrackingTouchChangedListener

    var progressActiveColor: Int = context.getColor(R.color.component)
        set(value) {
            (progressDrawable as LayerDrawable).getDrawable(2).setTintList(ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(value)))
            field = value
        }

    var progressInactiveColor: Int = context.getColor(R.color.component_inactive)
        set(value) {
            (progressDrawable as LayerDrawable).getDrawable(1).setTintList(ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(value)))
            field = value
        }


    var thumbColor: Int = context.getColor(R.color.component_inactive)
        set(value) {
            thumb.setTintList(ColorStateList(arrayOf(StateSet.WILD_CARD), intArrayOf(value)))
            field = value
        }

    var maxStep = 10000

    var step = 0

    var isOnTouch = false
        private set

    var snapToStep = false

    init {
        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (::progressChangedListener.isInitialized) progressChangedListener.invoke(progress, fromUser)
                if (::percentChangedListener.isInitialized) percentChangedListener.invoke(1f * progress / max, fromUser)
                val stepChanged = (1f * progress / max * maxStep).roundToInt()
                if (stepChanged != step) {
                    step = stepChanged
                    if (::stepChangedListener.isInitialized) stepChangedListener.invoke(stepChanged, fromUser)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isOnTouch = true
                if (::trackingTouchChangedListener.isInitialized) trackingTouchChangedListener.invoke(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isOnTouch = false
                if (snapToStep) setStep(step, true)
                if (::trackingTouchChangedListener.isInitialized) trackingTouchChangedListener.invoke(false)
            }
        })
        max = 10000
        isForceDarkAllowedCompat = false
    }

    fun SliderView.onStepChanged(listener: StepChangedListener) {
        stepChangedListener = listener
    }

    fun setStep(step: Int, animate: Boolean = false){
        setPercent(1.0 * step / maxStep, animate)
    }
// from SliderView
//    fun setStepRounded(step: Int, animate: Boolean = false){
//        this.step = step
//        setProgress((1.0 * step / maxStep * max).roundToInt().coerceIn(0..max), animate)
//    }

    fun setPercent(percent: Double, animate: Boolean = false) {
        setProgress((max * percent).toInt().coerceIn(0..max), animate)
    }

    fun setProgressChangedListener(listener: ProgressChangedListener) {
        progressChangedListener = listener
    }

    fun setTrackingTouchChangedListener(listener: TrackingTouchChangedListener) {
        trackingTouchChangedListener = listener
    }

    fun setPercentChangedListener(listener: PercentageChangedListener) {
        percentChangedListener = listener
    }
    fun setOnStepChangedListener(listener: StepChangedListener) {
        stepChangedListener = listener
    }
}

typealias ProgressChangedListener = (progress: Int, fromUser: Boolean) -> Unit
typealias PercentageChangedListener = (percent: Float, fromUser: Boolean) -> Unit
typealias StepChangedListener = (step: Int, fromUser: Boolean) -> Unit
typealias TrackingTouchChangedListener = (touched: Boolean) -> Unit

fun SliderView.doOnProgressChanged(listener: ProgressChangedListener) = setProgressChangedListener(listener)
fun SliderView.doOnPercentChanged(listener: PercentageChangedListener) = setPercentChangedListener(listener)
fun SliderView.doOnTrackingTouchChanged(listener: TrackingTouchChangedListener) = setTrackingTouchChangedListener(listener)
fun SliderView.doOnStepChanged(listener: StepChangedListener) = setOnStepChangedListener(listener)