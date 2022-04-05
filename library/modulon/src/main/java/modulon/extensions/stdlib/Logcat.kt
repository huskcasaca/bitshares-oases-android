package modulon.extensions.stdlib

import android.util.Log

fun Any?.logcat() = if (this == null) Log.i("logloglog", "NULL") else Log.i("logloglog", this::class.simpleName + " " + this.toString())
