package modulon.extensions.content

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResult

@Deprecated("removed")
object IntentStack {

    private const val FALLBACK_HASHCODE = 0

    const val INDEX_KEY = "shared_pool_index"

    private val pool = HashMap<Int, Any?>()

    fun <V> setContent(value: V): Int {
        val hashcode = value.hashCode()
        pool[hashcode] = value
        return hashcode
    }

    fun <V> get(key: Int): V {
        return (pool[key] as V).also {
            pool.remove(key)
        }
    }

    fun <V> getOrDefault(key: Int, default: V): V {
        return (pool.getOrDefault(key, default) as V).also {
            pool.remove(key)
        }
    }

    fun <V> getOrNull(key: Int): V? {
        return (pool.getOrDefault(key, null) as V?).also {
            pool.remove(key)
        }
    }


    fun <V> resolveOrNull(result: ActivityResult): V? {
        return runCatching { get<V>(result.data!!.getIntExtra(INDEX_KEY, FALLBACK_HASHCODE)) }.getOrNull()
    }

    fun <V> resolve(result: ActivityResult): V {
        return get(result.data!!.getIntExtra(INDEX_KEY, FALLBACK_HASHCODE))
    }

    fun <V> resolveOrDefault(result: ActivityResult, fallback: V): V {
        return if (result.resultCode == Activity.RESULT_OK && fallback != null) getOrDefault(result.data!!.getIntExtra(INDEX_KEY, FALLBACK_HASHCODE), fallback) else fallback
    }

    fun <V> resolveOrDefault(result: ActivityResult, key: String, fallback: V): V {
        return if (result.resultCode == Activity.RESULT_OK && fallback != null) getOrDefault(result.data!!.getIntExtra("${key}_$INDEX_KEY", FALLBACK_HASHCODE), fallback) else fallback
    }

    fun <V> resolveOrDefault(activity: Activity, fallback: V): V {
        return getOrDefault(activity.intent.getIntExtra(INDEX_KEY, FALLBACK_HASHCODE), fallback)
    }

    fun <V> resolveOrNull(intent: Intent, key: String): V? {
        return runCatching { get<V>(intent.getIntExtra("${key}_$INDEX_KEY", FALLBACK_HASHCODE)) }.getOrNull()
    }

    fun <V> resolveOrDefault(activity: Activity, fallback: Nothing?): V? {
        return getOrDefault(activity.intent.getIntExtra(INDEX_KEY, FALLBACK_HASHCODE), null)
    }


}