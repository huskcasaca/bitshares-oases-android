package modulon.layout.linear

import android.content.Context
import android.widget.LinearLayout
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class LinearView(context: Context) : LinearLayout(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}
