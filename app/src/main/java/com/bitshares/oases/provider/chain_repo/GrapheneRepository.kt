package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import bitshareskit.extensions.*
import bitshareskit.objects.GrapheneObject
import bitshareskit.objects.ObjectType.*
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.database.blockchain_daos.GrapheneObjectDao
import com.bitshares.oases.netowrk.java_websocket.GrapheneSocketLiveData
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.Source
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.launch
import modulon.extensions.livedata.emptyLiveData
import org.java_json.JSONArray
import org.java_json.JSONObject

object GrapheneRepository {

    val accountBalanceDao = BlockchainDatabase.INSTANCE.accountBalanceDao()
    val accountDao = BlockchainDatabase.INSTANCE.accountDao()
    val accountStatisticsDao = BlockchainDatabase.INSTANCE.accountStatisticsDao()
    val accountTransactionHistoryDao = BlockchainDatabase.INSTANCE.accountTransactionHistoryDao()
    val assetBitassetDataDao = BlockchainDatabase.INSTANCE.assetBitassetDataDao()
    val assetDao = BlockchainDatabase.INSTANCE.assetDao()
    val assetDynamicDataDao = BlockchainDatabase.INSTANCE.assetDynamicDataDao()
    val balanceDao = BlockchainDatabase.INSTANCE.balanceDao()
    val baseDao = BlockchainDatabase.INSTANCE.baseDao()
    val blindedBalanceDao = BlockchainDatabase.INSTANCE.blindedBalanceDao()
    val blockSummaryDao = BlockchainDatabase.INSTANCE.blockSummaryDao()
    val bucketDao = BlockchainDatabase.INSTANCE.bucketDao()
    val budgetRecordDao = BlockchainDatabase.INSTANCE.budgetRecordDao()
    val buybackDao = BlockchainDatabase.INSTANCE.buybackDao()
    val callOrderDao = BlockchainDatabase.INSTANCE.callOrderDao()
    val chainPropertyDao = BlockchainDatabase.INSTANCE.chainPropertyDao()
    val collateralBidDao = BlockchainDatabase.INSTANCE.collateralBidDao()
    val committeeMemberDao = BlockchainDatabase.INSTANCE.committeeMemberDao()
    val customAuthorityDao = BlockchainDatabase.INSTANCE.customAuthorityDao()
    val customDao = BlockchainDatabase.INSTANCE.customDao()
    val dynamicGlobalPropertyDao = BlockchainDatabase.INSTANCE.dynamicGlobalPropertyDao()
    val fbaAccumulatorDao = BlockchainDatabase.INSTANCE.fbaAccumulatorDao()
    val forceSettlementDao = BlockchainDatabase.INSTANCE.forceSettlementDao()
    val globalPropertyDao = BlockchainDatabase.INSTANCE.globalPropertyDao()
    val htlcDao = BlockchainDatabase.INSTANCE.htlcDao()
    val limitOrderDao = BlockchainDatabase.INSTANCE.limitOrderDao()
    val liquidityPoolDao = BlockchainDatabase.INSTANCE.liquidityPoolDao()
    val nullDao = BlockchainDatabase.INSTANCE.nullDao()
    val operationHistoryDao = BlockchainDatabase.INSTANCE.operationHistoryDao()
    val orderHistoryDao = BlockchainDatabase.INSTANCE.orderHistoryDao()
    val proposalDao = BlockchainDatabase.INSTANCE.proposalDao()
    val specialAuthorityDao = BlockchainDatabase.INSTANCE.specialAuthorityDao()
    val ticketDao = BlockchainDatabase.INSTANCE.ticketDao()
    val transactionDao = BlockchainDatabase.INSTANCE.transactionDao()
    val vestingBalanceDao = BlockchainDatabase.INSTANCE.vestingBalanceDao()
    val withdrawPermissionDao = BlockchainDatabase.INSTANCE.withdrawPermissionDao()
    val witnessDao = BlockchainDatabase.INSTANCE.witnessDao()
    val witnessScheduleDao = BlockchainDatabase.INSTANCE.witnessScheduleDao()
    val workerDao = BlockchainDatabase.INSTANCE.workerDao()
//    val networkScope = NetworkService.networkScope

    val skip = Graphene.KEY_BLOCK_INTERVAL.value * 1000L / 2

