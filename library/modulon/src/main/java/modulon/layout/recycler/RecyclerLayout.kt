package modulon.layout.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import modulon.R
import modulon.extensions.viewbinder.noClipping
import modulon.layout.recycler.containers.*
import modulon.layout.recycler.manager.FixedLinearLayoutManager
import modulon.layout.recycler.section.Section
import modulon.layout.recycler.decorations.SeparatorOverlay
import modulon.layout.recycler.decorations.ShaderOverlay
import modulon.union.UnionContext
import modulon.union.toUnion

class RecyclerLayout(context: Context) : RecyclerView(context), Section, UnionContext by context.toUnion() {

    companion object {
        val EMPTY_PAYLOAD = Any()
    }

    var isOnTouch = false
        private set

    private val shaderOverlay = ShaderOverlay(context)
    private val separatorOverlay = SeparatorOverlay(context)

    init {
        noClipping()

        layoutManager = FixedLinearLayoutManager(context)
        itemAnimator = DefaultItemAnimator()
        adapter = ConcatAdapter()
        edgeEffectFactory = BounceEdgeEffectFactory(VERTICAL)

        setItemViewCacheSize(12)
        setPadding(context.resources.getDimensionPixelSize(R.dimen.global_spacer_size), 0, context.resources.getDimensionPixelSize(R.dimen.global_spacer_size), 0)
        addItemDecoration(shaderOverlay)
        addItemDecoration(separatorOverlay)

    }

    override fun addView(child: View) {
        addContainer(DefaultContainer(child, ViewSize.ROW))
    }

    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        addContainer(DefaultContainer(child, ViewSize.ROW, params))
    }

    override fun removeAllViews() {
        // TODO: 2022/2/8 test
        (adapter as ConcatAdapter?)?.apply {
            adapters.forEach { removeAdapter(it) }
        }
        adapter = ConcatAdapter()
    }

    // prevent logcat output
    override fun scrollTo(x: Int, y: Int) { }

    override fun addContainer(block: Container<*>) {
        (adapter as ConcatAdapter).addAdapter(block.adapter)
    }

    override fun addContainer(block: Container<*>, index: Int) {
        (adapter as ConcatAdapter).addAdapter(index, block.adapter)
    }

    abstract class Container<C : View> {
        protected open var creator: () -> C = { throw IllegalArgumentException() }
        abstract val adapter: Adapter<*>
    }

}

