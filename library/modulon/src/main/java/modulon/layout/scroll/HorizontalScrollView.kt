package modulon.layout.scroll

import android.content.Context
import android.widget.HorizontalScrollView
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class HorizontalScrollView(context: Context): HorizontalScrollView(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}