package modulon.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.*
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.*
import android.view.animation.Animation.INFINITE
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.widget.ImageView
import android.widget.LinearLayout.*
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentManager
import modulon.R
import modulon.UI
import modulon.extensions.animation.extendedLayoutTransition
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceExtraBold
import modulon.extensions.font.typefaceRegular
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.graphics.createRoundRectSelectorDrawable
import modulon.extensions.view.*
import modulon.extensions.viewbinder.clipping
import modulon.extensions.viewbinder.noMotion
import modulon.interpolator.CubicBezierInterpolator
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.VerticalLayout
import modulon.layout.recycler.RecyclerLayout
import modulon.union.UnionDialogFragment
import modulon.widget.PlainTextView
import kotlin.math.abs

typealias OnViewCreatedListener = () -> Unit
typealias OnDismissListener = () -> Unit
typealias OnShowListener = () -> Unit

open class BottomDialogFragment : UnionDialogFragment() {

    fun hide() = dialog?.hide()
    fun show() = dialog?.show()

    var isCancelableTouchOutside = true
        get() = field && isCancelable
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
            dialog?.setOnDismissListener { }
        }

    var isCancelableByButtons = false
        get() = field && isCancelable

    private var allowCustomAnimation = true

    var state = DialogState.EMPTY
        set(value) {
            if (field != value) {
                field = value
                showProgressIcon(value)
            }
        }

    // TODO: 2022/2/21 replace with title()
    var title: CharSequence
        get() = titleView.text
        @JvmName("setTitleKt") set(text) {
            titleView.isVisible = text.isNotEmpty()
            titleView.text = text
            titleContainer.requestLayout()
        }

    // TODO: 2022/2/21 replace with subtitle()
    var subtitle: CharSequence
        get() = subtitleView.text
        @JvmName("setSubtitleKt") set(text) {
            subtitleView.isVisible = text.isNotEmpty()
            subtitleView.text = text
            titleContainer.requestLayout()
        }

    var customView: View? = null
        set(value) {
            field = value
            contentScrollView.removeAllViews()
            if (value != null) {
                if (value.parent is ViewGroup) {
                    (value.parent as ViewGroup).removeView(value)
                }
                contentScrollView.addView(value)
            }
        }

    val titleView: PlainTextView by lazyView {
        typeface = typefaceExtraBold
        textColor = context.getColor(R.color.dialog_title)
        textSize = 22.5f
        gravity = Gravity.START or Gravity.TOP
        isSingleLine = true
        isVisible = false
    }

    val subtitleView: PlainTextView by lazyView {
        typeface = typefaceBold
        textColor = context.getColor(R.color.dialog_subtitle)
        textSize = 14.5f
        gravity = Gravity.START or Gravity.TOP
        isVisible = false
        isAllCaps = true
    }

    val messageView: PlainTextView by lazyView {
        // TODO: 2021/1/4 extract to extension
        setLinkTextColor(context.getColor(R.color.text_link))
        typeface = typefaceRegular
        textColor = context.getColor(R.color.text_primary)
        textSize = 16f
        gravity = Gravity.START or Gravity.TOP
        isVisible = false
    }

    var message: CharSequence
        get() = messageView.text
        set(text) {
            messageView.isVisible = text.isNotEmpty()
            messageView.text = text
        }


    private var currentSheetAnimation: AnimatorSet? = null

    private val progressView: ImageView by lazyView()

    private val titleContainer: FrameLayout by lazyView {
        updatePadding(R.dimen.dialog_padding_vertical.contextDimenPixelSize(), R.dimen.dialog_padding_top.contextDimenPixelSize(), R.dimen.dialog_padding_vertical.contextDimenPixelSize(), 8.dp)
        addRow(titleView, gravity = Gravity.START or Gravity.TOP, end = 30.dp)
        addWrap(progressView, gravity = Gravity.END or Gravity.TOP)
        addRow(subtitleView, gravity = Gravity.START or Gravity.TOP, top = 28.dp)
    }

    private val contentScrollView: FrameLayout by lazyView {
//        isNestedScrollingEnabled = true
//        isVerticalScrollBarEnabled = false
    }

    private val containerButton: VerticalLayout by lazyView {
        setPadding(UI.SPACING.dp, UI.SPACING.dp, UI.SPACING.dp, UI.SPACING.dp)
        dividerDrawable = ShapeDrawable().apply {
            intrinsicHeight = 12.dp
            setTint(R.color.transparent.contextColor())
        }
        showDividers = SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END
    }

    private val containerView: VerticalLayout by lazyView {
        noMotion()
        clipping()
        fitsSystemWindows = true
        layoutTransition = extendedLayoutTransition
        background = createRoundRectDrawable(context.getColor(R.color.background), UI.CORNER_RADIUS_DIALOG.dpf, 0.dpf, 0.dpf, UI.CORNER_RADIUS_DIALOG.dpf)
        addRow(titleContainer)
        addRow(messageView, start = context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical), end = context.resources.getDimensionPixelSize(R.dimen.dialog_padding_vertical))
        addRow(contentScrollView, weight = 1f)
        addRow(containerButton)
    }

    private val container by lazy {
        DialogContainer(context).apply {
            fitsSystemWindows = false
            addRow(containerView)
        }
    }


    private var dismissCallback: OnDismissListener = { }
    private var viewCreatedListener: OnViewCreatedListener = { }
    private var showListener: OnShowListener = { }

    private val disableScroll = false

    var isDismissed = false
        private set

    fun show(manager: FragmentManager) {
        super.show(manager, null)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        showListener.invoke()
    }
    fun dismissNow() {
        if (isCancelable && !isDismissed) {
            dismissCallback.invoke()
            isDismissed = true
            super.dismiss()
        }
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        dismissCallback = listener
    }
    fun setOnViewCreatedListener(listener: OnViewCreatedListener) {
        viewCreatedListener = listener
    }
    fun setOnShowListener(listener: OnShowListener) {
        dialog?.setOnShowListener { listener.invoke() }
    }
    fun addButton(button: TextButton) {
        button.setParamsRow(height = context.resources.getDimensionPixelSize(R.dimen.dialog_button_height))
        containerButton.addRow(button)
    }
    fun addButton(button: TextButton, index: Int) {
        button.setParamsRow(height = context.resources.getDimensionPixelSize(R.dimen.dialog_button_height))
        containerButton.addView(button, index)
    }
    fun setButton(block: TextButton.() -> Unit, index: Int) {
        (containerButton.getChildAt(index) as TextButton).apply(block)
    }
    fun removeButton(button: TextButton) {
        containerButton.removeView(button)
    }

    private fun startStagingAnimation() {
        containerView.measure(View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.AT_MOST))
        containerView.translationY = containerView.measuredHeight.toFloat()
        container.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        containerView.translationY = containerView.measuredHeight.toFloat()
        currentSheetAnimation = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(containerView, View.TRANSLATION_Y, 0f),
