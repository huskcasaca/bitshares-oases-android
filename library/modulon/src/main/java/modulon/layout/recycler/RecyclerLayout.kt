package modulon.layout.recycler

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import modulon.R
import modulon.layout.recycler.containers.*
import modulon.layout.recycler.decorations.ItemHolderDispatcher
import modulon.layout.recycler.manager.FixedLinearLayoutManager
import modulon.layout.recycler.section.Section
import modulon.layout.recycler.decorations.SeparatorOverlay
import modulon.layout.recycler.decorations.ShaderOverlay
import modulon.union.UnionContext
import modulon.union.toUnion

// TODO: 2022/4/28 rename
class RecyclerLayout(context: Context) : RecyclerView(context), Section, UnionContext by context.toUnion() {

    companion object {
        val EMPTY_PAYLOAD = Any()
    }

    var isOnTouch = false
        private set

    private val shaderOverlay = ShaderOverlay(context)
    private val separatorOverlay = SeparatorOverlay(context)

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
        clipChildren = false
        clipToPadding = false
        adapter = ConcatAdapter().apply {

        }
        layoutManager = FixedLinearLayoutManager(context)
        itemAnimator = DefaultItemAnimator()
//


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            edgeEffectFactory = EdgeEffectFactory()
        } else {
            edgeEffectFactory = BounceEdgeEffectFactory(VERTICAL)
        }

        setPadding(context.resources.getDimensionPixelSize(R.dimen.global_spacer_size), 0, context.resources.getDimensionPixelSize(R.dimen.global_spacer_size), 0)
//        addItemDecoration(shaderOverlay)
        addItemDecoration((ItemHolderDispatcher(context)))
        addItemDecoration(separatorOverlay)


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

    // prevent logcat output
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

