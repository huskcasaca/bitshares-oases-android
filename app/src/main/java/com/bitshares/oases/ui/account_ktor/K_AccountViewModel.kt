package com.bitshares.oases.ui.account_ktor

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.resolveAccountPath
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import graphene.chain.K102_AccountObject
import graphene.protocol.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import modulon.extensions.stdlib.logcat

open class K_AccountViewModel(application: Application) : BaseViewModel(application) {

//    private val databaseApi by lazy {
//        GrapheneClient {
//            name = "XN_DELEGATE"
//            url = "wss://api.btsgo.net/ws"
//        }
//    }

    val accountUID = mutableLiveDataOf(0UL)

//    val account = accountUID.mapSuspend {
//        (databaseApi.getObject(it.toAccountIdType()) as K102_AccountObject?).orEmpty()
//    }

    override fun onActivityIntent(intent: Intent?) {
        super.onActivityIntent(intent)
        intent ?: return
        "onActivityIntent ${intent.action}".logcat()
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> {
                val uri = intent.data?.normalizeScheme()
                if (uri != null) {
                    viewModelScope.launch {
                        val accountPath = uri.pathSegments.firstOrNull()
                        val account = resolveAccountPath(accountPath)
                        withContext(Dispatchers.Main) { accountUID.value = account.uid.toULong() }
                    }
                }
            }
            null -> {
                val accountInstance = intent.getJson(IntentParameters.Account.KEY_UID, ChainConfig.EMPTY_INSTANCE)
                val chainId = intent.getJson(IntentParameters.Chain.KEY_CHAIN_ID, ChainConfig.EMPTY_STRING_ID)
                if (chainId != ChainConfig.EMPTY_STRING_ID) {
//                    chainIdManual.value = chainId
//                    accountUid.value = if (chainId == ChainPropertyRepository.chainId) accountInstance else ChainConfig.EMPTY_INSTANCE
//                    userUid.value = accountInstance
                    accountUID.value = accountInstance.toULong()
                } else {
//                    accountUid.value = accountInstance
//                    userUid.value = accountInstance
                    accountUID.value = accountInstance.toULong()
                }
            }
            else -> return
        }

    }

}