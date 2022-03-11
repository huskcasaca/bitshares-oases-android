package com.bitshares.oases.extensions.lifecycle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import modulon.extensions.reflect.invokeFieldAs

val ViewModelStore.viewModels: HashMap<String, ViewModel>
    get() = invokeFieldAs("mMap")
