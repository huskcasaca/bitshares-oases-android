package modulon.extensions.view

import android.view.View
import android.view.ViewGroup
import modulon.component.cell.ComponentCell
import modulon.extensions.stdlib.logcat
import modulon.layout.lazy.containers.LazySectionItemContainer
import modulon.layout.lazy.containers.RawItemContainer
import modulon.layout.lazy.containers.SectionItemContainer
import modulon.layout.lazy.section.HeaderSection
import modulon.union.UnionContext
import modulon.union.toUnion
import modulon.widget.PlainTextView
import java.util.HashMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

// TODO: 2022/2/22 replace first() with the correct constructor
@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> UnionContext.create(block: V.() -> Unit = {} ): V {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    return V::class.constructors.first().apply { isAccessible = true }.call(context).apply(block)
}

// TODO: 9/12/2021 apply to all
inline fun <reified V: View> UnionContext.lazyView(crossinline block: V.() -> Unit = {}): Lazy<V> {
    return lazy { create(block) }
}

@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> ViewGroup.view(block: V.() -> Unit = {} ) {
//    addDefault(toUnion().create(block))
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addView(toUnion().create(block))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> HeaderSection.view(block: V.() -> Unit = {} ) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addContainer(SectionItemContainer(toUnion().create(block), null, true))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> HeaderSection.lazyView(crossinline block: V.() -> Unit = {} ) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addContainer(LazySectionItemContainer({ toUnion().create(block) }, true))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> HeaderSection.isolated(block: V.() -> Unit = {} ) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addContainer(SectionItemContainer(toUnion().create(block), null, false))
}

@OptIn(ExperimentalContracts::class)
inline fun <reified V: View> HeaderSection.raw(block: V.() -> Unit = {} ) {
//    addDefault(toUnion().create(block))
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    addContainer(RawItemContainer(toUnion().create(block)))
}