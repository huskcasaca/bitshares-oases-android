package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import bitshareskit.objects.WitnessObject
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import bitshareskit.chain.CallMethod

object WitnessRepository {

    private val witnessDao = BlockchainDatabase.INSTANCE.witnessDao()

    val list: LiveData<List<WitnessObject>> = witnessDao.getListLive()

    suspend fun getWitness(uid: Long) = GrapheneRepository.getObject<WitnessObject>(uid)

    suspend fun getWitnessCount(): Int = runCatching {
        val result = NetworkService.sendSuspend(CallMethod.GET_WITNESS_COUNT)
        result as Int
    }.getOrDefault(0)

    suspend fun getAllWitnesses(): List<WitnessObject>? = GrapheneRepository.getObjectFromChain((0L..getWitnessCount()).toList())

    fun getByOwnerLive(uid: Long): LiveData<WitnessObject?> = witnessDao.getByOwnerLive(uid)

    suspend fun getWitnessDetail(witness: WitnessObject): WitnessObject = getWitness(witness.uid) ?: witness

//    suspend fun getWitnessAccountDetail(witness: WitnessObject): WitnessObject = (getWitness(witness.uid) ?: witness).witnessAccount.l


}

