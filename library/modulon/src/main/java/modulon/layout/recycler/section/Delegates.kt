package modulon.layout.recycler.section

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import modulon.R
import modulon.component.ComponentCell
import modulon.extensions.viewbinder.headerCellStyle
import modulon.layout.recycler.*
import modulon.layout.recycler.containers.DefaultContainer
import modulon.layout.recycler.containers.ExpandableContainer
import modulon.union.UnionContext
import modulon.union.toUnion

abstract class SectionDelegate(context: Context) : ViewGroup(context), Section, UnionContext by context.toUnion() {

    protected val adapterInternal = ConcatAdapter()

    val adapter = ConcatAdapter().apply {
        addAdapter(adapterInternal)
    }

    abstract override fun addContainer(block: RecyclerLayout.Container<*>)
    override fun addContainer(block: RecyclerLayout.Container<*>, index: Int) {
        addContainer(block)
    }

    override fun addView(child: View) {
        addContainer(DefaultContainer(child, ViewSize.ROW))
    }

    override fun addView(child: View, index: Int) {
        addContainer(DefaultContainer(child, ViewSize.ROW), index)
    }

    override fun addView(child: View, params: LayoutParams) {
        addContainer(DefaultContainer(child, ViewSize.ROW, params))
    }

    override fun setVisibility(visibility: Int) {
        if (visibility == View.VISIBLE) {
            adapter.addAdapter(adapterInternal)
        } else {
            adapter.removeAdapter(adapterInternal)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    }
}

// TODO: 2022/2/15 rename
class HeaderSectionDelegate(context: Context) : SectionDelegate(context) {

    lateinit var headerContainer: ExpandableContainer<RecyclerHeader>
    lateinit var headerCell: RecyclerHeader

    private var isInit = false

    override fun addContainer(block: RecyclerLayout.Container<*>) {
        if (isInit) {
            adapterInternal.addAdapter((adapterInternal.adapters.size - 1).coerceAtLeast(0), block.adapter)
        } else {
            adapterInternal.addAdapter(block.adapter)
        }
    }
    var header: CharSequence
        get() = headerCell.title
        set(value) {
            headerContainer.isExpanded = value.isNotEmpty()
            headerCell.title = value
        }

    init {
        // FIXME: 2022/2/22
        expandable<RecyclerHeaderSpacer> {
            construct {
                backgroundColor = R.color.transparent.contextColor()
            }
        }
        locator(RecyclerContentLocator.SpacerType.TOP)
        locator(RecyclerContentLocator.SpacerType.BOTTOM)
        isInit = true
        expandable<RecyclerHeader> {
            headerContainer = this
            isExpanded = false
            construct {
                headerCellStyle()
                headerCell = this
            }
        }
    }

}