    // get database dao
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : GrapheneObject> getGrapheneObjectDao(): GrapheneObjectDao<T> {
        return when (getGrapheneObjectType<T>()) {
            NULL_OBJECT -> nullDao
            BASE_OBJECT -> baseDao
            ACCOUNT_OBJECT -> accountDao
            ASSET_OBJECT -> assetDao
            FORCE_SETTLEMENT_OBJECT -> forceSettlementDao
            COMMITTEE_MEMBER_OBJECT -> committeeMemberDao
            WITNESS_OBJECT -> witnessDao
            LIMIT_ORDER_OBJECT -> limitOrderDao
            CALL_ORDER_OBJECT -> callOrderDao
            CUSTOM_OBJECT -> customDao
            PROPOSAL_OBJECT -> proposalDao
            OPERATION_HISTORY_OBJECT -> operationHistoryDao
            WITHDRAW_PERMISSION_OBJECT -> withdrawPermissionDao
            VESTING_BALANCE_OBJECT -> vestingBalanceDao
            WORKER_OBJECT -> workerDao
            BALANCE_OBJECT -> balanceDao
            HTLC_OBJECT -> htlcDao
            CUSTOM_AUTHORITY_OBJECT -> customAuthorityDao
            TICKET_OBJECT -> ticketDao
            LIQUIDITY_POOL_OBJECT -> liquidityPoolDao

            GLOBAL_PROPERTY_OBJECT -> globalPropertyDao
            DYNAMIC_GLOBAL_PROPERTY_OBJECT -> dynamicGlobalPropertyDao
            ASSET_DYNAMIC_DATA -> assetDynamicDataDao
            ASSET_BITASSET_DATA -> assetBitassetDataDao
            ACCOUNT_BALANCE_OBJECT -> accountBalanceDao
            ACCOUNT_STATISTICS_OBJECT -> accountStatisticsDao
            TRANSACTION_OBJECT -> transactionDao
            BLOCK_SUMMARY_OBJECT -> blockSummaryDao
            ACCOUNT_TRANSACTION_HISTORY_OBJECT -> accountTransactionHistoryDao
            BLINDED_BALANCE_OBJECT -> blindedBalanceDao
            CHAIN_PROPERTY_OBJECT -> chainPropertyDao
            WITNESS_SCHEDULE_OBJECT -> witnessScheduleDao
            BUDGET_RECORD_OBJECT -> budgetRecordDao
            SPECIAL_AUTHORITY_OBJECT -> specialAuthorityDao
            BUYBACK_OBJECT -> buybackDao
            FBA_ACCUMULATOR_OBJECT -> fbaAccumulatorDao
            COLLATERAL_BID_OBJECT -> collateralBidDao

            ORDER_HISTORY_OBJECT -> orderHistoryDao
            BUCKET_OBJECT -> bucketDao

        } as GrapheneObjectDao<T>
    }

//    // get object from blockchain only live
//    inline fun <reified T: GrapheneObject> getObjectLiveFromChain(objectId: String): LiveData<T?> = WebsocketLiveData { getObjectSocketCall<T>(objectId) }
//    inline fun <reified T: GrapheneObject> getObjectLiveFromChain(uid: Long): LiveData<T?> = WebsocketLiveData { getObjectSocketCall<T>(formatGrapheneObjectId<T>(uid)) }

    // get object from blockchain only live
    inline fun <reified T : GrapheneObject> getObjectLiveFromChain(id: String): LiveData<T?> {
        return if (isGrapheneInstanceValid(id)) GrapheneSocketLiveData(CallMethod.GET_OBJECTS, listOf(listOf(id), false)) {
            runCatching { GrapheneObject.fromJson<T>((it as JSONArray)[0] as JSONObject) }.onSuccess { blockchainDatabaseScope.launch { addObjectToDatabase(it) } }.getOrNull()
        } else emptyLiveData(null)
    }

    inline fun <reified T : GrapheneObject> getObjectLiveFromChain(uid: Long): LiveData<T?> = getObjectLiveFromChain(formatIdentifier<T>(uid))

    // get object from database only live
    inline fun <reified T : GrapheneObject> getObjectLiveFromDatabase(objectId: String): LiveData<T?> = getObjectLiveFromDatabase(formatInstance(objectId))
    inline fun <reified T : GrapheneObject> getObjectLiveFromDatabase(uid: Long): LiveData<T?> = getGrapheneObjectDao<T>().getLive(uid)


    // get object from database and blockchain live
    inline fun <reified T : GrapheneObject> getObjectLive(uid: Long): LiveData<T?> = MediatorLiveData<T>().apply {
        addSource(getObjectLiveFromChain<T>(uid)) { }
        addSource(getObjectLiveFromDatabase<T>(uid)) { value = it }
    }


    // add object to database
    suspend inline fun <reified T : GrapheneObject> addObjectToDatabase(obj: T) {
        val dao = getGrapheneObjectDao<T>()
        if (dao.get(obj.uid) != obj) dao.add(obj)
    }

    suspend inline fun <reified T : GrapheneObject> addObjectToDatabase(objList: List<T>) {
        val dao = getGrapheneObjectDao<T>()
        dao.add(objList)
    }

