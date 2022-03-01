package modulon.component

import android.content.Context
import androidx.core.view.isVisible
import modulon.R
import modulon.widget.PlainTextView
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.extensions.view.addRow

class ComponentHintCell(context: Context) : ComponentPaddingCell(context) {

    override val textView = create<PlainTextView> {
        subtextStyle()
        isVisible = false
//        typeface = typefaceBold
        textColor = context.getColor(R.color.cell_text_hint)
        // TODO: 31/1/2022 extract to dimens with headder cell or combine them?
        setPadding(16.dp, 4.dp, 16.dp, 2.dp)
        textSize = 14.5f

    }

    override var text: CharSequence
        get() = super.text
        set(value) {
            super.text = value
        }

    init {
        setPadding(0, 0, 0, 0)
        addRow(textView)
        backgroundColor = context.getColor(R.color.transparent)
    }

}
