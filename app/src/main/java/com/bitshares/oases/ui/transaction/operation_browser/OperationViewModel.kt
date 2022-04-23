package com.bitshares.oases.ui.transaction.operation_browser

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import bitshareskit.operations.Operation
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.stdlib.logcat

class OperationViewModel(application: Application) : BaseViewModel(application) {

    val operation = MutableLiveData(Operation.EMPTY)

    val operationType = operation.filterNotNull().map { it.operationType }

    override fun onActivityIntent(intent: Intent?) {
        super.onActivityIntent(intent)
        intent ?: return
        "onActivityIntent ${intent.action}".logcat()
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> return
            null -> intent.getJson(IntentParameters.Operation.KEY_OPERATION, Operation.EMPTY).let { operation.value = it }
            else -> return
        }
    }

}