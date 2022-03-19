package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.createAccountObject
import bitshareskit.extensions.formatIdentifier
import bitshareskit.extensions.formatInstance
import bitshareskit.extensions.isGrapheneInstanceValid
import bitshareskit.models.FullAccount
import bitshareskit.objects.*
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.GrapheneSocketLiveData
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.launch
import modulon.extensions.coroutine.mapParallel
import modulon.extensions.livedata.emptyLiveData
import org.java_json.JSONArray
import org.java_json.JSONObject

object AccountRepository {

    private val accountDao = BlockchainDatabase.INSTANCE.accountDao()
    val accountBalanceDao = BlockchainDatabase.INSTANCE.accountBalanceDao()
    private val accountStatisticsDao = BlockchainDatabase.INSTANCE.accountStatisticsDao()
    private val operationHistoryDao = BlockchainDatabase.INSTANCE.operationHistoryDao()

    suspend fun getAccountBalanceObjectFromDao(uid: Long): AccountBalanceObject? = accountBalanceDao.get(uid)

    fun getAccountBalanceLiveFromDao(uid: Long): LiveData<AccountBalanceObject?> = accountBalanceDao.getLive(uid).distinctUntilChanged()
    fun getAccountBalanceListLiveFromDao(ownerUid: Long): LiveData<List<AccountBalanceObject>> = accountBalanceDao.getListByOwnerLive(ownerUid).distinctUntilChanged()
    fun getAccountBalanceByAssetFromDao(assetUid: Long): LiveData<AccountBalanceObject?> = accountBalanceDao.getByAssetLive(assetUid).distinctUntilChanged()

    fun getAccountStatisticsLiveFromDao(uid: Long): LiveData<AccountStatisticsObject?> = accountStatisticsDao.getLive(uid).distinctUntilChanged()
    fun getAccountLiveFromDao(uid: Long) = accountDao.getLive(uid).distinctUntilChanged()

    fun getAccountLive(uid: Long) = if (uid != ChainConfig.EMPTY_INSTANCE) GrapheneRepository.getObjectLive<AccountObject>(uid) else emptyLiveData(null)


    private val fullAccountMap = mutableMapOf<Long, LiveData<FullAccount?>>()


    @Synchronized
    fun getFullAccountLive(uid: Long): LiveData<FullAccount?> {
        if (!isGrapheneInstanceValid(uid)) return emptyLiveData(null)
        val livedata = GrapheneSocketLiveData(CallMethod.GET_FULL_ACCOUNTS, listOf(listOf(formatIdentifier(ObjectType.ACCOUNT_OBJECT, uid)), false)) {
            runCatching { FullAccount((it as JSONArray).optJSONArray(0).getJSONObject(1)) }.onSuccess {
                addFullAccountToDatabase(it)
            }.getOrNull()
        }
        if (fullAccountMap[uid] == null) fullAccountMap[uid] = livedata
        return fullAccountMap[uid] ?: livedata
    }


    suspend fun getFullAccountFromChain(uid: Long): FullAccount? {
        val data = listOf(listOf(formatIdentifier(ObjectType.ACCOUNT_OBJECT, uid)), false)
        return NetworkService.sendOrNull(CallMethod.GET_FULL_ACCOUNTS, data) {
            runCatching { FullAccount((it as JSONArray).optJSONArray(0).getJSONObject(1)) }.getOrNull()
        }
    }


    suspend fun getAccountOrThrow(nameOrId: String): AccountObject? {
        return NetworkService.sendOrThrow(CallMethod.GET_ACCOUNTS, listOf(listOf(nameOrId), false)) {
            runCatching { AccountObject((it as JSONArray).getJSONObject(0)) }.getOrThrow()
        }
    }

    suspend fun getAccountOrNull(nameOrId: String): AccountObject? {
        return NetworkService.sendOrNull(CallMethod.GET_ACCOUNTS, listOf(listOf(nameOrId), false)) {
            runCatching { AccountObject((it as JSONArray).getJSONObject(0)) }.getOrNull()
        }
    }


