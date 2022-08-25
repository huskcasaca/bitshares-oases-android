package modulon.extensions.stdlib

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children

fun Any?.logcat() = Log.i("BitShares Oases", if (this == null) "null" else "${this::class.simpleName} $this")

@Deprecated("removed")
fun ViewGroup.printAllChildren() = printAllChildrenInternal(0)
@Deprecated("removed")
private fun ViewGroup.printAllChildrenInternal(intend: Int) {
    children.forEachIndexed { index, view ->
        "=== ChildPrinter === ${" ".repeat(4 * intend)} [${index}]${view::class.simpleName}".logcat()
        if (view is ViewGroup) view.printAllChildrenInternal(intend + 1)
    }
}



