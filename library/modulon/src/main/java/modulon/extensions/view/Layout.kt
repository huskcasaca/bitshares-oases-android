package modulon.extensions.view

import android.view.View
import android.view.ViewGroup
import modulon.union.UnionContext

inline fun <reified V: View> ViewGroup.viewRow(block: V.() -> Unit = {} ) {
    addDefaultRow((V::class.java.constructors.first().newInstance(context) as V).apply(block))
}

inline fun <reified V: View> ViewGroup.viewFill(block: V.() -> Unit = {} ) {
    addDefaultFill((V::class.java.constructors.first().newInstance(context) as V).apply(block))
}

inline fun <reified V: View> ViewGroup.view(block: V.() -> Unit = {} ) {
    addDefault((V::class.java.constructors.first().newInstance(context) as V).apply(block))
}

inline fun <reified V: View> ViewGroup.viewNoParams(block: V.() -> Unit = {} ) {
    addNoParams((V::class.java.constructors.first().newInstance(context) as V).apply(block))
}

// TODO: 2022/2/22 replace first() with the correct constructor
inline fun <reified V: View> UnionContext.create(block: V.() -> Unit = {} ): V {
    return (V::class.java.constructors.first().newInstance(context) as V).apply(block)
}

// TODO: 9/12/2021 apply to all
inline fun <reified V: View> UnionContext.lazyView(crossinline block: V.() -> Unit = {} ): Lazy<V> {
    return lazy { create(block) }
}