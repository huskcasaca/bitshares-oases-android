package modulon.component

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.ImageView
import androidx.core.view.isVisible
import modulon.R
import modulon.extensions.graphics.createSelectorDrawable
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.component.CellComponent
import modulon.layout.frame.FrameLayout
import modulon.widget.PlainTextView

abstract class AbstractCell(context: Context) : FrameLayout(context), CellComponent {

    // TODO: 3/10/2021  lineSpacingMultiplier changed!
    override val titleView: PlainTextView by lazyView {
        isVisible = false
        titleStyle()
        startScrolling()
    }

    override val subtitleView: PlainTextView by lazyView {
        isVisible = false
        subtitleStyle()
        startScrolling()
        gravity = Gravity.END
    }

    override val textView: PlainTextView by lazyView {
        isVisible = false
        textStyle()
        isSingleLine = false
    }

    override val subtextView: PlainTextView by lazyView {
        isVisible = false
        subtextStyle()
    }

    open val iconView: ImageView by lazyView()

    override var title: CharSequence
        get() = titleView.text
        set(text) {
            titleView.textWithVisibility = text
        }

    override var subtitle: CharSequence
        get() = subtitleView.text
        set(text) {
            subtitleView.textWithVisibility = text
        }

    override var text: CharSequence
        get() = textView.text
        set(text) {
            textView.textWithVisibility = text
        }

    override var subtext: CharSequence
        get() = subtextView.text
        set(text) {
            subtextView.textWithVisibility = text
        }

    open var icon: Drawable?
        get() = iconView.drawable
        set(icon) {
            iconView.setImageDrawable(icon)
            iconView.isVisible = icon != null
        }

    open var iconSize: IconSize = IconSize.TINY

    protected val containerSubviews= createVerticalLayout()

    fun AbstractCell.subtext(index: Int, block: PlainTextView.() -> Unit) {
        while (containerSubviews.getChildOrNullAt<PlainTextView>(index) == null) {
            containerSubviews.view<PlainTextView> {
                subtextStyle()
                lineSpacingExtra = 3.dpf
            }
        }
        containerSubviews.getChildAt<PlainTextView>(index).block()
    }

    private var isCheckViewInitialized = false

    open val checkView: FrameLayout by lazyView {
        isCheckViewInitialized = true
        isVisible = false
        backgroundTintColor = context.getColor(R.color.component)
    }

    open var backgroundColor = context.getColor(R.color.background)
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("setBackgroundColorKt") set(value) {
            field = value
            background = createSelectorDrawable(backgroundColor)
        }

    val componentOffset = context.resources.getDimensionPixelOffset(R.dimen.component_offset)
    val componentOffsetHalf = context.resources.getDimensionPixelOffset(R.dimen.component_offset) / 2

}