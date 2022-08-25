package modulon.union

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.savedstate.SavedStateRegistryOwner
import modulon.R
import modulon.extensions.graphics.tint

interface UnionContext {

    val context: Context
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getContextUnion")
        get() = throw RuntimeException("Not implemented!")

    // TODO: 2022/5/2 rename to toX()
    fun Int.contextDrawable(): Drawable = context.getDrawable( this)!!.apply { tint(R.color.component_dark_gray.contextColor()) }
    fun Int.contextDrawableNoTint(): Drawable = context.getDrawable( this)!!
    fun Int.contextColor(): Int = ContextCompat.getColor(context, this)

    fun Int.contextString(): String = context.getString(this)
    fun Int.contextString(vararg formatArgs: Any): String = context.getString(this, formatArgs)

    fun Int.contextColorStateList() = ContextCompat.getColorStateList(context, this)

    fun Int.contextDimenPixelSize(): Int = context.resources.getDimensionPixelSize( this)
    fun Int.contextDimenPixelOffset(): Int = context.resources.getDimensionPixelOffset( this)
    fun Int.contextDimen(): Float = context.resources.getDimension(this)
    fun Int.contextText(): CharSequence = context.resources.getText(this)
    fun Int.contextTextArray(): Array<CharSequence> = context.resources.getTextArray( this)

    // TODO: 2022/2/19 extract to extensions

}

interface UnionLifecycle: UnionContext {
    val lifecycleOwner: LifecycleOwner

    // TODO: 2022/2/11 rename to lifecycleScope
    val lifecycleScope: LifecycleCoroutineScope
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getLifecycleScopeUnion")
        get() = throw RuntimeException("Not implemented!")

    fun <T> LiveData<T>.observe(observer: Observer<T>) {
        observe(lifecycleOwner, observer)
    }
}

interface UnionFragmentManager: UnionContext {
    val parentFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getParentFragmentManagerUnion")
        get() = throw RuntimeException("Not implemented!")

    val childFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getChildFragmentManagerUnion")
        get() = throw RuntimeException("Not implemented!")
}

interface UnionResult {
    val activityResultCaller: ActivityResultCaller
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultCallerUnion")
        get() = throw RuntimeException("Not implemented!")

    val activityResultRegistry: ActivityResultRegistry
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultRegistryUnion")
        get() = throw RuntimeException("Not implemented!")
}

// TODO: 2022/2/11
interface UnionSavedStates {
    val savedStateRegistry: SavedStateRegistryOwner
}



interface Union: UnionContext, UnionLifecycle, UnionFragmentManager, UnionResult {
    val activity: Activity
    val union: Union
}
