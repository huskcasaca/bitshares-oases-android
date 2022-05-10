package modulon.layout.linear

import android.content.Context
import android.widget.HorizontalScrollView
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class HScrollView(context: Context): HorizontalScrollView(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}