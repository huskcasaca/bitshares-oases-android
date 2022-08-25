package modulon.union

import android.content.Context
import android.view.View
import modulon.union.UnionContext

// TODO: 2022/2/10 remove toUnion() for some objects
fun Context.toUnion(): UnionContext = object : UnionContext {
    override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = this@toUnion
}
fun View.toUnion(): UnionContext = object : UnionContext {
    override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = this@toUnion.context
}


//fun Context.asUnion(block: UnionContext.() -> Unit): UnionContext = object : UnionContext {
//    override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = this@asUnion
//}.apply(block)
//
//
//fun FragmentActivity.toUnion() = object : Union {
//    override val parentFragmentManager: FragmentManager
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getParentFragmentManagerUnion")
//        get() = supportFragmentManager
//
//    override val childFragmentManager: FragmentManager
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getChildFragmentManagerUnion")
//        get() = supportFragmentManager
//
//    override val lifecycleOwner: LifecycleOwner get() = this@toUnion
//    override val lifecycleScope: LifecycleCoroutineScope get() = this@toUnion.lifecycleScope
//
//    override val activityResultCaller: ActivityResultCaller get() = this@toUnion
//    override val activityResultRegistry: ActivityResultRegistry get() = this@toUnion.activityResultRegistry
//
//    override val activity: Activity get() = this@toUnion
//    override val union: Union get() = this
//    override val context: Context
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getContextUnion")
//        get() = this@toUnion
//}
//
//fun Fragment.toUnion() = object : Union {
//    override val parentFragmentManager: FragmentManager
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getParentFragmentManagerUnion")
//        get() = getParentFragmentManager()
//
//    override val childFragmentManager: FragmentManager
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getChildFragmentManagerUnion")
//        get() = getChildFragmentManager()
//
//    override val lifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
//    override val lifecycleScope: LifecycleCoroutineScope get() = this@toUnion.lifecycleScope
//
//    override val activityResultCaller: ActivityResultCaller get() = this@toUnion
//    override val activityResultRegistry: ActivityResultRegistry get() = requireActivity().activityResultRegistry
//
//    override val activity: Activity get() = requireActivity()
//    override val union: Union get() = this
//    override val context: Context
//        @Suppress("INAPPLICABLE_JVM_NAME")
//        @JvmName("getContextUnion")
//        get() = requireContext()
//}

