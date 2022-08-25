package modulon.dialog

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.*
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.*
import android.view.animation.Animation.INFINITE
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.widget.ImageView
import android.widget.LinearLayout.*
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.view.*
import androidx.fragment.app.FragmentManager
import modulon.R
import modulon.UI
import modulon.extensions.compat.isNightModeOn
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceExtraBold
import modulon.extensions.font.typefaceRegular
import modulon.extensions.graphics.createRoundRectDrawable
import modulon.extensions.graphics.createRoundRectSelectorDrawable
import modulon.extensions.stdlib.logcat
import modulon.extensions.view.*
import modulon.extensions.viewbinder.clipping
import modulon.extensions.viewbinder.coordinatorLayout
import modulon.extensions.viewbinder.noMotion
import modulon.extensions.viewbinder.verticalLayout
import modulon.interpolator.CubicBezierInterpolator
import modulon.layout.lazy.LazyListView
import modulon.layout.stack.StackView
import modulon.layout.linear.VerticalView
import modulon.union.UnionDialogFragment
import modulon.widget.PlainTextView
import kotlin.math.abs

typealias OnViewCreatedListener = () -> Unit
typealias OnDismissListener = () -> Unit
typealias OnShowListener = () -> Unit


abstract class ExpandableFragment : UnionDialogFragment() {

