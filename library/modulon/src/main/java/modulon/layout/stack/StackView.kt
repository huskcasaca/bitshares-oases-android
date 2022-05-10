package modulon.layout.stack

import android.content.Context
import android.widget.FrameLayout
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class StackView(context: Context): FrameLayout(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }
}