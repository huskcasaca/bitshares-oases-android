package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import bitshareskit.objects.WorkerObject
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import bitshareskit.chain.CallMethod
import org.java_json.JSONArray
import org.java_json.JSONObject

object WorkerRepository {

    private val workerDao = BlockchainDatabase.INSTANCE.workerDao()

    val list: LiveData<List<WorkerObject>> = workerDao.getListLive()

    suspend fun getWorker(uid: Long) = GrapheneRepository.getObject<WorkerObject>(uid)

    suspend fun getWorkerCount(): Int = runCatching {
        val result = NetworkService.sendSuspend(CallMethod.GET_WORKER_COUNT)
        result as Int
    }.getOrDefault(0)

    suspend fun getAllWorkers(): List<WorkerObject> = runCatching {
        val result = NetworkService.sendSuspend(CallMethod.GET_ALL_WORKERS)
        (result as JSONArray).map { WorkerObject(it as JSONObject) }
    }.onSuccess { workerDao.add(it) }.getOrDefault(emptyList())


}

