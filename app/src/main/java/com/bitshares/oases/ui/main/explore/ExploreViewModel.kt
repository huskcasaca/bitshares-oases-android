package com.bitshares.oases.ui.main.explore

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import bitshareskit.chain.ChainConfig
import bitshareskit.entities.Block
import com.bitshares.oases.chain.ChainType
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.chain_repo.CommitteeMemberRepository
import com.bitshares.oases.provider.chain_repo.WitnessRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.*

class ExploreViewModel(application: Application) : BaseViewModel(application) {

    val notifyTab = NonNullMutableLiveData(ExploreFragment.Tabs.BLOCKCHAIN)

    val chainId = Graphene.KEY_CHAIN_ID

    val chainType = chainId.map {
        when (it) {
            ChainConfig.Chain.CHAIN_ID_MAIN_NET -> ChainType.MAIN
            ChainConfig.Chain.CHAIN_ID_TEST_NET -> ChainType.TEST
            else -> ChainType.UNKNOWN
        }
    }

    val chainTime = ChainPropertyRepository.currentChainTime


    val localTime = ChainPropertyRepository.currentSystemTime
    val blockTimeOffset = ChainPropertyRepository.lastBlockOffset

    val showLocaltime = NonNullMutableLiveData(Settings.KEY_ENABLE_BLOCK_UPDATES.value)
    val isUpdatesEnabled = Settings.KEY_ENABLE_BLOCK_UPDATES

    private val dynamicGlobalProperty = ChainPropertyRepository.dynamicGlobalProperty
    private val globalProperty = ChainPropertyRepository.globalProperty
    private val dynamicGlobalPropertyNonNull = dynamicGlobalProperty.filterNotNull()
    private val globalPropertyNonNull = globalProperty.filterNotNull()

    val nextMaintenanceTime = dynamicGlobalPropertyNonNull.map { it.nextMaintenanceTime }

    val blockId = dynamicGlobalPropertyNonNull.map { it.headBlockId }
    val blockNum = dynamicGlobalPropertyNonNull.map { it.headBlockNumber }
    val irreversibleBlockNum = dynamicGlobalPropertyNonNull.map { it.lastIrreversibleBlockNum }


    val feeParameters = globalPropertyNonNull.map { it.currentFees }

    val showLifetimeFeeParameters = NonNullMutableLiveData(false)


    val witnessNum = globalPropertyNonNull.map { it.activeWitnesses.size }
    val committeeMemberNum = globalPropertyNonNull.map { it.activeCommitteeMembers.size }

    val witnessAccounts = globalPropertyNonNull.map { it.activeWitnesses }.distinctUntilChanged().mapChildParallel(viewModelScope) { AccountRepository.getAccountDetail(WitnessRepository.getWitnessDetail(it).witnessAccount) }
    val committeesAccounts = globalPropertyNonNull.map { it.activeCommitteeMembers }.distinctUntilChanged().mapChildParallel(viewModelScope) { AccountRepository.getAccountDetail(CommitteeMemberRepository.getCommitteeDetail(it).committeeMemberAccount) }

    private val lastBlock = dynamicGlobalPropertyNonNull.map(viewModelScope) {
        withContext(Dispatchers.IO) {
            val block = async { ChainPropertyRepository.getBlock(it.headBlockNumber) }
            val witnessAcc = async {
                WitnessRepository.getWitness(it.currentWitness.uid)?.apply {
                    witnessAccount = AccountRepository.getAccountObject(witnessAccount.uid) ?: witnessAccount
                }
            }
            val result = awaitAll(block, witnessAcc)
            (result[0] as? Block)?.apply {
                blockNum = it.headBlockNumber
                hash = it.headBlockId
                witness = witnessAcc.await() ?: witness
            }
        }
    }.filterNotNull()

//    private val blocksInternal = mutableListOf<Block>()
    private val blockComparator = Comparator { o1: Block, o2: Block -> o1.blockNum.compareTo(o2.blockNum) }
    private val blocksInternal = sortedSetOf(blockComparator)

    // 2022

    val recentBlocks = lastBlock.map {
        blocksInternal.add(it)
//        if (blocksInternal.size >= 128) blocksInternal.remove
        blocksInternal.reversed()
    }
    val opListLive = recentBlocks.map {
        it.flatMap { it.transactions.flatMap { it.operations } }
    }
    val blockTime = recentBlocks.map {
        if (it.size > 1 && it.component1().blockNum - it.component2().blockNum == 1L) it.component1().timestamp.time - it.component2().timestamp.time else 0
    }

    // filtered
    val filter = NonNullMutableLiveData(EMPTY_SPACE)
    val filterEnabled = filter.map { it.isNotEmpty() }

    // TODO: 27/1/2022 add filters and extract as function
    val recentBlocksFiltered = combineNonNull(recentBlocks, filter, filterEnabled) { blocks, filter, enabled ->
        if (enabled) blocks.filter { it.containsInternal(filter) } else blocks
    }

    val txPerBlock = recentBlocks.map {
        // TODO: 26/1/2022 10 or ?
        val list = it.take(10)
        if (list.isNotEmpty()) list.sumOf { it.operationCount } / list.size.toFloat() else 0f
    }

    val isBlockHistory = NonNullMutableLiveData(true)




}