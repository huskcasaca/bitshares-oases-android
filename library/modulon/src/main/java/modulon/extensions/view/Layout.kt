package modulon.extensions.view

import android.view.View
import android.view.ViewGroup
import modulon.extensions.stdlib.logcat
import modulon.union.UnionContext
import modulon.union.toUnion
import kotlin.reflect.jvm.isAccessible

// TODO: 2022/2/22 replace first() with the correct constructor
inline fun <reified V: View> UnionContext.create(block: V.() -> Unit = {} ): V {
    return V::class.constructors.first().apply { isAccessible = true }.call(context).apply(block)
}

// TODO: 9/12/2021 apply to all
inline fun <reified V: View> UnionContext.lazyView(crossinline block: V.() -> Unit = {} ): Lazy<V> {
    return lazy { create(block) }
}

inline fun <reified V: View> ViewGroup.viewRow(block: V.() -> Unit = {}) {
    addDefaultRow(toUnion().create(block))
}

inline fun <reified V: View> ViewGroup.viewFill(block: V.() -> Unit = {}) {
    addDefaultFill(toUnion().create(block))
}

inline fun <reified V: View> ViewGroup.view(block: V.() -> Unit = {} ) {
    addDefault(toUnion().create(block))
}

inline fun <reified V: View> ViewGroup.viewNoParams(block: V.() -> Unit = {}) {
    addNoParams(toUnion().create(block))
}