    private val animController = SheetAnimationController()
    private val container by lazy {
        StackView(context).apply {
            clipToOutline = false
            clipChildren = false
            clipToPadding = false
            layoutAnimation = null
            layoutTransition = null
            fitsSystemWindows = true
            layoutWidth = MATCH_PARENT
            layoutHeight = WRAP_CONTENT
            layoutGravityFrame = Gravity.BOTTOM
            background = createRoundRectDrawable(context.getColor(R.color.background), UI.CORNER_RADIUS_DIALOG.dpf, 0.dpf, 0.dpf, UI.CORNER_RADIUS_DIALOG.dpf)
            coordinatorLayout {
                onCreateCoordinatorView()
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container1: ViewGroup?, savedInstanceState: Bundle?): View? =
        object : StackView(context), NestedScrollingParent {
            private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
            private var velocityTracker: VelocityTracker = VelocityTracker.obtain()
            private var startedTrackingX = 0
            private var startedTrackingY = 0
            private var startedTrackingPointerId = -1
            private var maybeStartTracking = false
            private var startedTracking = false
            private var currentAnimation: AnimatorSet? = null
            private val scroller: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
            private var allowCustomAnimation = true
            private fun canDismissWithSwipe() = false

            override fun onStartNestedScroll(child: View, target: View, axes: Int): Boolean {
                return false
//                return !isDetached && !(nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL && !canDismissWithSwipe())
            }
            override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
                scroller.onNestedScrollAccepted(child, target, nestedScrollAxes)
                if (isDetached) return
                cancelCurrentAnimation()
            }
            override fun onStopNestedScroll(target: View) {
                scroller.onStopNestedScroll(target)
                if (isDetached) return
                val currentTranslation = container.translationY
                checkDismiss(0f, 0f)
            }
            override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
                "DialogContainer onNestedScroll target $target".logcat()
                if (isDetached) return
                cancelCurrentAnimation()
                if (dyUnconsumed != 0) {
                    var currentTranslation = container.translationY
                    currentTranslation -= dyUnconsumed.toFloat()
                    if (currentTranslation < 0) {
                        currentTranslation = 0f
                    }
                    container.translationY = currentTranslation
                }
            }
            override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
                "DialogContainer onNestedPreScroll target $target".logcat()
                if (isDetached) return
                cancelCurrentAnimation()
                var currentTranslation = container.translationY
                if (currentTranslation > 0 && dy > 0) {
                    currentTranslation -= dy.toFloat()
                    consumed[1] = dy
                    if (currentTranslation < 0) {
                        currentTranslation = 0f
                    }
                    container.translationY = currentTranslation
                }
            }
            override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean = false
            override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean = false
            override fun getNestedScrollAxes(): Int = scroller.nestedScrollAxes
            override fun onTouchEvent(event: MotionEvent?): Boolean {
                return processTouchEvent(event, false)
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
                val translationY = container.translationY
                val backAnimation = translationY < 80.dp && (velY < 3500 || abs(velY) < abs(velX)) || velY < 0 && Math.abs(velY) >= 3500
                if (!backAnimation) {
                    val allowOld: Boolean = allowCustomAnimation
                    allowCustomAnimation = false
//                useFastDismiss = true
                    dismiss()
                    allowCustomAnimation = allowOld
                } else {
                    currentAnimation = AnimatorSet().apply {
                        playTogether(ObjectAnimator.ofFloat(container, "translationY", 0f))
                        duration = (150 * (translationY / 80.dp)).toLong()
                        interpolator = CubicBezierInterpolator.EASE_OUT
                        addListener(onEnd = {
                            if (currentAnimation != null && currentAnimation == it) currentAnimation = null
                        })
                        start()
                    }
                }
            }
            private fun processTouchEvent(event: MotionEvent?, intercept: Boolean): Boolean {
                if (isDetached) return false
                // TODO: 23/8/2021 intercept event for contentScrollView
                when {
                    /* isCancelableTouchOutside */ isCancelable && event != null && (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) && !startedTracking && !maybeStartTracking && event.pointerCount == 1 -> {
                    startedTrackingX = event.x.toInt()
                    startedTrackingY = event.y.toInt()
                    if (startedTrackingY < container.top || startedTrackingX < container.left || startedTrackingX > container.right) {
                        dismiss()
                        return true
                    }
                    startedTrackingPointerId = event.getPointerId(0)
                    maybeStartTracking = true
                    cancelCurrentAnimation()
                    velocityTracker.clear()
                }
                    event != null && event.action == MotionEvent.ACTION_MOVE && event.getPointerId(0) == startedTrackingPointerId -> {
                        velocityTracker = VelocityTracker.obtain()
                        val dx = abs(event.x - startedTrackingX)
                        val dy = event.y - startedTrackingY

                        velocityTracker.addMovement(event)
                        if (/*!disableScroll && */maybeStartTracking && !startedTracking && dy > 0 && dy / 3.0f > abs(dx) && abs(dy) >= touchSlop) {
                            startedTrackingY = event.y.toInt()
                            maybeStartTracking = false
                            // FIXME: 2022/4/29
//                        val child = contentScrollView.getChildAt(0)
//                        startedTracking = if (child is RecyclerLayout) child.isOnTop else true
                            startedTracking = false
                        } else if (startedTracking) {
                            var translationY = container.translationY
                            translationY += dy
                            if (translationY < 0) translationY = 0f
                            container.translationY = translationY
                            startedTrackingY = event.y.toInt()
                        }
                    }
                    event == null || event.getPointerId(0) == startedTrackingPointerId && (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_POINTER_UP) -> {
                        velocityTracker = VelocityTracker.obtain()
                        velocityTracker.computeCurrentVelocity(1000)
                        val translationY = container.translationY
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
            private fun cancelCurrentAnimation() {
                currentAnimation?.cancel()
                currentAnimation = null
            }

            init {
                addView(container)
            }
        }
    override fun onStart() {
        super.onStart()
        animController.attach()
    }
    override fun dismiss() {
        animController.detach()
    }
    fun detachSelf() {
        animController.detach()
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = object : Dialog(context) {
        private var insetController: WindowInsetsControllerCompat? = null

        override fun onTouchEvent(event: MotionEvent): Boolean {
            if (isCancelable && event.action == MotionEvent.ACTION_DOWN) this@ExpandableFragment.dismiss()
            return super.onTouchEvent(event)
        }

        override fun onStart() {
            super.onStart()
            insetController?.isAppearanceLightNavigationBars = !context.isNightModeOn
            insetController?.isAppearanceLightStatusBars = !context.isNightModeOn
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            window?.let { insetController = WindowInsetsControllerCompat(it, it.decorView) }
            window?.apply {
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR)
                clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                statusBarColor = R.color.transparent.contextColor()
                navigationBarColor = R.color.transparent.contextColor()
                setLayout(MATCH_PARENT, MATCH_PARENT)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                animController.attachBackground(this)
            }
            setCanceledOnTouchOutside(false)
        }

    }

    abstract fun ViewGroup.onCreateCoordinatorView()

    private inner class SheetAnimationController {
        private val backgroundDrawable: ColorDrawable by lazy {
            ColorDrawable(R.color.background_dialog.contextColor()).apply {
                alpha = 0
                dialog?.window?.setBackgroundDrawable(this)
            }
        }
        private var currentSheetAnimation: AnimatorSet = AnimatorSet()
        fun attachBackground(window: Window) {
            window.setBackgroundDrawable(backgroundDrawable)
        }
        fun attach() {
            container.measure(View.MeasureSpec.makeMeasureSpec(displaySize.x, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(displaySize.y, View.MeasureSpec.AT_MOST))
            container.translationY = container.measuredHeight.toFloat()
            container.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            container.translationY = container.measuredHeight.toFloat()

            currentSheetAnimation.cancel()
            currentSheetAnimation = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(container, View.TRANSLATION_Y, 0f),
                )
                playTogether(
                    ValueAnimator.ofInt(0, 128).apply {
                        addUpdateListener {
                            backgroundDrawable.alpha = it.animatedValue as Int
                        }
                    }
                )
                duration = 480
                startDelay = 20
                interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
                doOnEnd {
                    if (currentSheetAnimation == it) {
//                        currentSheetAnimation = null
                        container.setLayerType(View.LAYER_TYPE_NONE, null)
                    }
                }
                start()
            }
        }
        fun detach() {
            currentSheetAnimation.cancel()
            currentSheetAnimation = AnimatorSet().apply {
                playTogether(
                    ObjectAnimator.ofFloat(container, View.TRANSLATION_Y, container.measuredHeight.toFloat()),
                )
                playTogether(
                    ValueAnimator.ofInt(128, 0).apply {
                        addUpdateListener {
                            backgroundDrawable.alpha = it.animatedValue as Int
                        }
                    }
                )
                duration = 480
                startDelay = 20
                interpolator = CubicBezierInterpolator.EASE_OUT_QUINT
                doOnEnd {
                    try {
                        super@ExpandableFragment.dismiss()
//                        isDismissed = true fixme
                    } catch (e: Throwable) { }
                }
                container.post { start() }
            }
        }
        private fun cancelSheetAnimation() {
            currentSheetAnimation.cancel()
        }
    }
}


abstract class AlertFragment() : ExpandableFragment() {

