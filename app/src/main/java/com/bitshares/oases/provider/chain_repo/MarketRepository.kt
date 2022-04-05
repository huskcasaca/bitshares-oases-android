package com.bitshares.oases.provider.chain_repo

import androidx.lifecycle.LiveData
import bitshareskit.chain.ChainConfig
import bitshareskit.entities.LimitOrder
import bitshareskit.entities.OrderBook
import bitshareskit.extensions.collateralAmount
import bitshareskit.extensions.debtAmount
import bitshareskit.extensions.rebase
import bitshareskit.models.Market
import bitshareskit.models.SimplePrice
import bitshareskit.models.Ticker
import bitshareskit.models.encodeMarketInstance
import bitshareskit.objects.AssetObject
import bitshareskit.objects.CallOrder
import bitshareskit.objects.CallOrderObject
import bitshareskit.objects.LimitOrderObject
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.database.entities.TickerEntity
import com.bitshares.oases.netowrk.java_websocket.GrapheneSocketLiveData
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.provider.chain_repo.AssetRepository.awaitPriceDetailWithExtraData
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import modulon.extensions.livedata.emptyLiveData
import modulon.extensions.livedata.mapChild
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

object MarketRepository {

    private val tickerDao = BlockchainDatabase.INSTANCE.tickerDao()

    suspend fun addTickerEntity(tickers: Collection<TickerEntity>) = tickerDao.add(tickers)

    fun getTickerEntityLiveFromDatabase(uids: List<Market>): LiveData<List<Ticker>> = tickerDao.getLive(uids.map { encodeMarketInstance(it.base.uid, it.quote.uid) }).mapChild { it.ticker }

    suspend fun getTickerEntity(base: AssetObject, quote: AssetObject): Ticker? {
        return getTicker(base, quote)
//            ?: tickerDao.get(encodeMarketInstance(base.uid.toUInt(), quote.uid.toUInt()).toLong())?.ticker
    }

    suspend fun getTickerWA(base: AssetObject, quote: AssetObject): Ticker? {
        return NetworkService.sendOrNull(CallMethod.GET_TICKER, listOf(base.id, quote.id)) {
            runCatching { TickerEntity(it as JSONObject, encodeMarketInstance(base.uid, quote.uid)) }.onSuccess {
                blockchainDatabaseScope.launch { tickerDao.add(it) }
            }
            runCatching { Ticker.fromJson(it as JSONObject, base, quote) }.getOrNull()
        }
    }

    suspend fun getTickerLF(base: AssetObject, quote: AssetObject): Ticker? {
        return tickerDao.get(encodeMarketInstance(base.uid, quote.uid))?.ticker ?: getTickerWA(base, quote)
    }

    suspend fun getTicker(base: AssetObject, quote: AssetObject): Ticker? {
        return NetworkService.sendOrNull(CallMethod.GET_TICKER, listOf(base.id, quote.id)) {
            runCatching { getTickerDetailed(Ticker.fromJson(it as JSONObject, base, quote)) }.getOrNull()
        }
    }

    fun getTickerLive(base: AssetObject, quote: AssetObject): GrapheneSocketLiveData<Ticker?> {
        return NetworkService.sendLive(CallMethod.GET_TICKER, listOf(base.id, quote.id), 18_000L) {
            runCatching { TickerEntity(it as JSONObject, encodeMarketInstance(base.uid, quote.uid)) }.onSuccess {
                blockchainDatabaseScope.launch {
                    tickerDao.add(it)
                }
            }
            runCatching { Ticker.fromJson(it as JSONObject, base, quote) }.getOrNull()
        }
    }

    fun getTickerLive(pair: Market) = if (pair != Market.EMPTY) getTickerLive(pair.base, pair.quote) else emptyLiveData()

    suspend fun getTickerDetailed(ticker: Ticker): Ticker {
        return coroutineScope {
            val base = withContext(Dispatchers.Default) { AssetRepository.getAssetWithExtraDataDetail(ticker.base) }
            val quote = withContext(Dispatchers.Default) { AssetRepository.getAssetWithExtraDataDetail(ticker.quote) }
            ticker.copy(base, quote)
        }
    }