    suspend fun getKeyReferencesOrThrow(keys: List<String>): List<AccountObject> {
        return NetworkService.sendOrThrow(CallMethod.GET_KEY_REFERENCES, listOf(keys)) {
            runCatching { (it as JSONArray).map { (it as JSONArray).optString(0) }.filter { it.isNotEmpty() }.distinct().mapParallel { getAccountOrThrow(it) }.filterNotNull() }.getOrThrow()
        }
    }


    suspend fun addAccountObject(obj: AccountObject) = accountDao.add(obj)

    suspend fun getAccountDetail(account: AccountObject) = getAccountObject(account.uid) ?: account

    suspend fun getAccountObject(uid: Long) = GrapheneRepository.getObject<AccountObject>(uid)
    suspend fun getAccountObjectFromChain(uid: Long) = GrapheneRepository.getObjectFromChain<AccountObject>(uid)
    suspend fun getAccountObjectFromChainOrThrow(uid: Long) = GrapheneRepository.getObjectFromChainOrThrow<AccountObject>(uid)

    fun getAccountOperationHistory(uid: Long) = operationHistoryDao.getListByOwnerLive(uid)

    @Deprecated("Use lookupAccountNames(lowerBoundName, limit, subscribe) instead")
    suspend fun lookupAccounts(lowerBoundName: String, limit: Int = 1000, subscribe: Boolean = false): List<Pair<String, String>> {
        val data: List<Any> = listOf(lowerBoundName, if (limit in 1..1000) limit else 1000, subscribe)
        return NetworkService.sendOrThrow(CallMethod.LOOKUP_ACCOUNTS, data) {
            (it as JSONArray).map { it as JSONArray; Pair(it[0] as String, it[1] as String) }
        }
    }

    suspend fun lookupAccountNames(lowerBoundName: String, limit: Int = 1000, subscribe: Boolean = false): List<AccountObject> {
        val data: List<Any> = listOf(lowerBoundName, limit.coerceIn(1..1000), subscribe)
        return NetworkService.sendOrThrow(CallMethod.LOOKUP_ACCOUNTS, data) {
            (it as JSONArray).map { it as JSONArray; createAccountObject(it[1] as String, it[0] as String) }
        }
    }

    suspend fun getAccountHistory(uid: Long, limit: Int = 100, start: Long = 0L, stop: Long = 0L): List<OperationHistoryObject> {
        return getAccountHistory(formatIdentifier<AccountObject>(uid), limit, start, stop)
    }

    suspend fun getAccountHistory(nameOrId: String, limit: Int = 100, start: Long = 0L, stop: Long = 0L): List<OperationHistoryObject> {
        return NetworkService.sendOrNull(CallMethod.GET_ACCOUNT_HISTORY, listOf(nameOrId, formatIdentifier<OperationHistoryObject>(stop), limit, formatIdentifier<OperationHistoryObject>(start))) {
            runCatching {
                (it as JSONArray).map { GrapheneObject.fromJson<OperationHistoryObject>(it as JSONObject).apply { ownerUid = formatInstance(nameOrId) } }.also {
                    GrapheneRepository.addObjectToDatabase(it)
                }
            }.getOrNull().orEmpty()
        }
    }


    private fun addFullAccountToDatabase(fullAccount: FullAccount?) {
        if (fullAccount != null) blockchainDatabaseScope.launch {
            if (accountDao.get(fullAccount.account.uid) != fullAccount.account) accountDao.add(fullAccount.account)
            if (accountStatisticsDao.get(fullAccount.accountStatistics.uid) != fullAccount.accountStatistics) accountStatisticsDao.add(fullAccount.accountStatistics)

            val balanceIds = fullAccount.balance.map { it.uid }
            val balanceToRemove = accountBalanceDao.getListByOwner(fullAccount.account.uid).filter { account -> !balanceIds.contains(account.uid) }
            accountBalanceDao.remove(balanceToRemove)
            val balanceLeft = accountBalanceDao.getListByOwner(fullAccount.account.uid)
            val balanceChanged = fullAccount.balance.filter { balance -> !balanceLeft.contains(balance) }
            accountBalanceDao.add(balanceChanged)
        }
    }

}