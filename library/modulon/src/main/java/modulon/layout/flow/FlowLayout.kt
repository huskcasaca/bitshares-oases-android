package modulon.layout.flow

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import modulon.extensions.view.dp
import modulon.layout.linear.LinearLayout

class FlowLayout(context: Context) : LinearLayout(context) {

    // TODO: 18/9/2021 concurrent
    private val horizontalSpacing = 2.dp
    private val verticalSpacing = 2.dp
    private var lineHeight = 0

    override fun generateDefaultLayoutParams(): LayoutParams {
        return TagLayoutParams(horizontalSpacing, verticalSpacing)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return TagLayoutParams(horizontalSpacing, verticalSpacing)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams) = p is TagLayoutParams

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        val width = r - l
        var xPos = paddingLeft
        var yPos = paddingTop

        children.forEach { child ->
            if (child.visibility != View.GONE) {
                val childWidth = child.measuredWidth
                val childHeight = child.measuredHeight

                val lp = child.layoutParams as TagLayoutParams
                if (xPos + childWidth > width) {
                    xPos = paddingLeft
                    yPos += lineHeight
                }

                child.layout(xPos, yPos, xPos + childWidth, yPos + childHeight)
                xPos += childWidth + lp.horizontalSpacing
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var height = View.MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        var lineHeight = 0

        var xPos = paddingLeft
        var yPos = paddingTop

        val childHeightMeasureSpec: Int
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        } else {
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        }

        children.forEach { child ->
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as TagLayoutParams
                child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), childHeightMeasureSpec)
                val childWidth = child.measuredWidth
                lineHeight = Math.max(lineHeight, child.measuredHeight + lp.verticalSpacing)

                if (xPos + childWidth > width) {
                    xPos = paddingLeft
                    yPos += lineHeight
                }
                xPos += childWidth + lp.horizontalSpacing
            }
        }

        this.lineHeight = lineHeight
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            height = yPos + lineHeight

        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            if (yPos + lineHeight < height) {
                height = yPos + lineHeight
            }
        }
        setMeasuredDimension(width, height)
    }

    class TagLayoutParams(
        val horizontalSpacing: Int,
        val verticalSpacing: Int
    ) : LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}


