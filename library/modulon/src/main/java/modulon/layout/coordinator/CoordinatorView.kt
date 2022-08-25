package modulon.layout.coordinator

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import modulon.union.UnionContext

open class CoordinatorView(context: Context): CoordinatorLayout(context), UnionContext {
    final override val context: Context @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("getContextUnion") get() = getContext()
}