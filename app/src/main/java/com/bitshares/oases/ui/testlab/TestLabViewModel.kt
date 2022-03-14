package com.bitshares.oases.ui.testlab

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.ui.base.BaseViewModel
import graphene.protocol.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.NonNullMutableLiveData

class TestLabViewModel(application: Application) : BaseViewModel(application) {

    var firstField = EMPTY_SPACE
    var secondField = EMPTY_SPACE

    val order1 = NonNullMutableLiveData(EMPTY_SPACE)
    val order2 = NonNullMutableLiveData(EMPTY_SPACE)


    val objectType: NonNullMutableLiveData<out ObjectType> = NonNullMutableLiveData(ProtocolType.ACCOUNT)


    val consoleFlowHeader = NonNullMutableLiveData(listOf(EMPTY_SPACE to EMPTY_SPACE))

    fun console(header: Any = EMPTY_SPACE, text: Any = EMPTY_SPACE) {
        viewModelScope.launch(Dispatchers.Main) {
            consoleFlowHeader.value = consoleFlowHeader.value + (header.toString() to text.toString())
        }
    }

}
