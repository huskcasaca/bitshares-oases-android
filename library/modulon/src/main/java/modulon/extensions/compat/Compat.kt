package modulon.extensions.compat

import android.app.Activity
import android.app.Application
import android.content.*
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import modulon.R
import modulon.union.Union
import modulon.union.UnionActivity
import modulon.union.UnionContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// startActivity
fun UnionContext.startActivity(intent: Intent) = context.startActivity(intent)
inline fun <reified T : Activity> UnionContext.startActivity(intent: Intent.() -> Unit = { }) = startActivity(Intent(context, T::class.java).apply(intent))
//inline fun <reified T : Activity> UnionContext.startActivityExtras(vararg params: Pair<String, Any?>) = startActivity<T> { params.forEach { putLocalExtra(it.first, it.second) } }

// startActivityForResultContract
inline fun <I, O> Union.startActivityForResultContract(contract: ActivityResultContract<I, O>, input: I, crossinline callback: (O) -> Unit) = activityResultCaller.registerForActivityResult(contract) { if (it != null) callback.invoke(it) }.launch(input)
inline fun Union.startActivityForResult(intent: Intent, crossinline callback: (ActivityResult) -> Unit) = startActivityForResultContract(ActivityResultContracts.StartActivityForResult(), intent, callback)
inline fun Union.startActivityForOpenDocument(input: Collection<String>, crossinline callback: (Uri?) -> Unit) = startActivityForResultContract(ActivityResultContracts.OpenDocument(), input.toTypedArray(), callback)
inline fun Union.startActivityForCreateDocument(input: String, crossinline callback: (Uri?) -> Unit) = startActivityForResultContract(ActivityResultContracts.CreateDocument(), input, callback)
@PublishedApi
internal inline fun <reified T : Activity> Union.startActivityForResult(crossinline intent: Intent.() -> Unit, crossinline callback: (ActivityResult) -> Unit) = startActivityForResult(Intent(context, T::class.java).apply(intent), callback)

// suspendActivityForResultContract
suspend inline fun <I, O> Union.suspendActivityForResultContract(contract: ActivityResultContract<I, O>, input: I): O = suspendCoroutine { cont -> startActivityForResultContract(contract, input) { cont.resume(it) } }
suspend inline fun Union.suspendActivityForResult(intent: Intent): ActivityResult = suspendActivityForResultContract(ActivityResultContracts.StartActivityForResult(), intent)
suspend inline fun Union.suspendActivityForOpenDocument(input: Array<String>): Uri? = suspendActivityForResultContract(ActivityResultContracts.OpenDocument(), input)
suspend inline fun Union.suspendActivityForCreateDocument(input: String): Uri? = suspendActivityForResultContract(ActivityResultContracts.CreateDocument(), input)
@PublishedApi
internal suspend inline fun <reified T : Activity> Union.suspendActivityForResult(crossinline intent: Intent.() -> Unit = { }): ActivityResult = suspendActivityForResultContract(ActivityResultContracts.StartActivityForResult(), Intent(context, T::class.java).apply(intent))

fun UnionContext.startUriBrowser(url: Uri) = startActivity(Intent(Intent.ACTION_VIEW, url))

// toast
fun UnionContext.toast(text: CharSequence) = Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
fun UnionContext.toastLong(text: CharSequence) = Toast.makeText(context, text, Toast.LENGTH_LONG).show()

// clipboard
fun UnionContext.setClipboard(label: CharSequence, data: CharSequence) = getSystemService<ClipboardManager>()?.setPrimaryClip(ClipData.newPlainText(label, data))
fun UnionContext.setClipboardToast(label: CharSequence, data: CharSequence) {
    setClipboard(label, data)
    toast(context.getString(R.string.clipboard_toast))
}

// secureWindow
fun Union.secureWindow() {
    // TODO: 2022/2/18 re-enable
//    activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
}