//                ObjectAnimator.ofInt<ColorDrawable>(backDrawable, AnimationProperties.COLOR_DRAWABLE_ALPHA, if (dimBehind) 51 else 0)
            )
            duration = 400
            startDelay = 20
            interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
            addListener(
                onEnd = {
                    if (currentSheetAnimation == it) {
                        currentSheetAnimation = null
                        container.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                },
                onCancel = {
                    if (currentSheetAnimation == it) currentSheetAnimation = null
                }
            )
            start()
        }
    }
    private fun startDetachingAnimation() {
        currentSheetAnimation = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(containerView, View.TRANSLATION_Y, containerView.measuredHeight.toFloat()),
            )
            duration = 400
            startDelay = 20
            interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
            addListener(
                onStart = {
                    dialog?.window?.setDimAmount(0f)
                },
                onEnd = {
                    if (currentSheetAnimation == it) {
                        currentSheetAnimation = null
                        container.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                    try { super.dismissAllowingStateLoss(); isDismissed = true } catch (e: Throwable) { }
                },
                onCancel = {
                    if (currentSheetAnimation == it) currentSheetAnimation = null
//                        dialog?.window?.setDimAmount(0.5f)
                }
            )
            containerView.post { start() }
        }
    }
    private fun cancelSheetAnimation() {
        currentSheetAnimation?.cancel()
        currentSheetAnimation = null
    }

    private fun showProgressIcon(currentState: DialogState) {
        when (currentState) {
            DialogState.PENDING -> {
                val loadingIcon = R.drawable.ic_dialog_loading.contextDrawableNoTint() as AnimatedVectorDrawable
                loadingIcon.setTint(context.getColor(R.color.title))
                val rotateAnimation = RotateAnimation(0f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f).apply {
                    duration = 1000
                    repeatCount = INFINITE
                    interpolator = LinearInterpolator()
                }
                val fadeInAnimation = AlphaAnimation(0f, 1f).apply {
                    duration = 200
                    interpolator = DecelerateInterpolator()
                }
                val animation = AnimationSet(false).apply {
                    addAnimation(fadeInAnimation)
                    addAnimation(rotateAnimation)
                }
                progressView.setImageDrawable(loadingIcon)
                progressView.startAnimation(animation)
                loadingIcon.start()
            }
            DialogState.SUCCESS -> {
                val successIcon = R.drawable.ic_dialog_success.contextDrawableNoTint() as AnimatedVectorDrawable
                successIcon.setTint(context.getColor(R.color.title))
                progressView.clearAnimation()
                progressView.setImageDrawable(successIcon)
                successIcon.start()
            }
            DialogState.FAILURE -> {
                val failedIcon = R.drawable.ic_dialog_failed.contextDrawableNoTint() as AnimatedVectorDrawable
                failedIcon.setTint(context.getColor(R.color.title))
                progressView.clearAnimation()
                progressView.setImageDrawable(failedIcon)
                failedIcon.start()
            }
            DialogState.EMPTY -> {
                val fadeOutAnimation = AlphaAnimation(1f, 0f).apply {
                    interpolator = DecelerateInterpolator()
                    duration = 200
                    fillAfter = true
                }
                progressView.startAnimation(fadeOutAnimation)

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container1: ViewGroup?, savedInstanceState: Bundle?): View? {
        // TODO: 2022/2/15 gesture bar
        dialog?.apply {
            window?.apply {
                attributes = WindowManager.LayoutParams().apply {
                    copyFrom(dialog?.window?.attributes)
                    gravity = Gravity.BOTTOM
                    windowAnimations = 0
//                    dimAmount = 0f
                    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE // SOFT_INPUT_STATE_VISIBLE  SOFT_INPUT_ADJUST_PAN
//                    flags = flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
                }
//                setBackgroundDrawable(ColorDrawable(context.getColor(R.color.transparent)))
                decorView.background = null
                setLayout(MATCH_PARENT, WRAP_CONTENT)
            }
            setOnDismissListener { dismissNow() }
            setCanceledOnTouchOutside(false)
        }
        return container
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreatedListener.invoke()
    }
    override fun onStart() {
        super.onStart()
        startStagingAnimation()
        dialog?.window?.apply {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val dimDrawable = GradientDrawable()
            // ...customize your dim effect here
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)
            val layers: Array<Drawable> = arrayOf(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)
            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            setBackgroundDrawable(windowBackground)
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(context) {
            override fun onTouchEvent(event: MotionEvent): Boolean {
                if (isCancelableTouchOutside && event.action == MotionEvent.ACTION_DOWN) this@BottomDialogFragment.dismiss()
                return super.onTouchEvent(event)
            }
        }
    }
    override fun dismiss() {
        if (isCancelable && !isDismissed) {
            dismissCallback.invoke()
            startDetachingAnimation()
        }
    }

    private inner class DialogContainer(context: Context) : FrameLayout(context), NestedScrollingParent {

        private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

        private var velocityTracker: VelocityTracker = VelocityTracker.obtain()
        private var startedTrackingX = 0
        private var startedTrackingY = 0
        private var startedTrackingPointerId = -1
        private var maybeStartTracking = false
        private var startedTracking = false
        private var currentAnimation: AnimatorSet? = null
        private val nestedScrollingParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)

        private var nestedScrollChild: View? = null

        fun canDismissWithSwipe() = true

        override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
            return !isDetached && !(nestedScrollChild != null && child !== nestedScrollChild) && nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && !canDismissWithSwipe()
        }

        override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
            nestedScrollingParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
            if (isDetached) return
            cancelCurrentAnimation()
        }

        override fun onStopNestedScroll(target: View) {
            nestedScrollingParentHelper.onStopNestedScroll(target)
            if (isDetached) return
            val currentTranslation = containerView.translationY
            checkDismiss(0f, 0f)
        }

        override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
            if (isDetached) return
            cancelCurrentAnimation()
            if (dyUnconsumed != 0) {
                var currentTranslation = containerView.translationY
                currentTranslation -= dyUnconsumed.toFloat()
                if (currentTranslation < 0) {
                    currentTranslation = 0f
                }
                containerView.translationY = currentTranslation
            }
        }

        override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
            if (isDetached) return
            cancelCurrentAnimation()
            var currentTranslation = containerView.translationY
            if (currentTranslation > 0 && dy > 0) {
                currentTranslation -= dy.toFloat()
                consumed[1] = dy
                if (currentTranslation < 0) {
                    currentTranslation = 0f
                }
                containerView.translationY = currentTranslation
            }
        }

        override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean = false

        override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean = false

        override fun getNestedScrollAxes(): Int = nestedScrollingParentHelper.nestedScrollAxes

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return processTouchEvent(event, false)
        }

        private fun cancelCurrentAnimation() {
            currentAnimation?.cancel()
            currentAnimation = null
        }


        override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
            return if (canDismissWithSwipe()) {
                processTouchEvent(event, true)
            } else super.onInterceptTouchEvent(event)
        }

        override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            if (maybeStartTracking && !startedTracking) {
                onTouchEvent(null)
            }
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }

        private fun checkDismiss(velX: Float, velY: Float) {
            val translationY = containerView.translationY
            val backAnimation = translationY < 80.dp && (velY < 3500 || abs(velY) < abs(velX)) || velY < 0 && Math.abs(velY) >= 3500
            if (!backAnimation) {
                val allowOld: Boolean = allowCustomAnimation
                allowCustomAnimation = false
//                useFastDismiss = true
                dismiss()
                allowCustomAnimation = allowOld
            } else {
                currentAnimation = AnimatorSet().apply {
                    playTogether(ObjectAnimator.ofFloat(containerView, "translationY", 0f))
                    duration = (150 * (translationY / 80.dp)).toLong()
                    interpolator = CubicBezierInterpolator.EASE_OUT
                    addListener(onEnd = {
                        if (currentAnimation != null && currentAnimation == it) currentAnimation = null
                    })
                    start()
                }
            }
        }

        fun processTouchEvent(event: MotionEvent?, intercept: Boolean): Boolean {
            if (isDetached) return false
            // TODO: 23/8/2021 intercept event for contentScrollView
            when {
                isCancelableTouchOutside && event != null && (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) && !startedTracking && !maybeStartTracking && event.pointerCount == 1 -> {
                    startedTrackingX = event.x.toInt()
                    startedTrackingY = event.y.toInt()
                    if (startedTrackingY < containerView.top || startedTrackingX < containerView.left || startedTrackingX > containerView.right) {
                        dismiss()
                        return true
                    }
                    startedTrackingPointerId = event.getPointerId(0)
                    maybeStartTracking = true
                    cancelCurrentAnimation()
                    velocityTracker?.clear()
                }
                event != null && event.action == MotionEvent.ACTION_MOVE && event.getPointerId(0) == startedTrackingPointerId -> {
                    velocityTracker = VelocityTracker.obtain()
                    val dx = abs(event.x - startedTrackingX)
                    val dy = event.y - startedTrackingY

                    velocityTracker.addMovement(event)
                    if (!disableScroll && maybeStartTracking && !startedTracking && dy > 0 && dy / 3.0f > abs(dx) && abs(dy) >= touchSlop) {
                        startedTrackingY = event.y.toInt()
                        maybeStartTracking = false
                        val child = contentScrollView.getChildAt(0)
                        startedTracking = if (child is RecyclerLayout) child.isOnTop else true
                    } else if (startedTracking) {
                        var translationY = containerView.translationY
                        translationY += dy
                        if (translationY < 0) translationY = 0f
                        containerView.translationY = translationY
                        startedTrackingY = event.y.toInt()
                    }
                }
                event == null || event.getPointerId(0) == startedTrackingPointerId && (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_POINTER_UP) -> {
                    velocityTracker = VelocityTracker.obtain()
                    velocityTracker.computeCurrentVelocity(1000)
                    val translationY = containerView.translationY
                    if (startedTracking || translationY != 0f) {
                        checkDismiss(velocityTracker.xVelocity, velocityTracker.yVelocity)
                        startedTracking = false
                    } else {
                        maybeStartTracking = false
                        startedTracking = false
                    }
                    velocityTracker.clear()
                    startedTrackingPointerId = -1
                }
            }
            return !intercept && maybeStartTracking || startedTracking || !canDismissWithSwipe()
        }
    }
    @SuppressLint("AppCompatCustomView")
    inner class TextButton(context: Context) : TextView(context) {

        var text: CharSequence
            @JvmName("getTextKt") get() = getText()
            @JvmName("setTextKt") set(value) {
                isVisible = value.isNotBlank()
                setText(value)
            }

        var textColor: Int
            @JvmName("getTextColorKt") get() = currentTextColor
            @JvmName("setTextColorKt") set(value) {
                setTextColor(value)
                background = createRoundRectSelectorDrawable(
                    value and 0x00FFFFFF or 0x36000000,
                    context.getColor(R.color.background_cover_1),
                    context.resources.getDimension(R.dimen.dialog_button_height) / 2)
            }

        // TODO: 26/1/2022 remove
        var isEnabled
            @JvmName("isEnabledKt") get() = isEnabled()
            @JvmName("setEnabledKt") set(value) {
                setEnabled(value)
                alpha = if (value) 1f else 0.5f
            }

        init {
            typeface = typefaceBold
            textSize = 18f
            textColor = context.getColor(R.color.dialog_button_normal)
            updatePadding(UI.SPACING.dp, 0, UI.SPACING.dp, 0)
            gravity = Gravity.CENTER
            ellipsize = TextUtils.TruncateAt.MIDDLE
            isSingleLine = true
            setOnClickListener(null)
            isVisible = false
            isAllCaps = true
        }

        override fun setOnClickListener(listener: OnClickListener?) {
            super.setOnClickListener {
                listener?.onClick(it)
                if (isCancelableByButtons) dismiss()
            }
        }

    }

}