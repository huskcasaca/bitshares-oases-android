package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.switchMap
import bitshareskit.chain.ChainConfig
import bitshareskit.entities.Block
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.isCore
import bitshareskit.extensions.isZero
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AssetObject
import bitshareskit.objects.DynamicGlobalPropertyObject
import bitshareskit.objects.GlobalPropertyObject
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.blockchainNetworkScope
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.GrapheneSocketLiveData
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.Source
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.*
import modulon.extensions.livedata.NonNullMediatorLiveData
import modulon.extensions.livedata.emptyLiveData
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.math.BigDecimal
import java.util.*
import kotlin.concurrent.schedule

object ChainPropertyRepository {

    // chainTime - System.currentTimeMillis()

    val feeReserved = Settings.KEY_RESERVED_FEE
    val coreAsset = Graphene.KEY_CORE_ASSET
//    val feeReservedDecimal = combineNonNull(feeReserved, coreAsset) { fee, asset -> formatAssetBigDecimal(fee, asset) }.withDefault { BigDecimal.ZERO }

    val feeReservedValue get() = formatAssetBigDecimal(Settings.KEY_RESERVED_FEE.value, Graphene.KEY_CORE_ASSET.value)
    val feeReservedAmount get() = AssetAmount(feeReserved.value, coreAsset.value)

    fun getFeeReservedValue(value: BigDecimal, reserve: Boolean): BigDecimal = if (reserve) (value - feeReservedValue).coerceAtLeast(BigDecimal.ZERO).let { if (it.isZero()) value else it }.stripTrailingZeros() else value

    fun getFeeReservedAmount(amount: AssetAmount) = if (amount.asset.isCore()) amount - feeReservedAmount else amount

    private val chainTime = NonNullMediatorLiveData(System.currentTimeMillis())
    private val timeOffset = NonNullMediatorLiveData(0L)
    val lastBlockOffset = NonNullMediatorLiveData(0L)
    val currentChainTime = NonNullMediatorLiveData(System.currentTimeMillis())
    val currentSystemTime = NonNullMediatorLiveData(System.currentTimeMillis())
    val currentChainId = Graphene.KEY_CHAIN_ID

    val chainDateTime get() = Date(currentChainTime.value)
    val chainId get() = Graphene.KEY_CHAIN_ID.value
    val chainSymbol get() = Graphene.KEY_SYMBOL.value
//    val chainInfo get() = ChainInfo(
//        Graphene.KEY_CHAIN_ID.value,
//        Graphene.KEY_SYMBOL.value,
//    )

    val chains get() = Graphene.KEY_CHAIN_CONFIG.value

    fun resolveSymbol(chainId: String) = chains[chainId] ?: AssetObject.CORE_ASSET_SYMBOL

    private val dynamicGlobalPropertyDao = BlockchainDatabase.INSTANCE.dynamicGlobalPropertyDao()
    private val globalPropertyDao = BlockchainDatabase.INSTANCE.globalPropertyDao()
    private val blockDao = BlockchainDatabase.INSTANCE.blockDao()

    val dynamicGlobalProperty = dynamicGlobalPropertyDao.getLive(0)
    val globalProperty = globalPropertyDao.getLive(0)

    suspend fun getLastDynamicGlobalProperty() = GrapheneRepository.getObjectFromChain<DynamicGlobalPropertyObject>(0)
    suspend fun getLastDynamicGlobalPropertyCF() = GrapheneRepository.getGrapheneObject<DynamicGlobalPropertyObject>(0, Source.CHAIN_FIRST)
    suspend fun getLastGlobalProperty() = GrapheneRepository.getObjectFromChain<GlobalPropertyObject>(0)

    suspend fun getDynamicGlobalProperty() = GrapheneRepository.getObject<DynamicGlobalPropertyObject>(0)
    suspend fun getGlobalProperty() = GrapheneRepository.getObject<GlobalPropertyObject>(0)

    suspend fun getChainId() = NetworkService.sendOrNull(CallMethod.GET_CHAIN_ID) {
        runCatching { it as String }.onSuccess {
            Graphene.KEY_CHAIN_ID.value = it
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    // TODO: replace with getBlockHeader
    suspend fun getBlock(height: Long): Block? {
        return NetworkService.sendOrNull(CallMethod.GET_BLOCK, listOf(height)) {
            runCatching { Block(it as JSONObject) }.onSuccess {
                blockDao.add(it)
            }.getOrNull()
        }
    }

    suspend fun getBlockHeader(height: Long): Block? {
        return NetworkService.sendOrNull(CallMethod.GET_BLOCK_HEADER, listOf(height)) {
            runCatching { Block(it as JSONObject) }.onSuccess {
                blockDao.addIgnore(it)
            }.getOrNull()
        }
    }

    suspend fun getBlockHeaderBatch(vararg height: Long): Map<Long, Block> {
        if (height.isEmpty()) return emptyMap()
        return NetworkService.sendOrNull(CallMethod.GET_BLOCK_HEADER_BATCH, listOf(height.distinct().toList())) {
            runCatching { (it as JSONArray).map { (it as JSONArray).optLong(0) to Block.fromJson(it.optJSONObject(1)) }.toMap() }.onSuccess {
                blockDao.addIgnore(it.values)
            }.getOrNull().orEmpty()
        }
    }

    suspend fun getBlockLocal(height: Long): Block? {
        return blockDao.get(height) ?: getBlock(height)
    }

    suspend fun getBlockHeaderBatchLocal(vararg height: Long): Map<Long, Block> {
        if (height.isEmpty()) return emptyMap()
        val localBlocks = coroutineScope {
            height.distinct().map { num -> async { blockDao.get(num)?.let { num to it } } }.awaitAll().filterNotNull().toMap()
        }
        return localBlocks + getBlockHeaderBatch(*height.filterNot { localBlocks.containsKey(it) }.toLongArray())
    }

    val dynamicGlobalPropertyFromChainLive = GrapheneSocketLiveData(CallMethod.GET_DYNAMIC_GLOBAL_PROPERTIES, period = Graphene.KEY_BLOCK_INTERVAL.value * 1000L) {
        DynamicGlobalPropertyObject(it as JSONObject)
    }

    private fun getDynamicGlobalPropertyFromChain() = dynamicGlobalPropertyFromChainLive


    fun start() {
        blockchainNetworkScope.launch(Dispatchers.Main) {
            if (AppConfig.ENABLE_BLOCK_UPDATE) {
                Settings.KEY_ENABLE_BLOCK_UPDATES.switchMap {
                    if (it) getDynamicGlobalPropertyFromChain() else emptyLiveData()
                }.observeForever {
                    if (it != null) {
                        blockchainDatabaseScope.launch { dynamicGlobalPropertyDao.add(it) }
                        chainTime.value = it.time.time
                        timeOffset.value = it.time.time - System.currentTimeMillis()
                    }
                }
                Settings.KEY_ENABLE_BLOCK_UPDATES.switchMap { if (it) GrapheneRepository.getObjectLiveFromChain<GlobalPropertyObject>(ChainConfig.GLOBAL_INSTANCE) else emptyLiveData() }.observeForever {
//                if (it != null) chainTime.value = it.t
                }
            }
            Timer().schedule(1000L, 1000L) {
                val time = timeOffset.value + System.currentTimeMillis()
                currentChainTime.postValue(time)
                currentSystemTime.postValue(System.currentTimeMillis())
                lastBlockOffset.postValue(time - chainTime.value)
            }
        }

    }


}
