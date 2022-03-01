package com.bitshares.android.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bitshares.android.extensions.compat.ViewModelIntent

open class BaseViewModel(application: Application) : AndroidViewModel(application), ViewModelIntent {


}