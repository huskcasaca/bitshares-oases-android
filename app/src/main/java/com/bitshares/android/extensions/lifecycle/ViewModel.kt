package com.bitshares.android.extensions.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import modulon.extensions.reflect.invokeFieldAs

val ViewModelStore.viewModels: HashMap<String, ViewModel>
    get() = invokeFieldAs("mMap")
