package modulon.layout.lazy

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import modulon.UI.ENABLE_SHADER
import modulon.layout.lazy.containers.*
import modulon.layout.lazy.decorations.HolderGroupPositionDispatcher
import modulon.layout.lazy.decorations.SeperatorDecoration
import modulon.layout.lazy.manager.FixedLinearLayoutManager
import modulon.layout.lazy.section.Section
import modulon.union.UnionContext
import modulon.union.toUnion

// TODO: 2022/4/28 rename
class LazyListView(context: Context) : RecyclerView(context), Section, UnionContext by context.toUnion() {

    companion object {
        val EMPTY_PAYLOAD = Any()
    }

    var isOnTouch = false
        private set

//    private val separatorOverlay = SeparatorOverlay(context)

    override fun getAdapter(): ConcatAdapter {
        return super.getAdapter() as ConcatAdapter
    }
    override fun setAdapter(adapter: Adapter<*>?) {
        if (adapter is ConcatAdapter) {
            super.setAdapter(adapter)
        } else {
            throw IllegalArgumentException("ConcatAdapter required!")
        }
    }

    init {
        clipToOutline = true
        clipChildren = true
        clipToPadding = true
        adapter = ConcatAdapter()
        layoutManager = FixedLinearLayoutManager(context)
        itemAnimator = DefaultItemAnimator()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            edgeEffectFactory = EdgeEffectFactory()
//        } else {
//            edgeEffectFactory = BounceEdgeEffectFactory(VERTICAL)
//        }
        edgeEffectFactory = EdgeEffectFactory()

        if (ENABLE_SHADER) {
            addItemDecoration(HolderGroupPositionDispatcher(context))
        }
        addItemDecoration(SeperatorDecoration(context))


    }

    override fun addView(child: View) {
        addContainer(SectionItemContainer(child))
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addContainer(SectionItemContainer(child, params))
    }

    override fun removeAllViews() {
        // TODO: 2022/2/8 test
        with(adapter) {
            adapters.forEach {
                removeAdapter(it)
            }
        }
    }

    // FIXME prevent log output
    override fun scrollTo(x: Int, y: Int) { }

    override fun addContainer(block: Container<*>) {
        adapter.addAdapter(block.adapter)
    }

    override fun addContainer(block: Container<*>, index: Int) {
        adapter.addAdapter(index, block.adapter)
    }

    abstract class Container<C : View> {
        protected open var creator: () -> C = { throw IllegalArgumentException() }
        abstract val adapter: Adapter<*>
    }

}

