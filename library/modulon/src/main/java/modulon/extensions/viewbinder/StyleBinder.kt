package modulon.extensions.viewbinder

import android.graphics.drawable.GradientDrawable
import modulon.R
import modulon.component.cell.ComponentPaddingCell
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceExtraBold
import modulon.extensions.view.dp
import modulon.extensions.view.updatePaddingBottom
import modulon.extensions.view.updatePaddingStart
import modulon.extensions.view.updatePaddingTop

fun ComponentPaddingCell.titleCellStyle() {
    updatePaddingTop(8.dp)
    updatePaddingBottom(5.dp)
    titleView.apply {
        textColor = R.color.cell_text_secondary.contextColor()
        textSize = 15f
        typeface = typefaceBold
        isAllCaps = true
    }
    background = GradientDrawable(GradientDrawable.Orientation.TR_BL, intArrayOf(R.color.background_component.contextColor(), R.color.background_dark.contextColor()))
}


fun ComponentPaddingCell.headerCellStyle() {
    updatePaddingTop(0.dp)
    updatePaddingBottom(6.dp)
    updatePaddingStart(10.dp)
    titleView.apply {
        textColor = R.color.cell_text_secondary.contextColor()
        textSize = 19f
        typeface = typefaceExtraBold
    }
    background = null
}