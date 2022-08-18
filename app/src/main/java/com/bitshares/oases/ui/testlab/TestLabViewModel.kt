package com.bitshares.oases.ui.testlab

import android.app.Application
import androidx.lifecycle.viewModelScope
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.provider.chain_repo.GrapheneRepository
import graphene.protocol.ObjectType
import graphene.protocol.ProtocolType
import com.bitshares.oases.ui.base.BaseViewModel
import graphene.chain.K102_AccountObject
import graphene.chain.K103_AssetObject
import graphene.protocol.*
import graphene.serializers.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.NonNullMutableLiveData

class TestLabViewModel(application: Application) : BaseViewModel(application) {


    val objectType: NonNullMutableLiveData<out ObjectType> = NonNullMutableLiveData(ProtocolType.ACCOUNT)


    val consoleFlowHeader = NonNullMutableLiveData(listOf(EMPTY_SPACE to EMPTY_SPACE))

    fun console(header: Any = EMPTY_SPACE, text: Any = EMPTY_SPACE) {
        viewModelScope.launch(Dispatchers.Main) {
            consoleFlowHeader.value = consoleFlowHeader.value + (header.toString() to text.toString())
        }
    }



    private val jobs: MutableMap<ObjectType, Job> = mutableMapOf()

    val info: MutableMap<ObjectType, NonNullMutableLiveData<String>> = mutableMapOf(
        ProtocolType.ACCOUNT to NonNullMutableLiveData(""),
        ProtocolType.ASSET to NonNullMutableLiveData("")
    )

    fun testK102() {
        jobs[ProtocolType.ACCOUNT]?.cancel()
        jobs[ProtocolType.ACCOUNT] = viewModelScope.launch(Dispatchers.IO) {
            var instance = 0UL
            while (true) {
                runCatching {
                    val o = GrapheneRepository.getObjectFromChain<AccountObject>(instance.toLong())
                    if (o != null) {
                        console(o.rawJson.toString(4))
                        GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromString<K102_AccountObject>(o.rawJson.toString())
                    } else {
                        null
                    }
                }.onSuccess {
                    launch(Dispatchers.Main) {
                        info[ProtocolType.ACCOUNT]?.value = if (it != null) "Success ${it.id.standardId}" else "Skipping $instance"
                    }
                }.onFailure {
                    launch(Dispatchers.Main) {
                        info[ProtocolType.ACCOUNT]?.value = "Failed ${AccountIdType(instance).standardId}\n" +
                                it.stackTraceToString()
                        it.printStackTrace()
                    }
                    cancel()
                }
                instance++
            }
        }
    }
    fun stopK102() {
        jobs[ProtocolType.ACCOUNT]?.cancel()
    }

    fun testK103(instance: ULong = 0U) {
        jobs[ProtocolType.ASSET]?.cancel()
        jobs[ProtocolType.ASSET] = viewModelScope.launch(Dispatchers.IO) {
            var instance = instance
            while (true) {
                runCatching {
                    val o = GrapheneRepository.getObjectFromChain<AssetObject>(instance.toLong())
                    if (o != null) {
                        console(o.rawJson.toString(4))
                        GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromString<K103_AssetObject>(o.rawJson.toString())
                    } else {
                        null
                    }
                }.onSuccess {
                    launch(Dispatchers.Main) {
                        info[ProtocolType.ASSET]?.value = if (it != null) "Success ${it.id.standardId}" else "Skipping $instance"
                    }
                }.onFailure {
                    launch(Dispatchers.Main) {
                        info[ProtocolType.ASSET]?.value = "Failed ${AssetIdType(instance).standardId}\n" +
                                it.stackTraceToString()
                        it.printStackTrace()
                    }
                    cancel()
                }
                instance++
            }
        }
    }
    fun stopK103() {
        jobs[ProtocolType.ASSET]?.cancel()
    }


}
