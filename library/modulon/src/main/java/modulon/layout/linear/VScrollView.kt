package modulon.layout.linear

import android.content.Context
import android.widget.ScrollView
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class VScrollView(context: Context): ScrollView(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}