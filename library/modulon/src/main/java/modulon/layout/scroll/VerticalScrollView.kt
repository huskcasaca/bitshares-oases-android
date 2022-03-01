package modulon.layout.scroll

import android.content.Context
import android.widget.ScrollView
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class VerticalScrollView(context: Context): ScrollView(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}