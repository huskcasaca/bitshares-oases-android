package com.bitshares.oases.extensions.viewbinder

import android.text.TextUtils
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.bitshares.oases.R
import modulon.component.cell.BaseCell
import modulon.component.cell.ComponentCell
import modulon.extensions.font.typefaceBold
import modulon.extensions.font.typefaceDinMedium
import modulon.extensions.graphics.createSelectorDrawable
import modulon.extensions.view.*
import modulon.extensions.viewbinder.horizontalLayout
import modulon.extensions.viewbinder.startScrolling

fun ComponentCell.setTickerStyle() {
    textView.apply {
        translationY += 8.dp
        isSingleLine = true
        typeface = typefaceBold
        textSize = 48f
        alpha = 0.15f

        layoutWidth = WRAP_CONTENT
        layoutHeight = WRAP_CONTENT
        layoutGravityFrame = Gravity.END or Gravity.BOTTOM
    }
    titleView.apply {
        textSize = 22f
        startScrolling()
    }
    subtitleView.apply {
        textSize = 22f
        ellipsize = TextUtils.TruncateAt.END
    }
    textView.parentViewGroup.removeView(textView)
    addView(textView)
}

fun BaseCell.setTickerItemStyle() {
    setPadding(context.resources.getDimensionPixelSize(R.dimen.cell_padding_start), 0, context.resources.getDimensionPixelSize(R.dimen.cell_padding_end), 0)
    layoutAnimation = null
    layoutTransition = null
    horizontalLayout {
        layoutAnimation = null
        layoutTransition = null
        layoutWidth = MATCH_PARENT
        view(titleView) {
            isVisible = true
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            gravity = Gravity.START
            textSize = 15.5f
            typeface = typefaceDinMedium

            layoutWidth = 0
            layoutMarginEnd = 2.dp
            layoutWeightLinear = 1f
            layoutGravityLinear = Gravity.START
        }
        view(subtitleView) {
            isVisible = true
            ellipsize = TextUtils.TruncateAt.END
            isSingleLine = true
            gravity = Gravity.END
            textSize = 15.5f
            typeface = typefaceDinMedium

            layoutWidth = 0
            layoutMarginEnd = 2.dp
            layoutWeightLinear = 1f
            layoutGravityLinear = Gravity.END
        }
    }
}

fun ComponentCell.setDrawerItemStyle() {
    updatePadding(top = 14.dp, bottom = 14.dp, left = context.resources.getDimensionPixelSize(modulon.R.dimen.navigation_drawer_padding_vertical), right = context.resources.getDimensionPixelSize(modulon.R.dimen.navigation_drawer_padding_vertical))
    background = createSelectorDrawable(R.color.background_component.contextColor(), floatArrayOf(Float.MAX_VALUE, Float.MAX_VALUE, 0f, 0f))
    titleView.typeface = titleView.typefaceBold
}