    abstract fun VerticalView.onCreateAlertView()

    final override fun ViewGroup.onCreateCoordinatorView() {
        verticalLayout {
            onCreateAlertView()
        }
    }

    inline fun ViewGroup.alertHeader(block: AlertHeaderView.() -> Unit) {
        view<AlertHeaderView> {
            layoutWidth = MATCH_PARENT
            layoutHeight = WRAP_CONTENT
            block()
        }
    }

    inline fun ViewGroup.alertBody(block: LazyListView.() -> Unit) {
        view<LazyListView> {
            layoutWidth = MATCH_PARENT
            layoutHeight = 0
            layoutWeightLinear = 1f
            block()
        }
    }

    inline fun ViewGroup.alertFooter(block: VerticalView.() -> Unit) {
        view<VerticalView> {
            layoutWidth = MATCH_PARENT
            layoutHeight = WRAP_CONTENT
            block()
        }
    }

}



class AlertHeaderView(context: Context) : StackView(context) {

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

    val titleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            typeface = typefaceExtraBold
            textColor = context.getColor(R.color.dialog_title)
            textSize = 24f
            gravity = Gravity.START or Gravity.TOP
            isSingleLine = true
            isVisible = false
        }
    }

    val subtitleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            typeface = typefaceExtraBold
            textColor = context.getColor(R.color.dialog_subtitle)
            textSize = 16f
            gravity = Gravity.START or Gravity.TOP
            isVisible = false
            isAllCaps = true
        }
    }

    val messageView: PlainTextView by lazy {
        PlainTextView(context).apply {
            // TODO: 2021/1/4 extract to extension
            setLinkTextColor(context.getColor(R.color.text_link))
            typeface = typefaceRegular
            textColor = context.getColor(R.color.text_primary)
            textSize = 16f
            gravity = Gravity.START or Gravity.TOP
            isVisible = false
        }
    }

    var message: CharSequence
        get() = messageView.text
        set(text) {
            messageView.isVisible = text.isNotEmpty()
            messageView.text = text
        }


    private val progressView: ImageView by lazyView()

    var state = DialogState.EMPTY
        set(value) {
            if (field != value) {
                field = value
                showProgressIcon(value)
            }
        }

    private val titleContainer: StackView by lazyView {
        updatePadding(R.dimen.dialog_padding_vertical.contextDimenPixelSize(), 4.dp, R.dimen.dialog_padding_vertical.contextDimenPixelSize(), 4.dp)
        viewRow(titleView) {
            layoutMarginEnd = 30.dp
            layoutGravityFrame = Gravity.START or Gravity.TOP
        }
        view(progressView) {
            layoutGravityFrame = Gravity.END or Gravity.TOP
        }
        viewRow(subtitleView) {
            layoutMarginTop = 28.dp
            layoutGravityFrame = Gravity.START or Gravity.TOP
        }
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


    init {
        addView(titleContainer)
    }
}


open class BottomDialogFragment : ExpandableFragment() {

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

