package modulon.union

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope

abstract class UnionDialogFragment : DialogFragment(), Union {

    final override val parentFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getParentFragmentManagerUnion")
        get() = getParentFragmentManager()

    final override val childFragmentManager: FragmentManager
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getChildFragmentManagerUnion")
        get() = getChildFragmentManager()

    final override val lifecycleOwner: LifecycleOwner get() = viewLifecycleOwner
    final override val lifecycleScope: LifecycleCoroutineScope get() = viewLifecycleOwner.lifecycleScope

    final override val activityResultCaller: ActivityResultCaller
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultCallerUnion")
        get() = this
    final override val activityResultRegistry: ActivityResultRegistry
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getActivityResultRegistryUnion")
        get() = (this as ActivityResultRegistryOwner).activityResultRegistry

    final override val activity: Activity get() = requireActivity()
    final override val union: Union get() = this
    final override val context: Context
        @Suppress("INAPPLICABLE_JVM_NAME")
        @JvmName("getContextUnion")
        get() = getContext()

    @SuppressLint("UseRequireInsteadOfGet")
    final override fun getContext(): Context = super.getContext()!!


}