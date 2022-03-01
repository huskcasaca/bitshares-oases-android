package modulon.extensions.lifecycle

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel

inline fun <reified VM : ViewModel> Fragment.parentViewModels() = viewModels<VM>({ parentFragment ?: activity ?: this }, { (parentFragment ?: activity ?: this).defaultViewModelProviderFactory })