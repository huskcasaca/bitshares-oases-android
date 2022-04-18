package modulon.extensions.stdlib

import android.util.Log

fun Any?.logcat() = Log.i("BitShares Oases", if (this == null) "null" else "${this::class.simpleName} $this")
