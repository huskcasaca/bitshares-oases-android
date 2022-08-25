package modulon.extensions.viewbinder

import android.view.ViewGroup
import modulon.component.cell.ComponentSpacerCell
import modulon.component.cell.ComponentCell
import modulon.component.cell.ComponentHintCell
import modulon.extensions.view.addDefaultRow
import modulon.widget.FieldTextView
import modulon.widget.PlainTextView

// ComponentCell
inline fun ViewGroup.cell(block: ComponentCell.() -> Unit) = addDefaultRow(ComponentCell(context).apply(block))
inline fun ViewGroup.title(block: ComponentCell.() -> Unit) = addDefaultRow(ComponentCell(context).apply { titleCellStyle() }.apply(block))

inline fun ViewGroup.hint(block: ComponentHintCell.() -> Unit) = addDefaultRow(ComponentHintCell(context).apply(block))

inline fun ViewGroup.spacer(block: ComponentSpacerCell.() -> Unit = {}) = addView(ComponentSpacerCell(context).apply(block))

inline fun ViewGroup.plainText(block: PlainTextView.() -> Unit) = addView(PlainTextView(context).apply(block))
inline fun ViewGroup.fieldText(block: FieldTextView.() -> Unit) = addView(FieldTextView(context).apply(block))
