package modulon.layout.lazy.section

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import modulon.R
import modulon.extensions.view.isolated
import modulon.extensions.view.view
import modulon.extensions.viewbinder.headerCellStyle
import modulon.layout.lazy.*
import modulon.layout.lazy.containers.SectionItemContainer
import modulon.union.UnionContext
import modulon.union.toUnion

abstract class HeaderSection(context: Context) : FrameLayout(context), Section, UnionContext by context.toUnion() {

    protected val adapterInternal = ConcatAdapter()

    val adapter = ConcatAdapter().apply {
        addAdapter(adapterInternal)
    }

    abstract override fun addContainer(block: LazyListView.Container<*>)

    override fun addContainer(block: LazyListView.Container<*>, index: Int) {
        // FIXME: 2022/5/13 index
        addContainer(block)
    }
    override fun addView(child: View, index: Int) {
        addContainer(SectionItemContainer(child, null, true), index)
    }
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        addContainer(SectionItemContainer(child, params, true), index)
    }
    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams?, preventRequestLayout: Boolean): Boolean {
        addContainer(SectionItemContainer(child, params, true))
        return true
    }

    override fun setVisibility(visibility: Int) {
        // FIXME: 2022/5/3
        if (visibility == View.VISIBLE) {
            if (adapter.adapters.size == 0) {
                adapter.addAdapter(adapterInternal)
            }
        } else {
            if (adapter.adapters.size == 1) {
                adapter.removeAdapter(adapterInternal)
            }
        }
    }
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}

// TODO: 2022/2/15 rename
class HeaderSectionImpl(context: Context) : HeaderSection(context) {

    private var headerCell: RecyclerHeader

    override fun addContainer(block: LazyListView.Container<*>) {
        adapterInternal.addAdapter(block.adapter)
    }

    var header: CharSequence
        get() = headerCell.title
        set(value) {
            headerCell.isVisible = value.isNotEmpty()
            headerCell.title = value
        }

    init {
        // FIXME: 2022/2/22
        isolated<RecyclerHeaderSpacer> {
            backgroundColor = R.color.transparent.contextColor()
        }
        isolated<RecyclerHeader> {
            headerCellStyle()
            headerCell = this
            isVisible = false
        }
    }

}