    // get object from database only
    suspend inline fun <reified T : GrapheneObject> getObjectFromDatabase(uid: Long): T? {
        return getGrapheneObjectDao<T>().get(uid)
    }

//    suspend inline fun <reified T: GrapheneObject> getObjectFromChain(uid: Long): T? = getObjectSocketCallThrottle<T>(uid).await()
//    suspend inline fun <reified T: GrapheneObject> getObjectFromChainOrThrow(uid: Long): T? = getObjectSocketCallThrottle<T>(uid).awaitOrThrow()
//    suspend inline fun <reified T: GrapheneObject> getObjectFromChain(uidList: List<Long>): List<T>? = getObjectSocketCall<T>(uidList).await()

    suspend inline fun <reified T : GrapheneObject> getObjectFromChain(uid: Long): T? {
        val id = formatIdentifier<T>(uid)
        return if (isGrapheneInstanceValid(id)) NetworkService.sendOrNull(CallMethod.GET_OBJECTS, listOf(listOf(formatIdentifier<T>(uid)), false)) {
            runCatching { GrapheneObject.fromJson<T>((it as JSONArray)[0] as JSONObject) }.onSuccess { blockchainDatabaseScope.launch { addObjectToDatabase(it) } }.onFailure { it.printStackTrace() }.getOrNull()
        } else null
    }

    suspend inline fun <reified T : GrapheneObject> getObjectFromChainOrThrow(uid: Long): T? {
        val id = formatIdentifier<T>(uid)
        return if (isGrapheneInstanceValid(id)) NetworkService.sendOrNull(CallMethod.GET_OBJECTS, listOf(listOf(formatIdentifier<T>(uid)), false)) {
            runCatching { GrapheneObject.fromJson<T>((it as JSONArray)[0] as JSONObject) }.onSuccess { blockchainDatabaseScope.launch { addObjectToDatabase(it) } }.getOrThrow()
        } else null
    }

    suspend inline fun <reified T : GrapheneObject> getObjectFromChain(uidList: List<Long>): List<T> = NetworkService.sendOrNull(
        CallMethod.GET_OBJECTS, listOf(formatIdentifier<T>(uidList), false)) {
        runCatching { (it as JSONArray).mapNotNull { runCatching { GrapheneObject.fromJson<T>(it as JSONObject) }.getOrNull() } }.onSuccess { blockchainDatabaseScope.launch { addObjectToDatabase(it) } }.onFailure { it.printStackTrace() }.getOrNull()
    }.orEmpty()

    // get object from first database then blockchain
    suspend inline fun <reified T : GrapheneObject> getObject(uid: Long): T? = getObjectFromDatabase(uid) ?: getObjectFromChain(uid)
    suspend inline fun <reified T : GrapheneObject> getObjectOrEmpty(uid: Long): T = getObjectFromDatabase(uid) ?: getObjectFromChain(uid) ?: createGrapheneEmptyInstance()
    suspend inline fun <reified T : GrapheneObject> getObject(instance: T): T = getObjectFromDatabase(instance.uid) ?: getObjectFromChain(instance.uid) ?: instance

    suspend inline fun <reified T : GrapheneObject> getGrapheneObject(uid: Long, source: Source): T? = when (source) {
        Source.LOCAL_FIRST -> getObjectFromDatabase(uid) ?: getObjectFromChain(uid)
        Source.CHAIN_FIRST -> getObjectFromChain(uid) ?: getObjectFromDatabase(uid)
        Source.LOCAL_ONLY -> getObjectFromDatabase(uid)
        Source.CHAIN_ONLY -> getObjectFromChain(uid)
        Source.NONE -> null
    }


//    suspend inline fun <reified K : AbstractObject> getKObjectFromChain(uid: Long): K {
//        return NetworkService.sendOrNull(CallMethod.GET_OBJECTS, listOf(listOf(("1.2.$uid")), false)) {
//            logcat("getKObjectFromChain $")
////            config1.decodeFromString(ListSerializer(serializer()), (it as JSONArray).toString())
//            config1.decodeFromString(ListSerializer(serializer<K>()), (it as JSONArray).toString()).firstOrNull() ?: emptyKGrapheneObject()
//        }
//    }

//    suspend inline fun <reified K: KGrapheneObject> getKGrapheneObjectsFromChain(vararg uid: Long): K {
//        return NetworkService.sendOrNull(CallMethod.GET_OBJECTS, listOf(listOf(("1.2.$uid")), false)) {
//            log("getKObjectFromChain $")
////            config1.decodeFromString(ListSerializer(serializer()), (it as JSONArray).toString())
//            config1.decodeFromString(ListSerializer(serializer<K>()), (it as JSONArray).toString()).firstOrNull()
//        }
//    }

//    suspend fun getKAccountFromChain(uid: Long): KAccountObject = getKObjectFromChain(uid)


}
