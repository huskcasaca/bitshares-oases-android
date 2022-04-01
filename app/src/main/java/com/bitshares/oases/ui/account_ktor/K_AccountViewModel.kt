package com.bitshares.oases.ui.account_ktor

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.FullAccount
import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import bitshareskit.objects.*
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.resolveAccountPath
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.*
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import graphene.chain.K102_AccountObject
import graphene.protocol.*
import graphene.rpc.DatabaseClientAPI
import graphene.rpc.MultiClient
import graphene.rpc.Node
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import java.math.BigDecimal

open class K_AccountViewModel(application: Application) : BaseViewModel(application) {

    private val databaseApi by lazy {
        MultiClient().run {
            switch(Node("XN_DELEGATE", "wss://api.btsgo.net/ws"))
            DatabaseClientAPI(this)
        }
    }

    val accountUID = mutableLiveDataOf(0UL)

    val account = accountUID.mapSuspend {
        (databaseApi.getObject(it.toAccount()) as K102_AccountObject?).orEmpty()
    }

    override fun onActivityIntent(intent: Intent?) {
        super.onActivityIntent(intent)
        intent ?: return
        logcat("onActivityIntent", intent.action)
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