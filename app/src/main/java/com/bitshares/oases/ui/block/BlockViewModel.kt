package com.bitshares.oases.ui.block

import android.app.Application
import android.content.Intent
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.resolveBlock
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.chain_repo.TransactionRepository
import com.bitshares.oases.provider.chain_repo.WitnessRepository
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import modulon.extensions.stdlib.logcat

class BlockViewModel(application: Application) : BaseViewModel(application) {

    private val fetchContext = viewModelScope.coroutineContext + Dispatchers.IO

    val blockNum = NonNullMutableLiveData(ChainConfig.EMPTY_INSTANCE)

    val block = blockNum.mapSuspend(fetchContext) { ChainPropertyRepository.getBlock(it) }.filterNotNull()
    val witness = block.filterNotNull().map(viewModelScope) {
        WitnessRepository.getWitness(it.witness.uid)?.apply { witnessAccount = AccountRepository.getAccountObject(witnessAccount.uid) ?: witnessAccount }
    }.filterNotNull()

    val tx = block.filterNotNull().map { it.transactions }

    val ops = tx.map { it.flatMap { it.operations } }.mapChildParallel(viewModelScope) {
        TransactionRepository.getOperationDetail(it)
    }

    fun setBlockNumber(num: Long) = blockNum.postValue(num)

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
                        val blockPath = uri.pathSegments.firstOrNull()
                        val block = resolveBlock(blockPath)
                        withContext(Dispatchers.Main) { setBlockNumber(block.blockNum) }
                    }
                }
            }
            null -> intent.getJson(IntentParameters.Block.KEY_HEIGHT, ChainConfig.EMPTY_INSTANCE).let { setBlockNumber(it) }
            else -> return
        }

    }


}