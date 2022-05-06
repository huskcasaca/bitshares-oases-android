package modulon.layout.recycler.section

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.ConcatAdapter
import modulon.R
import modulon.extensions.viewbinder.headerCellStyle
import modulon.layout.recycler.*
import modulon.layout.recycler.containers.DefaultContainer
import modulon.layout.recycler.containers.ExpandableContainer
import modulon.union.UnionContext
import modulon.union.toUnion

abstract class HeaderSection(context: Context) : FrameLayout(context), Section, UnionContext by context.toUnion() {

    protected val adapterInternal = ConcatAdapter()

    val adapter = ConcatAdapter().apply {
        addAdapter(adapterInternal)
    }

    abstract override fun addContainer(block: RecyclerLayout.Container<*>)
    override fun addContainer(block: RecyclerLayout.Container<*>, index: Int) {
        addContainer(block)
    }

    override fun addView(child: View) {
        addContainer(DefaultContainer(child, ))
    }

    override fun addView(child: View, index: Int) {
        addContainer(DefaultContainer(child), index)
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams?) {
        addContainer(DefaultContainer(child, params))
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
        expandable<RecyclerHeader> {
            headerContainer = this
            isExpanded = false
            construct {
                headerCellStyle()
                headerCell = this
            }
        }
        locator(RecyclerContentLocator.SpacerType.TOP)
        locator(RecyclerContentLocator.SpacerType.BOTTOM)

        isInit = true
    }

}
