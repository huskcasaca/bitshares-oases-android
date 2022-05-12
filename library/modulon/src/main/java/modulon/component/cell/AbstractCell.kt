package modulon.component.cell

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.view.isVisible
import modulon.R
import modulon.extensions.graphics.createSelectorDrawable
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.component.CellComponent
import modulon.layout.stack.StackView
import modulon.widget.PlainTextView

abstract class AbstractCell(context: Context) : StackView(context), CellComponent {

    // TODO: 3/10/2021  lineSpacingMultiplier changed!
    override val titleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            titleStyle()
//            startScrolling()
        }
    }

    override val subtitleView: PlainTextView by lazy {
        PlainTextView(context).apply {
            subtitleStyle()
//            startScrolling()
        }
    }

    override val textView: PlainTextView by lazy {
        PlainTextView(context).apply {
            textStyle()
            isSingleLine = false
        }
    }

    override val subtextView: PlainTextView by lazy {
        PlainTextView(context).apply {
            subtextStyle()
        }
    }

    open val iconView: ImageView by lazy {
        ImageView(context).apply {
        }
    }

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

    protected val subviewsContainer= createVerticalLayout()

    fun AbstractCell.subtext(index: Int, block: PlainTextView.() -> Unit) {
        while (subviewsContainer.getChildOrNullAt<PlainTextView>(index) == null) {
            subviewsContainer.view<PlainTextView> {
                subtextStyle()
                lineSpacingExtra = 3.dpf
            }
        }
        subviewsContainer.getChildAt<PlainTextView>(index).block()
    }

    private var isCheckViewInitialized = false

    open val checkView: StackView by lazy {
        StackView(context).apply {
            isCheckViewInitialized = true
            isVisible = false
            backgroundTintColor = context.getColor(R.color.component)
        }
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