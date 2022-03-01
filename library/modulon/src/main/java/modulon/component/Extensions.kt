package modulon.component

import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.children
import androidx.core.view.isVisible
import modulon.R
import modulon.widget.PlainTextView
import modulon.widget.ToggleView
import modulon.extensions.view.*
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.font.typefaceBold
import modulon.extensions.view.getChildAt
import modulon.extensions.view.textWithVisibility
import modulon.extensions.viewbinder.subtextStyle

fun ComponentCell.toggleEnd(block: ToggleView.() -> Unit) {
    (customViewEnd as ToggleView?)?.block() ?: run {
        customViewEnd = create<ToggleView>().apply {
            setColors(context.getColor(R.color.component), context.getColor(R.color.component_inactive))
            block()
        }
    }
}

fun ComponentCell.buttonStyle() {
    titleView.apply {
        isVisible = true
        typeface = typefaceBold
        textColor = context.getColor(R.color.component)
    }
}

var ComponentCell.isButtonEnabled: Boolean
    get() = titleView.textColor == context.getColor(R.color.component)
    set(value) {
        titleView.textColor = if (value) context.getColor(R.color.component) else context.getColor(R.color.component_inactive)

    }

fun TableLayout.row(key: CharSequence = EMPTY_SPACE, value: CharSequence = EMPTY_SPACE) {
    viewNoParams<TableRow> {
        view<PlainTextView> {
            subtextStyle()
            textWithVisibility = key
            layoutParams = TableRow.LayoutParams(0).apply {
                rightMargin = 4.dp
            }
        }
        view<PlainTextView> {
            subtextStyle()
            textWithVisibility = value
            layoutParams = TableRow.LayoutParams(1)
        }
    }
}

fun TableLayout.row(index: Int, key: CharSequence, value: CharSequence) {
    while (index >= childCount) row()
    getChildAt<TableRow>(index).apply {
        getChildAt<PlainTextView>(0).apply { textWithVisibility = key }
        getChildAt<PlainTextView>(1).apply { textWithVisibility = value }
    }
}

var TableLayout.tables: Map<CharSequence, CharSequence>
    get() = children.map {
        (it as ViewGroup).getChildAt<PlainTextView>(0).text to it.getChildAt<PlainTextView>(0).text
    }.toMap()
    set(value) {
        removeAllViews()
        value.forEach(::row)
    }
