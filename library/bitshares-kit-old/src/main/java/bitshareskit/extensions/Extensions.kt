package bitshareskit.extensions

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import graphene.extension.info
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.math.BigDecimal

@Deprecated("removed")
fun Boolean?.orFalse() = this ?: false
@Deprecated("removed")
fun Boolean?.orTrue() = this ?: true

@Deprecated("removed")
fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
    return this != null && isNotEmpty()
}

// FIXME: 2022/4/16
fun BigDecimal.stripTrailingZerosFixes(): BigDecimal = if (compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else stripTrailingZeros()

@Deprecated("removed")
inline fun <reified T: Any?> Any?.asOrNull(): T? = this as? T

@Deprecated("removed")
inline fun <T> T?.ifNull(block: () -> T): T = this ?: block.invoke()


// debug only
@Deprecated("removed")
fun logcat(vararg message: Any?) {
//    runCatching {
//        Log.i("*** ***", message.toList().toString())
//    }.onFailure {
//        (listOf(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>") + message.toList()).info()
//    }
}

@Deprecated("removed")
fun ViewGroup.printAllChildren() = printAllChildrenInternal(0)

@Deprecated("removed")
private fun ViewGroup.printAllChildrenInternal(intend: Int) {
    children.forEachIndexed { index, view ->
        logcat("=== ChildPrinter === ${" ".repeat(4 * intend)} [${index}]${view::class.simpleName}")
        if (view is ViewGroup) view.printAllChildrenInternal(intend + 1)
    }
}



