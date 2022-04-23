package bitshareskit.extensions

import android.view.ViewGroup
import androidx.core.view.children
import graphene.extension.info
import java.math.BigDecimal

// FIXME: 2022/4/16
fun BigDecimal.stripTrailingZerosFixes(): BigDecimal = if (compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else stripTrailingZeros()

@Deprecated("removed")
fun ViewGroup.printAllChildren() = printAllChildrenInternal(0)
@Deprecated("removed")
private fun ViewGroup.printAllChildrenInternal(intend: Int) {
    children.forEachIndexed { index, view ->
        "=== ChildPrinter === ${" ".repeat(4 * intend)} [${index}]${view::class.simpleName}".info()
        if (view is ViewGroup) view.printAllChildrenInternal(intend + 1)
    }
}



