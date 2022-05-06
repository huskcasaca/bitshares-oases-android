package modulon.union

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import modulon.extensions.view.*

abstract class UnionFragment : Fragment(), Union {

    final override val parentFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getParentFragmentManagerUnion")
        get() = getParentFragmentManager()

    final override val childFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getChildFragmentManagerUnion")
        get() = getChildFragmentManager()

    final override val lifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    final override val lifecycleScope: LifecycleCoroutineScope get() = viewLifecycleOwner.lifecycleScope

    final override val activityResultCaller: ActivityResultCaller
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultCallerUnion")
        get() = this
    final override val activityResultRegistry: ActivityResultRegistry
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultRegistryUnion")
        get() = (this as ActivityResultRegistryOwner).activityResultRegistry

    final override val activity: Activity get() = requireActivity()
    final override val union: Union get() = this
    final override val context: Context
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getContextUnion")
        get() = getContext()


    @SuppressLint("UseRequireInsteadOfGet")
    final override fun getContext(): Context = super.getContext()!!


    // TODO: 2022/2/18 move to extensions
    protected class FragmentStateAdapterBuilder(private val fragmentManager: FragmentManager, private val lifecycle: Lifecycle) {
        private lateinit var counter: () -> Int
        private lateinit var creator: (position: Int) -> Fragment
        fun FragmentStateAdapterBuilder.onCountItem(block: () -> Int) {
            counter = block
        }
        fun FragmentStateAdapterBuilder.onCreateFragment(block: (position: Int) -> Fragment) {
            creator = block
        }
        internal fun build() = object : FragmentStateAdapter(fragmentManager, lifecycle) {
            override fun getItemCount() = counter.invoke()
            override fun createFragment(position: Int) = creator.invoke(position)
        }
    }

    // TODO: 2022/2/18 move to extensions
    protected fun ViewPager2.attachFragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, block: FragmentStateAdapterBuilder.() -> Unit) {
        adapter = FragmentStateAdapterBuilder(fragmentManager, lifecycle).apply(block).build()
    }

    // TODO: 2022/2/18 move to extensions
    @Deprecated("removed")
    protected inline fun <reified T : Enum<T>> ViewPager2.attachFragmentEnumsAdapter(crossinline block: (enum: T) -> Fragment) {
        val values = enumValues<T>()
        attachFragmentAdapter(childFragmentManager, lifecycle) {
            onCountItem { values.size }
            onCreateFragment { block.invoke(values[it]) }
        }
//        offscreenPageLimit = values.size.coerceAtLeast(1)
        offscreenPageLimit = 2

    }

    // TODO: 2022/2/18 move to extensions
    protected inline fun <D> ViewPager2.attachFragmentListAdapter(data: List<D>, crossinline block: (value: D) -> Fragment) {
        attachFragmentAdapter(childFragmentManager, lifecycle) {
            onCountItem { data.size }
            onCreateFragment { block.invoke(data[it]) }
        }
        offscreenPageLimit = data.size.coerceAtLeast(1)
    }

    // TODO: 2022/2/18 move to extensions
    protected inline fun <D> ViewPager2.attachFragmentListAdapter(data: Array<D>, crossinline block: (value: D) -> Fragment) {
        attachFragmentAdapter(childFragmentManager, lifecycle) {
            onCountItem { data.size }
            onCreateFragment { block.invoke(data[it]) }
        }
//        offscreenPageLimit = data.size.coerceAtLeast(1)
        offscreenPageLimit = 2
    }

    // TODO: 2022/2/18 move to extensions
    protected inline fun <reified V: View, D> ViewPager2.attachStaticAdapter(data: List<D>, crossinline block: V.(D) -> Unit) {
        adapter = AdapterBuilder {
            context.toUnion().create<V>().apply {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
            }
        }.apply {
            onCountItem { data.size }
            onBindData { block.invoke(this, data[it]) }
        }.build()
    }

    // TODO: 2022/2/18 move to extensions
    protected inline fun <reified V: View, D> ViewPager2.attachStaticAdapter(data: Array<D>, crossinline block: V.(D) -> Unit) {
        adapter = AdapterBuilder<V> {
            context.toUnion().create<V>().apply {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
            }
        }.apply {
            onCountItem { data.size }
            onBindData { block.invoke(this, data[it]) }
        }.build()
    }



}