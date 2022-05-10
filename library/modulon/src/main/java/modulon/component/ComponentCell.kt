package modulon.component

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableLayout
import androidx.core.view.*
import modulon.R
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.frame.FrameLayout
import modulon.layout.linear.HorizontalLayout
import modulon.layout.linear.VerticalLayout
import modulon.widget.*

class ComponentCell(context: Context) : ComponentPaddingCell(context) {

    override val iconView: ImageView by lazy { ImageView(context) }

    private val titleContainer: FrameLayout by lazy { FrameLayout(context) }
    private val textContainer: FrameLayout by lazy { FrameLayout(context) }
    private val subtextContainer: FrameLayout by lazy { FrameLayout(context) }

    private val containerBody: VerticalLayout by lazy { VerticalLayout(context) }
    private val containerText: FrameLayout by lazy { FrameLayout(context) }

    private val containerForm: VerticalLayout by lazy { VerticalLayout(context) }
    private val containerStart: FrameLayout
    private val containerEnd: FrameLayout by lazy { FrameLayout(context) }


    override var title: CharSequence
        get() = titleView.text
        set(text) {
            titleContainer.isVisible = text.isNotEmpty() || subtitle.isNotEmpty()
            titleView.text = text
        }

    override var subtitle: CharSequence
        get() = subtitleView.text
        set(text) {
            titleContainer.isVisible = text.isNotEmpty() || title.isNotEmpty()
            subtitleView.text = text
        }

    override var text: CharSequence
        get() = textView.text
        set(text) {
            textContainer.isVisible = text.isNotEmpty()
            textView.text = text
        }

    override var iconSize: IconSize = IconSize.TINY
        set(value) {
//            if (::iconView.isLazyInitialized) {
                iconView.layoutWidth = resources.getDimensionPixelSize(value.size)
                iconView.layoutHeight = resources.getDimensionPixelSize(value.size)
//            }
            field = value
        }

    // TODO: 22/1/2022 remove
    open var isChecked = false
        set(value) {
            checkView.isVisible = value
            field = value
        }

    var allowMultiLine = false
        set(value) {
            field = value
            // FIXME: 2022/5/8  
//            requestLayout()
        }

    // TODO: 21/1/2022 avoid conflicts
    var customView: View? = null
        set(value) {
            field = value
            containerForm.removeView(field)
            if (value != null) containerForm.addView(value)
        }

    var customViewStart: View? = null
        set(value) {
            field = value
            containerStart.removeView(field)
            containerStart.isVisible = value != null
            if (value != null) containerStart.addView(value)
        }

    var customViewEnd: View? = null
        set(value) {
            field = value
            containerEnd.removeView(field)
            containerEnd.isVisible = value != null
            if (value != null) containerEnd.addView(value)
        }

    init {
        noClipping()
        noMotion()
        horizontalLayout {
            noClipping()
            noMotion()
            view<FrameLayout> {
                noMotion()
                noClipping()
                containerStart = this
                isVisible = false
                layoutMarginEnd = componentOffset
                layoutGravityLinear = Gravity.START or Gravity.CENTER_VERTICAL
            }
            view(iconView) {
                isVisible = false
                layoutHeight = resources.getDimensionPixelSize(iconSize.size)
                layoutWidth = resources.getDimensionPixelSize(iconSize.size)
                layoutMarginEnd = componentOffset
                layoutGravityLinear = Gravity.START or Gravity.CENTER_VERTICAL
            }
            // TODO: 23/1/2022 consider ui time consumption
            view(containerForm) {
                noMotion()
                noClipping()
                layoutWidth = 0
                layoutHeight = ViewGroup.LayoutParams.WRAP_CONTENT
                layoutWeightLinear = 1f
                layoutGravityLinear = Gravity.START or Gravity.CENTER_VERTICAL
                viewRow(titleContainer) {
                    isVisible = false
                    viewRow(titleView) {
                        layoutGravityFrame = Gravity.START or Gravity.CENTER_VERTICAL
                    }
                    viewRow(subtitleView) {
                        gravity = Gravity.END
                        layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
                    }
                }
                viewRow(textContainer) {
                    isVisible = false
                    viewRow(textView)
                }
                viewRow(subtextContainer) {
                    isVisible = false
                    viewRow(subtextView)
                }
                viewRow(containerBody) {
                    noClipping()
                    isVisible = false
                }
                viewRow(subviewsContainer)
            }
            view(containerEnd) {
                noMotion()
                noClipping()
                isVisible = false
                layoutMarginStart = componentOffset
                layoutGravityLinear = Gravity.END or Gravity.CENTER_VERTICAL
            }
        }
        view(checkView) {
            noMotion()
            noClipping()
            layoutWidth = 6.dp
            layoutHeight = MATCH_PARENT
            layoutMarginStart = -paddingStart
            layoutMarginTop = -paddingTop
            layoutMarginEnd = -paddingEnd
            layoutMarginBottom = -paddingBottom
            layoutGravityFrame = Gravity.CENTER_VERTICAL
        }
        backgroundSelectorColor = R.color.background_component.contextColor()
    }

