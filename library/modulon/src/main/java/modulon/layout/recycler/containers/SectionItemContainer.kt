package modulon.layout.recycler.containers

import android.content.Context
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.RecyclerView
import modulon.R
import modulon.extensions.view.MATCH_PARENT
import modulon.extensions.view.WRAP_CONTENT
import modulon.extensions.view.dpf
import modulon.layout.recycler.RecyclerLayout
import modulon.layout.recycler.decorations.ItemHolderDispatcher
import modulon.layout.frame.FrameLayout

class SectionItemContainer(
    private val view: View,
    private val params: ViewGroup.LayoutParams? = null
) : RecyclerLayout.Container<View>() {

    init {
        if (params != null) {
            view.layoutParams = params
        }
    }

    override var creator: () -> View = { view }

    override val adapter = object : RecyclerView.Adapter<SectionHolder>() {
        override fun onBindViewHolder(holder: SectionHolder, position: Int) {}
        override fun onBindViewHolder(holder: SectionHolder, position: Int, dropped: MutableList<Any>) {}
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionHolder =
            SectionHolder(parent.context).apply {
                (view.parent as? ViewGroup)?.removeView(view)
                container.addView(view)
            }
        override fun getItemCount() = 1
    }
}

// TODO: 2022/4/26
open class SectionHolder(context: Context) : RecyclerView.ViewHolder(
    SectionItem(context)
) {

    val container get() = itemView as SectionItem

    val _drawType: ItemHolderDispatcher.CellShader = ItemHolderDispatcher.CellShader(false, false)
    var drawType: ItemHolderDispatcher.CellShader
        get() = _drawType
        set(value) {
            _drawType.top = value.top
            _drawType.bottom = value.bottom
            container.drawType = _drawType
        }

}

class SectionItem(context: Context) : FrameLayout(context) {

    var drawType: ItemHolderDispatcher.CellShader = ItemHolderDispatcher.CellShader(false, false)
        set(value) {
            if (value != field) {
//                outlineProvider = sectionOutlineProvider
                field = value
//                invalidateOutline()
//                "invalidateOutline".logcat()
            }

        }

    private val sectionOutlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, 0.dpf)
            if (drawType.top) {
                outline.setRoundRect(0, 0, view.width, view.height, 10.dpf)
            }
            if (drawType.bottom) {
                outline.setRoundRect(0, 0, view.width, view.height, 20.dpf)
            }
        }
    }

    init {
        clipToOutline = true
        clipChildren = false
        clipToPadding = false
//        outlineProvider = sectionOutlineProvider
        layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
    }
    // TODO: 2022/2/8 add shaderColor to cells
    private val shaderEnd = context.getColor(R.color.shader_end)
    private val shaderCenter = context.getColor(R.color.component)

    private val backgroundColor = context.getColor(R.color.background)

    private val bounds = Rect()
    private val paint  = Paint(Paint.ANTI_ALIAS_FLAG)

    private val radius = modulon.UI.CORNER_RADIUS.dpf

    private val shaderSize = R.dimen.global_corner_shader.contextDimen()
    val child: View? get() = getChildAt(0)

    fun replace(view: View) {
        removeAllViews()
        addView(view)
    }

}
