package modulon.component.appbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import modulon.R
import modulon.extensions.animation.visibilityLayoutTransition
import modulon.extensions.compat.activity
import modulon.extensions.compat.hideSoftKeyboard
import modulon.extensions.font.typefaceExtraBold
import modulon.extensions.graphics.createIconSelectorDrawable
import modulon.extensions.graphics.tint
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.stack.StackView
import modulon.layout.linear.HorizontalView
import modulon.layout.linear.VerticalView
import modulon.widget.PlainTextView
import modulon.widget.TextViewSwitcher

class AppbarView(context: Context) : StackView(context) {

    class Item(context: Context) : StackView(context) {

        val iconView: ImageView

        // TODO: 2022/2/17
        var text: CharSequence = ""

        var icon: Drawable?
            get() = iconView.drawable
            // TODO: 2022/2/28 change color
            set(drawable) = iconView.setImageDrawable(drawable?.tint(R.color.cell_text_primary.contextColor()))

        init {
            background = createIconSelectorDrawable(context.getColor(R.color.background))
            isClickable = true
            view<ImageView> {
                iconView = this
                layoutGravityFrame = Gravity.CENTER
            }
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(48.dp, MeasureSpec.EXACTLY)
            )
        }
    }

    companion object {
        // TODO: 2022/2/17 move to dimens.xml
        private const val TITLE_FONT_SCALED = 36f
        private const val TITLE_FONT_NORMAL = 22f
        private const val SUBTITLE_FONT_SCALED = 18f
        private const val SUBTITLE_FONT_NORMAL = 16f

        private const val TITLE_FONT_SCALE = TITLE_FONT_SCALED / TITLE_FONT_NORMAL
        private const val SUBTITLE_FONT_SCALE = SUBTITLE_FONT_SCALED / SUBTITLE_FONT_NORMAL

        private const val TITLES_Y_SCALE = 0.9f
        private val TITLE_Y_OFFSET = 64.dpf
        private val TITLE_X_OFFSET = 44.dpf

    }

    val actionButton: Item
    val iconView: ImageView
    private val titleView: TextViewSwitcher
    private val subtitleView: TextViewSwitcher
    private val titleSection: VerticalView
    private val menuSection: HorizontalView
    private val customSection: StackView
    private val components: StackView

    var icon: Drawable?
        get() = iconView.drawable
        set(icon) {
            iconView.isVisible = icon != null
            iconView.setImageDrawable(icon)
        }
    var title: CharSequence
        get() = titleView.text
        set(text) {
            titleView.isVisible = true
            titleView.text = text
        }
    var subtitle: CharSequence
        get() = subtitleView.text
        set(text) {
            subtitleView.isVisible = text.isNotBlank()
            subtitleView.text = text
        }
    var actionView: View? = null
        set(value) {
            customSection.removeAllViews()
            if (value != null) customSection.addView(value)
            field = value
        }

    var isCollapsed
        get() = !customSection.isVisible
        set(value) {
            if (value == isCollapsed) return
            val behavior = (layoutParams as? CoordinatorLayout.LayoutParams)?.behavior
            if (behavior is ActionBarBehavior) {
                behavior.animateToPositionLock(this, !value)
            }
            if (value) {
                customSection.isVisible = false
                titleSection.isVisible = true
                menuSection.isVisible = true
                collapseListeners.forEach { it.invoke() }
                //            avatarView.isVisible = toolbarAvatarSeed.isNotEmpty()
                // TODO: 10/10/2021 useless? test
                hideSoftKeyboard()
            } else {
                customSection.isVisible = true
                titleSection.isVisible = false
                menuSection.isVisible = false
                expandListeners.forEach { it.invoke() }
                //            avatarView.isVisible = false
            }
        }
    var isExpanded
        get() = !isCollapsed
        set(value) { isCollapsed = !value }

    var translation = -1
        set(value) {
            val scroll = value.coerceIn(0, 64.dp)
//            if (field == value) return
            val scrollY = scroll.toFloat()
            val factor = (1f - scrollY / TITLE_Y_OFFSET).coerceIn(0f..1f)
            translationY = -scrollY
            actionButton.translationY = scrollY
            menuSection.translationY = scrollY
            components.translationY = scrollY
            titleSection.apply {
                translationX = if (actionButton.isVisible) TITLE_X_OFFSET * (titleScaleInterpolator.getInterpolation(scrollY / TITLE_Y_OFFSET) - 1) else 0f
                translationY = TITLES_Y_SCALE * TITLE_Y_OFFSET * factor
            }
            titleView.forEach<PlainTextView> {
                it.marqueeRepeatLimit = if (factor != 0f && factor != 1f) 0 else -1
//                it.scaleX = ((TITLE_FONT_SCALE - 1) * factor + 1)
//                it.scaleY = ((TITLE_FONT_SCALE - 1) * factor + 1)
                it.textSize = TITLE_FONT_NORMAL * ((TITLE_FONT_SCALE - 1) * factor + 1)
            }
            subtitleView.forEach<PlainTextView> {
                it.marqueeRepeatLimit = if (factor != 0f && factor != 1f) 0 else -1
                it.textSize = SUBTITLE_FONT_NORMAL * ((SUBTITLE_FONT_SCALE - 1) * factor + 1)
            }
//            titleView.translationY = 2.dpf
            subtitleView.translationY = (-4).dpf * factor
            iconView.apply {
                translationY = TITLES_Y_SCALE * TITLE_Y_OFFSET * factor
                translationX = if (actionButton.isVisible) 40.dpf * (titleScaleInterpolator.getInterpolation(scrollY / TITLE_Y_OFFSET) - 1) else 0f
                scaleX = (1.2f - 1) * factor + 1
                scaleY = (1.2f - 1) * factor + 1
            }
            // TODO: 22/12/2021 after 64.dpf return
            field = scroll
        }

    val isOnBottom get() = translation == 0 && translationY == 0f
    val isOnTop get() = translation == 64.dp && translationY == -64.dp.toFloat()

    private val collapseListeners = mutableListOf<() -> Unit>()
    private val expandListeners = mutableListOf<() -> Unit>()

    private val titleScaleInterpolator = DecelerateInterpolator(1.5f)


    init {
        clipChildren = false
        clipToPadding = false
        layoutTransition = null
        backgroundTintColor = context.getColor(R.color.background) // TODO: 1/2/2022 backgroundColor as var

        frameLayout {
            clipChildren = false
            clipToPadding = false
            layoutTransition = null
            setPadding(12.dp, 8.dp, 8.dp, 12.dp)
            view<Item> {
                actionButton = this
                layoutMarginStart = -6.dp
                layoutGravityFrame = Gravity.TOP or Gravity.START
                icon = R.drawable.ic_cell_back_arrow.contextDrawable()
                doOnClick { if (isExpanded) collapseActionView() else activity.onBackPressed() }
            }
            frameLayout {
                components = this
                layoutWidth = MATCH_PARENT
                layoutHeight= 48.dp
                layoutGravityFrame = Gravity.TOP or Gravity.START
                noClipping()
                noMotion()
                verticalLayout {
                    titleSection = this
                    noClipping()
                    layoutAnimation = null
                    layoutTransition = visibilityLayoutTransition
                    layoutGravityFrame = Gravity.CENTER_VERTICAL or Gravity.START
                    view<TextViewSwitcher> {
                        titleView = this
                        setFactory {
                            create<PlainTextView> {
                                textSize = TITLE_FONT_SCALED
                                typeface = typefaceExtraBold
                                startScrolling()
                                textColor = context.getColor(R.color.title)
                            }
                        }
// TODO: 16/1/2022 animation removed
//        inAnimation = animationSet {
//            translate((-16).dpf, 0f)
//            alpha(0f, 1f)
//            startOffset = 120
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
//        outAnimation = animationSet {
//            translate(0f, 16.dpf)
//            alpha(1f, 0f)
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
                    }
                    view<TextViewSwitcher> {
                        subtitleView = this
                        setFactory {
                            create<PlainTextView> {
                                textSize = SUBTITLE_FONT_SCALED
                                typeface = typefaceExtraBold
                                startScrolling()
                                textColor = context.getColor(R.color.subtitle)
                            }
                        }
                        isVisible = false
// TODO: 16/1/2022 animation removed
//        inAnimation = animationSet {
//            translate((-16).dpf, 0f)
//            alpha(0f, 1f)
//            startOffset = 120
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
//        outAnimation = animationSet {
//            translate(0f, 16.dpf)
//            alpha(1f, 0f)
//            duration = 180
//            interpolator = DecelerateInterpolator()
//        }
                    }
                }
                view<ImageView> {
                    iconView = this
                    layoutWidth = 40.dp
                    layoutHeight = 40.dp
                    layoutGravityFrame = Gravity.CENTER_VERTICAL or Gravity.START
                    isVisible = false
//        val backgroundColor = context.getColor(R.color.background_cover)
//        val backgroundRadius = 44.dpf / 10
//        background = createRoundRectDrawable(backgroundColor, backgroundRadius)
                }
                view<StackView> {
                    customSection = this
                    isVisible = false
                    layoutWidth = MATCH_PARENT
                    layoutHeight = MATCH_PARENT
                }
            }
            horizontalLayout {
                menuSection = this
                layoutGravityFrame = Gravity.TOP or Gravity.END
                noClipping()
                noMotion()
            }
        }

        translation = 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(128.dp, MeasureSpec.EXACTLY)
        )
        components.updatePadding(if (actionButton.isVisible) actionButton.measuredWidth + 8.dp else 12.dp)
        menuSection.measure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )
        titleSection.updatePadding(if (iconView.isVisible) (52.dp * iconView.scaleX).toInt() else 0)
        val menuWidth = if (menuSection.isNotEmpty()) menuSection.measuredWidth + 8.dp else 12.dp
        val a = titleScaleInterpolator.getInterpolation(1f - titleSection.translationY / TITLES_Y_SCALE / TITLE_Y_OFFSET)
        val toMinus = ((actionButton.measuredWidth + menuWidth) * a).toInt() + 12.dp
        titleSection.measure(
            MeasureSpec.makeMeasureSpec(components.measuredWidth - toMinus , MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.AT_MOST)
        )
        if (fitsSystemWindows) {
            setMeasuredDimension(
                measuredWidth,
                measuredHeight + (rootWindowInsets?.systemWindowInsetTop ?: 0),
            )
        }
    }

    fun setActionBarView(view: View, params: ViewGroup.LayoutParams? = null) {
        customSection.removeAllViews()
        customSection.addView(view, params)
    }

    @Deprecated("")
    fun collapseActionView() {
        isCollapsed = true
    }

    @Deprecated("")
    fun expandActionView() {
        isExpanded = true
    }

    // listeners
    fun addOnCollapseListener(listener: () -> Unit) {
        collapseListeners.add(listener)
    }

    fun clearOnCollapseListeners() {
        collapseListeners.clear()
    }

    fun addOnExpandListener(listener: () -> Unit) {
        expandListeners.add(listener)
    }

    fun clearOnExpandListeners() {
        expandListeners.clear()
    }

    fun addMenu(item: Item) {
        menuSection.addView(item)
    }

    fun removeMenuItem(item: Item) {
        menuSection.addView(item)
    }

}


