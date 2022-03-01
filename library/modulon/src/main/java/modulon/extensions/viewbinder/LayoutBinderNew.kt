package modulon.extensions.viewbinder

import android.view.ViewGroup
import modulon.component.ComponentSpacerCell
import modulon.component.ComponentCell
import modulon.component.ComponentHintCell
import modulon.extensions.view.addDefaultRow

// ComponentCell
inline fun ViewGroup.cell(block: ComponentCell.() -> Unit) = addDefaultRow(ComponentCell(context).apply(block))

inline fun ViewGroup.hint(block: ComponentHintCell.() -> Unit) = addDefaultRow(ComponentHintCell(context).apply(block))

inline fun ViewGroup.spacer(block: ComponentSpacerCell.() -> Unit = {}) = addDefaultRow(ComponentSpacerCell(context).apply(block))