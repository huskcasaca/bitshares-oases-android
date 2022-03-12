package com.bitshares.oases.ui.main.market

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import bitshareskit.chain.ChainConfig
import bitshareskit.chain.ChainConfig.EMPTY_INSTANCE
import bitshareskit.models.Market
import bitshareskit.models.Ticker
import bitshareskit.objects.AssetObject
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.provider.chain_repo.MarketRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.coroutine.debounce
import modulon.extensions.livedata.*

class MarketViewModel(application: Application) : BaseViewModel(application) {

    val selectedMarket = NonNullMutableLiveData(0)
//    val selectedMarket = Settings.KEY_LAST_MARKET_INDEX

    private val markets = (if (AppConfig.ENABLE_MARKET_MODIFICATION) Settings.KEY_MARKETS else Settings.KEY_MARKETS.default).map { it.toList() }

    private val tickersInternal = markets.switchMap {
        MarketRepository.getTickerEntityLiveFromDatabase(it)
    }.mapChildParallel(viewModelScope) {
        MarketRepository.getTickerDetailed(it)
    }.sources {
        markets.switchMap { combineLatest(it.map { MarketRepository.getTickerLive(it) }) }
    }

    private val tickerInstanceComparator = Comparator { o1: Ticker, o2: Ticker ->
        o1.base.uid.compareTo(o2.base.uid).shl(16) + o1.quote.uid.compareTo(o2.quote.uid)
    }

    private val sorter = NonNullMutableLiveData(tickerInstanceComparator)

    val tickers = combineNonNull(markets, tickersInternal, sorter) { marketsDetail, tickersInternal, sorter ->
        val tickersDetailSorted = marketsDetail.map { Ticker(it.base, it.quote) }.toSortedSet(tickerInstanceComparator)
        val tickersInternalSorted = tickersInternal.filterNotNull().filter { tickersDetailSorted.contains(it) }.toSortedSet(tickerInstanceComparator)
        (tickersDetailSorted - tickersInternalSorted + tickersInternalSorted).toList()
    }

    fun getFilteredTickers(uid: Long?) = if (uid == null || uid == EMPTY_INSTANCE) tickers else tickers.filterChild { it.base.uid == uid }

    val invertColor = globalPreferenceManager.INVERT_COLOR.distinctUntilChanged()

    fun addMarket(market: Market) {
        Settings.KEY_MARKETS.value = Settings.KEY_MARKETS.value + market
    }

    fun addMarket(base: Long, quote: Long) = addMarket(Market(base, quote))

    fun removeMarket(market: Market) {
        Settings.KEY_MARKETS.value = Settings.KEY_MARKETS.value - market
    }

    fun removeMarket(base: Long, quote: Long) = removeMarket(Market(base, quote))

    // dialog
    private val baseAsset = NonNullMutableLiveData(AssetObject.EMPTY)
    private val quoteAsset = NonNullMutableLiveData(AssetObject.EMPTY)
    private var baseField = EMPTY_SPACE
    private var quoteField = EMPTY_SPACE
    val isBaseFieldError = NonNullMutableLiveData(false)
    val isQuoteFieldError = NonNullMutableLiveData(false)

    private val changeBaseAsset = debounce(viewModelScope) { nameOrId: String ->
        if (nameOrId != EMPTY_SPACE) viewModelScope.launch(Dispatchers.IO) {
            val asset = AssetRepository.getAssetOrNull(nameOrId) ?: AssetObject.EMPTY
            if (nameOrId == baseField && (nameOrId == asset.symbol || nameOrId == asset.id)) withContext(Dispatchers.Main) { baseAsset.value = asset }
        } else baseAsset.value = AssetObject.EMPTY
    }
    private val changeQuoteAsset = debounce(viewModelScope) { nameOrId: String ->
        if (nameOrId != EMPTY_SPACE) viewModelScope.launch(Dispatchers.IO) {
            val asset = AssetRepository.getAssetOrNull(nameOrId) ?: AssetObject.EMPTY
            if (nameOrId == quoteField && (nameOrId == asset.symbol || nameOrId == asset.id)) withContext(Dispatchers.Main) { quoteAsset.value = asset }
        } else quoteAsset.value = AssetObject.EMPTY
    }

