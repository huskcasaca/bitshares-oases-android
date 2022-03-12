package bitshareskit.extensions

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.children
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.contracts.contract

// TODO: 2022/2/15 remove
fun delayMain(millis: Long, block: () -> Unit){
    CoroutineScope(Dispatchers.Main).launch {
        delay(millis)
        block.invoke()
    }
}

fun Boolean?.orFalse() = this ?: false
fun Boolean?.orTrue() = this ?: true

fun <T> Collection<T>?.isNotNullOrEmpty(): Boolean {
// TODO: removed
//    contract {
//        returns(true) implies (this@isNotNullOrEmpty != null)
//    }
    return this != null && isNotEmpty()
}

fun BigDecimal.stripTrailingZerosFixes(): BigDecimal = if (compareTo(BigDecimal.ZERO) == 0) BigDecimal.ZERO else stripTrailingZeros()

inline fun <reified T: Any?> Any?.asOrNull(): T? = this as? T

inline fun <T> T?.ifNull(block: () -> T): T = this ?: block.invoke()


// debug only

fun logcat(message: Any?) = Log.i("*** ***", message.toString())
fun logcat(vararg message: Any?) = Log.i("*** ***", message.toList().toString())
fun Any?.logloglog() = if (this == null) Log.i("logloglog", "NULL") else Log.i("logloglog", this::class.simpleName + " " + this.toString())

fun ViewGroup.printAllChildren() = printAllChildrenInternal(0)

private fun ViewGroup.printAllChildrenInternal(intend: Int) {
    children.forEachIndexed { index, view ->
        logcat("=== ChildPrinter === ${BLANK_SPACE.repeat(4 * intend)} [${index}]${view::class.simpleName}")
        if (view is ViewGroup) view.printAllChildrenInternal(intend + 1)
    }
}