// TODO: 2022/2/18 move
inline fun <reified T> UnionContext.getSystemService() = context.getSystemService(T::class.java)


fun Fragment.finishActivity(resultCode: Int = Activity.RESULT_OK, intent: Intent.() -> Unit) {
    activity?.setResult(resultCode, Intent().apply(intent))
    activity?.finish()
}

fun Fragment.finishActivity() {
    activity?.finish()
}

fun Fragment.recreateActivity() {
    activity?.recreate()
}

// TODO: 2022/2/10 rename
fun Fragment.arguments(block: Bundle.() -> Unit): Fragment = apply {
    arguments = Bundle().apply(block)
}

//fun Activity.hideKeyboard() = hideKeyboard(currentFocus ?: View(this))
//fun Fragment.hideKeyboard() = view?.let { activity?.hideKeyboard(it) } ?: false
//fun Context.hideKeyboard(view: View) = hideKeyboardCompat(this, view)
//fun hideKeyboardCompat(context: Context, view: View) = context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(view.windowToken, 0) ?: false

//@Deprecated("Deprecated", ReplaceWith("View.showSoftKeyboard()"))
//fun Activity.showKeyboard1() = window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
//fun Fragment.showKeyboard1() = activity?.showKeyboard1()

//@Deprecated("Deprecated", ReplaceWith("View.showSoftKeyboard()"))
//fun Activity.showKeyboard2() = showKeyboard2(currentFocus ?: View(this))
//fun Fragment.showKeyboard2() = view?.let { activity?.showKeyboard2(it) } ?: false
//fun Context.showKeyboard2(view: View) = showKeyboardCompat2(this, view)
//fun View.showKeyboard2() = showKeyboardCompat2(context, this)
//fun showKeyboardCompat2(context: Context, view: View) = context.getSystemService(InputMethodManager::class.java)?.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_IMPLICIT, 0) ?: false

fun Activity.showSoftKeyboard() = showSoftKeyboardCompat(this, currentFocus ?: View(this))
fun Fragment.showSoftKeyboard() = showSoftKeyboardCompat(requireContext(), view ?: View(requireContext()))
fun View.showSoftKeyboard() = showSoftKeyboardCompat(context, this)
fun showSoftKeyboardCompat(context: Context, view: View) = view.post { context.getSystemService(InputMethodManager::class.java)?.toggleSoftInputFromWindow(view.windowToken, InputMethodManager.SHOW_IMPLICIT, 0) }

fun Activity.hideSoftKeyboard() = hideSoftKeyboardCompat(this, currentFocus ?: View(this))
fun Fragment.hideSoftKeyboard() = hideSoftKeyboardCompat(requireContext(), view ?: View(requireContext()))
fun View.hideSoftKeyboard() = hideSoftKeyboardCompat(context, this)
fun hideSoftKeyboardCompat(context: Context, view: View) = view.post { context.getSystemService(InputMethodManager::class.java)?.hideSoftInputFromWindow(view.windowToken, 0) }

val View.activity: UnionActivity
    get() {
        var context: Context = context
        while (context is ContextWrapper) {
            if (context is UnionActivity) return context
            context = context.baseContext
        }
        throw NullPointerException("Activity not found")
    }

val Context.activity: UnionActivity
    get() {
        var context: Context = this
        while (context is ContextWrapper) {
            if (context is UnionActivity) return context
            context = context.baseContext
        }
        throw NullPointerException("Activity not found")
    }

val Context.application: Application
    get() {
        return if (this is Application) {
            this
        } else {
            activity.application
        }
    }

// TODO: 2022/2/19 remove
val Context.nightMode get() = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
val Context.isNightModeOn get() = nightMode == Configuration.UI_MODE_NIGHT_YES

var View.isForceDarkAllowedCompat : Boolean
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isForceDarkAllowed
        } else {
            false
        }
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isForceDarkAllowed = value
        }
    }