    private val market = combineFirst(baseAsset, quoteAsset) { base, quote -> if (base != null && quote != null) Market(base, quote) else null }
    private val ticker = market.filterNotNull().switchMap { MarketRepository.getTickerLive(it) }
    val tickerToAdd = combineFirst(market, ticker) { market, ticker ->
        when {
            market == null -> Ticker.EMPTY
            ticker == null || market != ticker.market -> Ticker(market.base, market.quote)
            else -> ticker
        }
    }.distinctUntilChanged()

    fun changeBaseField(text: String) {
        baseField = text
        isBaseFieldError.value = false
        changeBaseAsset.invoke(text)
    }

    fun changeQuoteField(text: String) {
        quoteField = text
        isQuoteFieldError.value = false
        changeQuoteAsset.invoke(text)
    }

    fun addMarket(): Boolean {
        if (baseAsset.value == AssetObject.EMPTY) isBaseFieldError.value = true
        if (quoteAsset.value == AssetObject.EMPTY) isQuoteFieldError.value = true
        val market = market.value
        if (market != null && market.base.isExist && market.quote.isExist) addMarket(market)
        return market != null && market.base.isExist && market.quote.isExist
    }

    // marketInternal group

    val marketGroupNames = (if (AppConfig.ENABLE_MARKET_GROUP_MODIFICATION) Settings.KEY_MARKET_GROUPS else Settings.KEY_MARKET_GROUPS.default)

    val marketGroupInternal = marketGroupNames.mapChildParallel(viewModelScope) {
        AssetRepository.getAssetOrNull(it)
    }.filterChildNotNull()

    val marketGroupToChange = NonNullMutableLiveData(AssetObject.EMPTY)

    val isMarketGroupFieldError = NonNullMutableLiveData(false)

    private var marketGroupField = EMPTY_SPACE

    private val changeMarketGroupAsset = debounce(viewModelScope) { nameOrId: String ->
        if (nameOrId != EMPTY_SPACE) viewModelScope.launch(Dispatchers.IO) {
            val asset = AssetRepository.getAssetOrNull(nameOrId) ?: AssetObject.EMPTY
            if (nameOrId == marketGroupField && (nameOrId == asset.symbol || nameOrId == asset.id)) withContext(Dispatchers.Main) { marketGroupToChange.value = asset }
        } else marketGroupToChange.value = AssetObject.EMPTY
    }

    fun changeMarketGroupField(text: String) {
        marketGroupField = text
        isMarketGroupFieldError.value = false
        changeMarketGroupAsset.invoke(text)
    }

    fun addMarketGroup(): Boolean {
        val asset = marketGroupToChange.value
        return if (asset.uid != ChainConfig.EMPTY_INSTANCE) {
            Settings.KEY_MARKET_GROUPS.value = Settings.KEY_MARKET_GROUPS.value + asset.symbol
            true
        } else {
            isMarketGroupFieldError.value = true
            false
        }
    }

    fun removeMarketGroup(asset: AssetObject): Boolean {
        return if (asset.uid != ChainConfig.EMPTY_INSTANCE) {
            Settings.KEY_MARKET_GROUPS.value = Settings.KEY_MARKET_GROUPS.value - asset.symbol
            true
        } else false
    }

    fun resetField() {
        isBaseFieldError.value = false
        isQuoteFieldError.value = false
        isMarketGroupFieldError.value = false
        baseField = EMPTY_SPACE
        quoteField = EMPTY_SPACE
        marketGroupField = EMPTY_SPACE
        baseAsset.value = AssetObject.EMPTY
        quoteAsset.value = AssetObject.EMPTY
        marketGroupToChange.value = AssetObject.EMPTY
    }


}