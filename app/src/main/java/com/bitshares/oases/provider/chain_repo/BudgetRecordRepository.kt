package com.bitshares.oases.provider.chain_repo

import bitshareskit.extensions.formatIsoTime
import bitshareskit.objects.BudgetRecordObject
import com.bitshares.oases.database.BlockchainDatabase
import modulon.extensions.livedata.NonNullMediatorLiveData

object BudgetRecordRepository {

    const val BASE_RECORD_UID = 41100L
    const val BASE_RECORD_TIME = "2020-06-21T12:00:00"

    private val lastBudgetRecordUid = NonNullMediatorLiveData(BASE_RECORD_UID)

    private val budgetRecordDao = BlockchainDatabase.INSTANCE.budgetRecordDao()

    val lastBudgetRecord = budgetRecordDao.getLastLive()

    suspend fun getLastBudgetRecord() = GrapheneRepository.getObjectFromChain<BudgetRecordObject>(calculateLastBudgetRecordUid())

    private fun calculateLastBudgetRecordUid(): Long {
// FIXME: 22/1/2022 java.lang.NullPointerException: Attempt to invoke virtual method 'long java.lang.Number.longValue()' on a null object reference
//        at com.bitshares.android.repos.BudgetRecordRepository.calculateLastBudgetRecordUid(BudgetRecordRepository.kt:22)
//        at com.bitshares.android.repos.BudgetRecordRepository.getLastBudgetRecord(BudgetRecordRepository.kt:19)
//        at com.bitshares.android.user_interface.account.voting.VotingViewModel$refresh$1$5.invokeSuspend(VotingViewModel.kt:34)
        val blockOffset = kotlin.runCatching { (ChainPropertyRepository.currentChainTime.value - formatIsoTime(BASE_RECORD_TIME).time) / (3600 * 1000) }.getOrDefault(0L)
        lastBudgetRecordUid.postValue(blockOffset + BASE_RECORD_UID)
        return blockOffset + BASE_RECORD_UID
    }


}

