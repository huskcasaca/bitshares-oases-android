package modulon.extensions.stdlib

import android.util.Log

fun logcatUI(message: Any?) = Log.i("*** ***", message.toString())
fun logcatUI(vararg message: Any?) = Log.i("*** ***", message.toList().toString())