    override fun setPadding(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPadding(start, top, end, bottom)
        checkView.apply {
            layoutWidth = 6.dp
            layoutHeight = MATCH_PARENT
            layoutMarginStart = -start
            layoutMarginTop = -top
            layoutMarginEnd = -end
            layoutMarginBottom = -bottom
            layoutGravityFrame = Gravity.CENTER_VERTICAL
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )

        val available = MeasureSpec.getSize(widthMeasureSpec) - paddingStart - paddingEnd
        subtitleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        titleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

        if (subtitleView.measuredWidth + titleView.measuredWidth + componentOffsetHalf > available) {
            if (allowMultiLine) {
                subtitleView.updatePadding(top = titleView.measuredHeight)
                subtitleView.layoutGravityFrame = Gravity.START or Gravity.CENTER_VERTICAL
                subtitleView.gravity = Gravity.START
                titleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                subtitleView.measure(MeasureSpec.makeMeasureSpec(available, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
//                containerHeader.updateViewLayout(subtitleView, subtitleView.layoutParams)
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                )
            } else {
                subtitleView.updatePadding(top = 0)
                subtitleView.layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
                subtitleView.gravity = Gravity.END
                titleView.measure(MeasureSpec.makeMeasureSpec(available * 4 / 5, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                subtitleView.measure(MeasureSpec.makeMeasureSpec(available - titleView.measuredWidth - componentOffsetHalf, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            }
        } else {
            subtitleView.updatePadding(top = 0)
            subtitleView.layoutGravityFrame = Gravity.END or Gravity.CENTER_VERTICAL
            subtitleView.gravity = Gravity.END
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
        }
    }

    fun ComponentCell.customHorizontal(block: ComponentCustomHorizontalLayout.() -> Unit = {}) {
        containerBody.isVisible = true
        containerBody.viewRow(create(block)) {
            layoutTransition = null
            noClipping()
            updatePaddingTop(2.dp)
        }
    }

    fun ComponentCell.field(block: FieldTextView.() -> Unit = {}) {
        customHorizontal { field(block) }
    }
    fun ComponentCell.slider(block: SliderView.() -> Unit = {}) {
        customHorizontal { slider(block) }
    }
    fun ComponentCell.table(block: TableLayout.() -> Unit = {}) {
        customHorizontal { table(block) }
    }

    fun ComponentCustomHorizontalLayout.field(block: FieldTextView.() -> Unit = {}) {
        view<FieldTextView> {
            block()
            layoutWidth = 0
            layoutMarginStart = (-4).dp
            layoutMarginEnd = (-4).dp
            layoutWeightLinear = 1f
            layoutGravityLinear = Gravity.CENTER_VERTICAL
        }
    }

    fun ComponentCustomHorizontalLayout.slider(block: SliderView.() -> Unit = {}) {
        view<SliderView> {
            block()
            layoutWidth = 0
            layoutHeight = 32.dp
            layoutMarginStart = (-12).dp
            layoutMarginEnd = (-12).dp
            layoutWeightLinear = 1f
            layoutGravityLinear = Gravity.CENTER_VERTICAL
        }
    }

    fun ComponentCustomHorizontalLayout.text(block: PlainTextView.() -> Unit = {}) {
        view<PlainTextView> {
            block()
            layoutMarginStart = componentOffset
            layoutGravityLinear = Gravity.CENTER_VERTICAL
        }
    }

    fun ComponentCustomHorizontalLayout.toggle(block: ToggleView.() -> Unit) {
        view<ToggleView> {
            block()
            setColors(context.getColor(R.color.component), context.getColor(R.color.component_inactive))
            expandTouchArea(32.dp, 32.dp, 32.dp, 32.dp)
            layoutMarginStart = componentOffset
            layoutGravityLinear = Gravity.CENTER_VERTICAL
        }
    }
    // TODO: 2022/4/26
    fun ComponentCustomHorizontalLayout.table(block: TableLayout.() -> Unit = {}) {
        view<TableLayout> {
            block()
            layoutWidth = MATCH_PARENT
            layoutGravityLinear = Gravity.CENTER_VERTICAL
        }
    }
    class ComponentCustomHorizontalLayout(context: Context) : HorizontalLayout(context)

    val ComponentCell.tableView: TableLayout
        get() {
            containerBody.forEach<ComponentCustomHorizontalLayout> { it.forEach<View> { if (it is TableLayout) return it } }
            table()
            containerBody.forEach<ComponentCustomHorizontalLayout> { it.forEach<View> { if (it is TableLayout) return it } }
            throw NullPointerException("Tableview Notfound")
        }

}