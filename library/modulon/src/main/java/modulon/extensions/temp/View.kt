package modulon.extensions.temp

import android.graphics.Paint
import android.graphics.Rect
import modulon.R
import modulon.component.BaseCell
import modulon.extensions.view.dpf
import modulon.layout.recycler.decorations.drawAllShaders
import modulon.layout.tab.TabLayout
import modulon.widget.FloatingButton

fun BaseCell.drawShaders() {
    val bounds = Rect()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val shaderCenter = R.color.shader_center_floating.contextColor()
    val shaderEnd = R.color.shader_end.contextColor()
    val backgroundColor = R.color.background.contextColor()
    val radius = modulon.UI.CORNER_RADIUS.dpf
    val shaderSize = R.dimen.global_corner_shader.contextDimen()

    doOnDraw { canvas ->
        // getDrawingRect getFocusedRect getHitRect
        getDrawingRect(bounds)
        drawAllShaders(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
    }

}

fun TabLayout.TabView.drawShaders() {
    val bounds = Rect()
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val shaderCenter = R.color.shader_center_floating.contextColor()
    val shaderEnd = R.color.shader_end.contextColor()
    val backgroundColor = R.color.background.contextColor()
    val shaderSize = R.dimen.global_corner_shader.contextDimen()

    doOnDraw { canvas ->
        // getDrawingRect getFocusedRect getHitRect
        getFocusedRect(bounds)
        bounds.inset(R.dimen.global_corner_shader.contextDimenPixelOffset(), R.dimen.global_corner_shader.contextDimenPixelOffset())

        // FIXME: 2022/2/28 java.lang.IllegalArgumentException: ending radius must be > 0
        val radius = (bounds.bottom - bounds.top) / 2f
        if (radius > 0f) {
            drawAllShaders(canvas, bounds, paint, radius, shaderSize, shaderCenter, shaderEnd, backgroundColor)
        }
    }

}
