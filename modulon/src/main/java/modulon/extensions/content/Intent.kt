package modulon.extensions.content

import android.content.Intent
import android.os.Bundle
import java.io.Serializable


// TODO: 2022/2/19 remove
@Deprecated("replace with Json")
fun Bundle?.optBoolean(key: String, fallback: Boolean) = this?.getBoolean(key) ?: fallback



val Intent.extrasSafe get() = extras ?: Bundle()

fun <T: Serializable> Intent.getExtra(name: String): T = runCatching { extrasSafe.get(name) as T }.getOrThrow()
fun <T: Serializable> Intent.getExtraOrNull(name: String): T? = runCatching { extrasSafe.get(name) as T }.getOrNull()
fun <T: Serializable> Intent.getExtra(name: String, defaultValue: T): T = runCatching { extras!!.get(name) as T }.getOrDefault(defaultValue)