    val titleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            typeface = typefaceExtraBold
            textColor = context.getColor(R.color.dialog_title)
            textSize = 24f
            gravity = Gravity.START or Gravity.TOP
            isSingleLine = true
            isVisible = false
        }
    }

    val subtitleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            typeface = typefaceExtraBold
            textColor = context.getColor(R.color.dialog_subtitle)
            textSize = 16f
            gravity = Gravity.START or Gravity.TOP
            isVisible = false
            isAllCaps = true
        }
    }

    val messageView: PlainTextView by lazy {
        PlainTextView(context).apply {
            // TODO: 2021/1/4 extract to extension
            setLinkTextColor(context.getColor(R.color.text_link))
            typeface = typefaceRegular
            textColor = context.getColor(R.color.text_primary)
            textSize = 16f
            gravity = Gravity.START or Gravity.TOP
            isVisible = false
        }
    }

    var message: CharSequence
        get() = messageView.text
        set(text) {
            messageView.isVisible = text.isNotEmpty()
            messageView.text = text
        }


    private val progressView: ImageView by lazyView()

    var state = DialogState.EMPTY
        set(value) {
            if (field != value) {
                field = value
                showProgressIcon(value)
            }
        }



    private val titleContainer: StackView by lazyView {
        updatePadding(R.dimen.dialog_padding_vertical.contextDimenPixelSize(), 4.dp, R.dimen.dialog_padding_vertical.contextDimenPixelSize(), 4.dp)
        viewRow(titleView) {
            layoutMarginEnd = 30.dp
            layoutGravityFrame = Gravity.START or Gravity.TOP
        }
        view(progressView) {
            layoutGravityFrame = Gravity.END or Gravity.TOP
        }
        viewRow(subtitleView) {
            layoutMarginTop = 28.dp
            layoutGravityFrame = Gravity.START or Gravity.TOP
        }
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

    override fun ViewGroup.onCreateCoordinatorView() {
        verticalLayout {
            noMotion()
            clipping()
            viewRow(titleContainer)
            viewRow(messageView) {
                layoutMarginStart = context.resources.getDimensionPixelSize(modulon.R.dimen.dialog_padding_vertical)
                layoutMarginEnd = context.resources.getDimensionPixelSize(modulon.R.dimen.dialog_padding_vertical)
            }
            viewRow(contentScrollView) {
                layoutWeightLinear = 1f
            }
            viewRow(containerButton)
        }
    }

    fun hide() = dialog?.hide()
    fun show() = dialog?.show()



    private val contentScrollView: StackView by lazyView {
//        isNestedScrollingEnabled = true
//        isVerticalScrollBarEnabled = false
    }

    private val containerButton: VerticalView by lazyView {
        setPadding(UI.SPACING.dp, UI.SPACING.dp, UI.SPACING.dp, UI.SPACING.dp)
        dividerDrawable = ShapeDrawable().apply {
            intrinsicHeight = 12.dp
            setTint(R.color.transparent.contextColor())
        }
        showDividers = SHOW_DIVIDER_MIDDLE or SHOW_DIVIDER_END
    }

    fun addButton(button: BottomDialogFragment.TextButton) {
        button.layoutWidth = MATCH_PARENT
        button.layoutHeight = context.resources.getDimensionPixelSize(R.dimen.dialog_button_height)
        containerButton.addView(button)
    }
    fun addButton(button: BottomDialogFragment.TextButton, index: Int) {
        button.layoutWidth = MATCH_PARENT
        button.layoutHeight = context.resources.getDimensionPixelSize(R.dimen.dialog_button_height)
        containerButton.addView(button, index)
    }
    fun removeButton(button: BottomDialogFragment.TextButton) {
        containerButton.removeView(button)
    }
    fun removeButtonAt(index: Int) {
        containerButton.removeViewAt(index)
    }

    val buttons
        get() = containerButton.children.toList()

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

    private var dismissCallback: OnDismissListener = { }
    private var showListener: OnShowListener = { }

    private val disableScroll = false

    var isDismissed = false
        private set

    fun show(manager: FragmentManager) {
        super.show(manager, null)
//        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
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

    override fun dismiss() {
        if (isCancelable && !isDismissed) {
            dismissCallback.invoke()
            super.dismiss()
        }
    }

    @Deprecated("Useless")
    var isCancelableByButtons = false
        get() = field && isCancelable

    private var viewCreatedListener: OnViewCreatedListener = { }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreatedListener.invoke()
    }
    fun setOnViewCreatedListener(listener: OnViewCreatedListener) {
        viewCreatedListener = listener
    }
    fun setOnShowListener(listener: OnShowListener) {
        dialog?.setOnShowListener { listener.invoke() }
    }

    var isCancelableTouchOutside = true
        get() = field && isCancelable
        set(value) {
            field = value
            dialog?.setCanceledOnTouchOutside(value)
            dialog?.setOnDismissListener { }
        }
}