package com.bitshares.oases.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bitshares.oases.extensions.compat.ViewModelIntent

open class BaseViewModel(application: Application) : AndroidViewModel(application), ViewModelIntent {


}