    suspend fun getTradePairDetailed(pair: Market): Market {
        return coroutineScope {
            val base = withContext(Dispatchers.Default) { AssetRepository.getAssetWithExtraDataDetail(pair.base) }
            val quote = withContext(Dispatchers.Default) { AssetRepository.getAssetWithExtraDataDetail(pair.quote) }
            pair.copy(base, quote)
        }
    }

    suspend fun getMarketHistoryBuckets(): List<UInt> {
        return NetworkService.sendOrThrow(CallMethod.GET_MARKET_HISTORY_BUCKETS) {
            runCatching { (it as JSONArray).map { (it as Int).toUInt() } }.getOrDefault(emptyList())
        }
    }

    suspend fun getMarketHistory(base: AssetObject, quote: AssetObject, bucketSeconds: UInt, start: Date, end: Date) {
        return
    }

    suspend fun getMarketDetail(market: Market): Market = Market(AssetRepository.getAssetDetail(market.base), AssetRepository.getAssetDetail(market.quote))

    // TODO: 3/10/2021 get limit in exception
    // returns Pair of Bid and Ask Orders
    suspend fun getOrderBook(market: Market, limit: Int = 50) = getOrderBook(market.base, market.quote, limit)
    suspend fun getOrderBook(base: AssetObject, quote: AssetObject, limit: Int = 50): OrderBook {
        return NetworkService.sendOrNull(CallMethod.GET_ORDER_BOOK, listOf(base.id, quote.id, limit)) {
            runCatching { OrderBook.fromJson(it as JSONObject) }.getOrDefault(OrderBook.EMPTY)
        }
    }

    fun getOrderBookLive(market: Market, limit: Int = 50) = if (market != Market.EMPTY) getOrderBookLive(market.base, market.quote, limit) else emptyLiveData()
    fun getOrderBookLive(base: AssetObject, quote: AssetObject, limit: Int = 50): LiveData<OrderBook> {
        return NetworkService.sendLive(CallMethod.GET_ORDER_BOOK, listOf(base.id, quote.id, limit)) {
            runCatching { OrderBook.fromJson(it as JSONObject) }.getOrDefault(OrderBook.EMPTY)
        }
    }

    suspend fun subscribeToMarket(market: Market) = subscribeToMarket(market.base, market.quote)
    suspend fun subscribeToMarket(base: AssetObject, quote: AssetObject): Flow<Any?> {
        return NetworkService.sendSubscribe(CallMethod.SUBSCRIBE_TO_MARKET, listOf(base.id, quote.id)) {
        }
    }

    // TODO: 3/10/2021 get all limitation config
    suspend fun getLimitOrders(market: Market, limit: Int = 300) = getLimitOrders(market.base, market.quote, limit)
    suspend fun getLimitOrders(base: AssetObject, quote: AssetObject, limit: Int = 300): List<LimitOrderObject> {
        return NetworkService.sendOrNull(CallMethod.GET_LIMIT_ORDERS, listOf(base.id, quote.id, limit)) {
            runCatching { (it as JSONArray).map { LimitOrderObject(it as JSONObject) } }.getOrDefault(emptyList())
        }
    }

    suspend fun getOrderDetail(order: LimitOrderObject, market: Market): LimitOrder {
        val price = withContext(Dispatchers.IO) { AssetRepository.getPriceDetail(order.salePrice) }
        return LimitOrder(price.base, price.quote, order, market)
    }

    suspend fun getCallOrder(order: CallOrderObject): CallOrder {
        return coroutineScope {
            val borrower = async { AccountRepository.getAccountDetail(order.borrower) }
            val collateral = async { AssetRepository.getAssetAmountDetail(order.collateralAmount) }
            val debt = async { AssetRepository.getAssetAmountDetail(order.debtAmount) }
            val price = async { SimplePrice(order.collateralAmount, order.debtAmount).awaitPriceDetailWithExtraData().rebase(AssetObject.CORE_ASSET).let { SimplePrice(it.base * ChainConfig.GRAPHENE_RATIO_SCALE, it.quote * it.quote.asset.bitassetData.currentFeed.maintenanceCollateralRatio) } }
            CallOrder(order, borrower.await(), collateral.await(), debt.await(), price.await())
        }
    }

}

