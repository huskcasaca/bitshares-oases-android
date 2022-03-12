package modulon.layout.frame

import android.content.Context
import android.widget.FrameLayout
import modulon.extensions.compat.activity
import modulon.extensions.compat.isForceDarkAllowedCompat

import modulon.union.UnionContext

open class FrameLayout(context: Context): FrameLayout(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
    init { isForceDarkAllowedCompat = false }

    init {

        this.activity
    }
}