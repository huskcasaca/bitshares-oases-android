package modulon.extensions.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import modulon.layout.frame.FrameLayout
import modulon.layout.recycler.ViewSize
import modulon.layout.recycler.containers.DefaultContainer
import modulon.layout.recycler.containers.ListContainer
import modulon.layout.recycler.containers.StaticContainer
import modulon.union.toUnion

class ZViewHolder(context: Context): RecyclerView.ViewHolder(FrameLayout(context)) {
    fun replace(view: View) {
        itemView as ViewGroup
        itemView.removeAllViews()
        itemView.addView(view)
    }
}

class AdapterBuilder<T : View>(private val creator: () -> T) {

    private lateinit var positionBinder: T.(Int) -> Unit
    private lateinit var itemCountCallback: () -> Int

    fun <T : View> AdapterBuilder<T>.onBindData(block: T.(Int) -> Unit) {
        positionBinder = block
    }

    fun <T : View> AdapterBuilder<T>.onCountItem(block: () -> Int) {
        itemCountCallback = block
    }

    fun build(): RecyclerView.Adapter<ZViewHolder> {
        return object : RecyclerView.Adapter<ZViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZViewHolder = ZViewHolder(parent.context)
            override fun onBindViewHolder(holder: ZViewHolder, position: Int) = holder.replace(creator().apply { positionBinder(this, position) })
            override fun getItemCount(): Int = itemCountCallback.invoke()
        }
    }
}


internal fun createEmptyAdapter() = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = throw IllegalArgumentException("Empty ViewHolder")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = Unit
    override fun getItemCount(): Int = 0
}

fun ViewPager2.attachEmptyAdapter() {
    adapter = createEmptyAdapter()
}

inline fun <reified C: View, D> ViewPager2.pageList(block: ListContainer<C, D>.() -> Unit) {
    if (adapter !is ConcatAdapter) adapter = ConcatAdapter()
    (adapter as ConcatAdapter).addAdapter(ListContainer<C, D>(context.toUnion()::create).apply(block).adapter)
}

inline fun <reified V: View> ViewPager2.page(block: V.() -> Unit) {
    if (adapter !is ConcatAdapter) adapter = ConcatAdapter()
    (adapter as ConcatAdapter).addAdapter(DefaultContainer(context.toUnion().create(block), ViewSize.FILL).adapter)
}

inline fun <reified C : View, D : Any> ViewPager2.addStatic(data: List<D>, crossinline binder: C.(D) -> Unit = {}) {
    if (adapter !is ConcatAdapter) adapter = ConcatAdapter()
    (adapter as ConcatAdapter).addAdapter(StaticContainer<C, D>(data, { context.toUnion().create() }, { binder.invoke(this, it) }).adapter)
}
