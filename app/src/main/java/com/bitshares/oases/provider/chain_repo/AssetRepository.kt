package com.bitshares.oases.provider.chain_repo

import bitshareskit.chain.ChainConfig
import bitshareskit.models.AssetAmount
import bitshareskit.models.SimplePrice
import bitshareskit.objects.AssetBitassetData
import bitshareskit.objects.AssetDynamicData
import bitshareskit.objects.AssetObject
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.Source
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.java_json.JSONArray
import org.java_json.JSONObject

object AssetRepository {

    private val assetDao = BlockchainDatabase.INSTANCE.assetDao()
    private val assetBitassetDataDao = BlockchainDatabase.INSTANCE.assetBitassetDataDao()

    suspend fun getCoreAssetFromChain() = GrapheneRepository.getObjectFromChain<AssetObject>(ChainConfig.GLOBAL_INSTANCE)
    suspend fun getCoreAsset() = (GrapheneRepository.getObject(ChainConfig.GLOBAL_INSTANCE) ?: Graphene.KEY_CORE_ASSET.value).also {
        withContext(Dispatchers.Main) { Graphene.KEY_CORE_ASSET.value = it }
    }

    suspend fun getAsset(uid: Long, method: Source = Source.LOCAL_FIRST) = GrapheneRepository.getGrapheneObject<AssetObject>(uid, method)
    suspend fun getAssetOrDefault(uid: Long, defaultValue: AssetObject = AssetObject.EMPTY, method: Source = Source.LOCAL_FIRST) = getAsset(uid, method) ?: defaultValue

    suspend fun getAssetWithExtraData(uid: Long) = GrapheneRepository.getObject<AssetObject>(uid)?.apply {
        if (bitassetData.isExist) getAssetBitassetData(bitassetData.uid)?.let { bitassetData = it }
        if (dynamicData.isExist) getAssetDynamicData(dynamicData.uid)?.let { dynamicData = it }
    }

    suspend fun getAssetWithExtraDataLocalFirst(uid: Long) = GrapheneRepository.getObject<AssetObject>(uid)?.apply {
        if (bitassetData.isExist) getAssetBitassetData(bitassetData.uid, Source.LOCAL_FIRST)?.let { bitassetData = it }
        if (dynamicData.isExist) getAssetDynamicData(dynamicData.uid, Source.LOCAL_FIRST)?.let { dynamicData = it }
    }

    suspend fun getAssetWithBitassetData(uid: Long) = GrapheneRepository.getObject<AssetObject>(uid)?.apply {
        if (bitassetData.isExist) getAssetBitassetData(bitassetData.uid, Source.LOCAL_FIRST)?.let { bitassetData = it }
    }

    suspend fun getAssetBitassetData(uid: Long, method: Source = Source.CHAIN_FIRST) = GrapheneRepository.getGrapheneObject<AssetBitassetData>(uid, method)
    suspend fun getAssetDynamicData(uid: Long, method: Source = Source.CHAIN_FIRST) = GrapheneRepository.getGrapheneObject<AssetDynamicData>(uid, method)

    fun getAssetLive(uid: Long) = GrapheneRepository.getObjectLive<AssetObject>(uid)
    fun getAssetLiveFromDatabase(uid: Long) = GrapheneRepository.getObjectLiveFromDatabase<AssetObject>(uid)

    fun getAssetBitassetDataLive(uid: Long) = GrapheneRepository.getObjectLive<AssetBitassetData>(uid)
    fun getAssetDynamicDataLive(uid: Long) = GrapheneRepository.getObjectLive<AssetDynamicData>(uid)


    suspend fun getAssetObject(uid: Long) = GrapheneRepository.getObject<AssetObject>(uid)
    suspend fun getAssetObjectFromChain(uid: Long) = GrapheneRepository.getObjectFromChain<AssetObject>(uid)

    suspend fun getPriceDetail(price: SimplePrice): SimplePrice {
        price.base.asset = getAssetDetail(price.base.asset)
        price.quote.asset = getAssetDetail(price.quote.asset)
        return price
    }

    suspend fun getPriceDetailWithExtraData(price: SimplePrice): SimplePrice {
        price.base.asset = getAssetWithExtraDataDetail(price.base.asset)
        price.quote.asset = getAssetWithExtraDataDetail(price.quote.asset)
        return price
    }

    suspend fun SimplePrice.awaitPriceDetailWithExtraData(): SimplePrice = apply {
        base.asset = getAssetWithExtraDataDetail(base.asset)
        quote.asset = getAssetWithExtraDataDetail(quote.asset)
    }

    suspend fun getAssetAmountDetail(amount: AssetAmount): AssetAmount {
        amount.asset = getAssetDetail(amount.asset)
        return amount
    }

    suspend fun getAssetDetail(asset: AssetObject): AssetObject = getAsset(asset.uid) ?: asset
    suspend fun getAssetWithExtraDataDetail(asset: AssetObject): AssetObject = getAssetWithExtraDataLocalFirst(asset.uid) ?: asset

    suspend fun add(asset: AssetObject) = assetDao.add(asset)
    suspend fun addAll(assets: List<AssetObject>) = assetDao.add(assets)
    suspend fun remove(asset: AssetObject) = assetDao.remove(asset)
    suspend fun removeAll() = assetDao.clear()


    suspend fun getAssetOrThrow(symbolOrId: String): AssetObject? {
        return NetworkService.sendOrThrow(CallMethod.GET_ASSETS, listOf(listOf(symbolOrId), false)) {
            runCatching { AssetObject((it as JSONArray).getJSONObject(0)) }.getOrThrow()
        }
    }

    suspend fun getAssetOrDefault(symbolOrId: String, defaultValue: AssetObject = AssetObject.EMPTY): AssetObject = getAssetOrNull(symbolOrId) ?: defaultValue
    suspend fun getAssetOrNull(symbolOrId: String): AssetObject? = NetworkService.sendOrNull(CallMethod.GET_ASSETS, listOf(listOf(symbolOrId), false)) {
        runCatching { AssetObject((it as JSONArray).getJSONObject(0)) }.getOrNull()
    }

    suspend fun getAssetWithBitassetDataOrNull(symbolOrId: String): AssetObject? = getAssetOrNull(symbolOrId)?.apply {
        if (bitassetData.isExist) getAssetBitassetData(bitassetData.uid, Source.LOCAL_FIRST)?.let { bitassetData = it }
    }

    suspend fun lookupAssetSymbols(lowerBoundName: String, limit: Int = 30): List<AssetObject> {
        val data: List<Any> = listOf(lowerBoundName, limit.coerceIn(1..101))
        return NetworkService.sendOrThrow(CallMethod.LIST_ASSETS, data) {
            (it as JSONArray).map { AssetObject(it as JSONObject) }
        }
    }

}