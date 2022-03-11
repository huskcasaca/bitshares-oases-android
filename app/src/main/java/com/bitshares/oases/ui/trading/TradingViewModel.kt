package com.bitshares.oases.ui.trading

import android.app.Application
import android.content.Intent
import android.content.res.Resources
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Market
import bitshareskit.models.Ticker
import bitshareskit.objects.LimitOrderObject
import bitshareskit.operations.LimitOrderCancelOperation
import bitshareskit.operations.LimitOrderCreateOperation
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.chain_repo.MarketRepository
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.getJson
import modulon.extensions.livedata.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.log10

class TradingViewModel(application: Application) : AccountViewModel(application) {

    val isHorizontalLayout get() = Resources.getSystem().configuration.fontScale * Resources.getSystem().displayMetrics.density <= 3 && !Settings.KEY_IS_VERTICAL_LAYOUT.value
    val isVerticalLayoutEnabled = Settings.KEY_IS_VERTICAL_LAYOUT.map { it || !isHorizontalLayout }

    val marketInternal = MutableLiveData<Market>()
    val market = marketInternal.map(viewModelScope) { MarketRepository.getMarketDetail(it) }
    val tickerLive = marketInternal.switchMap(viewModelScope) { MarketRepository.getTickerLive(it) }.withDefault { Ticker.EMPTY }

    private val currentMarketLimitOrdersInternal = combineNonNull(market, limitOrders) { market, orders ->
        orders.filter { (it.salePrice.base.asset.uid == market.base.uid && it.salePrice.quote.asset.uid == market.quote.uid) || (it.salePrice.base.asset.uid == market.quote.uid && it.salePrice.quote.asset.uid == market.base.uid) }
    }

    val currentMarketLimitOrders = combineNonNull(currentMarketLimitOrdersInternal, market) { orders, market -> orders.map { it to market } }.mapChildParallel(viewModelScope) { MarketRepository.getOrderDetail(it.first, it.second) }

    val orderBook = marketInternal.switchMap {
        MarketRepository.getOrderBookLive(it)
    }.distinctUntilChanged()

    val orderBookFixed = marketInternal.switchMap(viewModelScope) {
        MarketRepository.subscribeToMarket(it).asLiveData()
    }.distinctUntilChanged()

//    val precision = MutableLiveData(8)
    val precision = tickerLive.map { (8 - log10(it.latest).toInt()).coerceIn(0..8) }.distinctUntilChanged().toMutableLiveData()

    // FIXME: 25/1/2022 catch     java.lang.NullPointerException: Attempt to invoke virtual method 'java.util.List bitshareskit.entities.OrderBook.getAsks()' on a null object reference
    //        at com.bitshares.android.user_interface.trading.TradingViewModel$special$$inlined$map$3.apply(Transformations.kt:89)
    private val asks = orderBook.map { it?.asks ?: emptyList() }.distinctUntilChanged()
    private val bids = orderBook.map { it?.bids ?: emptyList() }.distinctUntilChanged()

    val roundedAsks = combineNonNull(asks, precision.debounce(viewModelScope, 800)) { orders, precision ->
        orders.groupBy { it.price.setScale(precision, RoundingMode.UP).toPlainString() }.values.map {
            val base = it.sumOf { it.base }
            val quote = it.sumOf { it.quote }
            it.first().copy(base.divide(quote, precision, RoundingMode.UP), base,  quote)
        }
    }

    val roundedBids = combineNonNull(bids, precision.debounce(viewModelScope, 800)) { orders, precision ->
        orders.groupBy { it.price.setScale(precision, RoundingMode.DOWN).toPlainString() }.values.map {
            val base = it.sumOf { it.base }
            val quote = it.sumOf { it.quote }
            it.first().copy(base.divide(quote, precision, RoundingMode.DOWN), base,  quote)
        }
    }


    fun setMarket(market: Market) {
        if (market != Market.EMPTY) marketInternal.value = market
    }

    val baseAsset = marketInternal.map(viewModelScope) { AssetRepository.getAssetWithExtraDataDetail(it.base) }
    val quoteAsset = marketInternal.map(viewModelScope) { AssetRepository.getAssetWithExtraDataDetail(it.quote) }

    // FIXME: 5/10/2021 empty when recreated
    override fun onActivityIntent(intent: Intent?) {
        intent ?: return
        logcat("onActivityIntent", intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> return
            null -> {
                intent.getJson(IntentParameters.MarketTrade.KEY_MARKET, Market.EMPTY).let { setMarket(it) }
                intent.getJson(IntentParameters.Account.KEY_UID, Settings.KEY_CURRENT_ACCOUNT_ID.value).let { setAccountUid(it) }
//            intent.getLocalExtra<AccountObject>(IntentParameters.AccountBrowser.KEY_ACCOUNT)?.let { setAccountUid(it.uid) }
            }
            else -> return
        }


    }

    private val quotePricis get() = quoteAsset.value?.precision ?: 0
    private val basePricis get() = quoteAsset.value?.precision ?: 0

    val buyPriceFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)
    val buyAmountFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)
    val buyTotalFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)

    val sellPriceFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)
    val sellAmountFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)
    val sellTotalFieldNoticed = NonNullMediatorLiveData(modulon.extensions.charset.EMPTY_SPACE)

    val buyPriceFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)
    val buyAmountFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)
    val buyTotalFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)

    val sellPriceFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)
    val sellAmountFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)
    val sellTotalFieldDecimal = NonNullMutableLiveData(BigDecimal.ZERO)

    private val baseBalance = combineLatest(account, accountBalance, baseAsset) { account, balances, asset ->
        if (account != null && asset != null) AssetAmount(balances.orEmpty().find { it.assetUid == asset.uid && it.ownerUid == account.uid }?.balance ?: 0L, asset) else null
    }.filterNotNull()

    private val quoteBalance = combineLatest(account, accountBalance, quoteAsset) { account, balances, asset ->
        if (account != null && asset != null) AssetAmount(balances.orEmpty().find { it.assetUid == asset.uid && it.ownerUid == account.uid }?.balance ?: 0L, asset) else null
    }.filterNotNull()

    val buyAmount = combineNonNull(quoteAsset, buyAmountFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset), asset) }
    val buyTotal = combineNonNull(baseAsset, buyTotalFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset), asset) }
    val buyTotalLeft = combineNonNull(baseBalance, buyTotal) { balance, total -> balance - total }.sources(buyPriceFieldDecimal, buyAmountFieldDecimal, buyTotalFieldDecimal)
    val buyBalanceDecimal = baseBalance.map { ChainPropertyRepository.getFeeReservedValue(it.formattedValue, it.asset.isCore()) }

    val sellAmount = combineNonNull(quoteAsset, sellAmountFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset), asset) }
    val sellTotal = combineNonNull(baseAsset, sellTotalFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset), asset) }
    val sellAmountLeft = combineNonNull(quoteBalance, sellAmount) { balance, amount -> balance - amount }.sources(sellPriceFieldDecimal, sellAmountFieldDecimal, sellTotalFieldDecimal)
    val sellBalanceDecimal = quoteBalance.map { ChainPropertyRepository.getFeeReservedValue(it.formattedValue, it.asset.isCore()) }


    val isBuySliderOnTouch = NonNullMediatorLiveData(false)
    val isSellSliderOnTouch = NonNullMediatorLiveData(false)

    val buyProgress = combineNonNull(buyBalanceDecimal, buyTotalFieldDecimal, isBuySliderOnTouch) { balance, total, touch ->
        if (!touch && balance.isNotZero()) total.toDouble() / balance.toDouble() else null
    }.filterNotNull()

    val sellProgress = combineNonNull(sellBalanceDecimal, sellAmountFieldDecimal, isSellSliderOnTouch) { balance, amount, touch ->
        if (!touch && balance.isNotZero()) amount.toDouble() / balance.toDouble() else null
    }.filterNotNull()

    fun setPrice(price: BigDecimal, buy: Boolean) {
        (if (buy) buyPriceFieldNoticed else sellPriceFieldNoticed).value = price.toPlainString()
        changePriceField(price.toPlainString(), true, buy)
    }

    fun changePriceField(text: String, fromUser: Boolean, buy: Boolean) {
        val dec = text.toBigDecimalOrNull()?.stripTrailingZerosFixes() ?: BigDecimal.ZERO
        if (buy) {
            buyPriceFieldDecimal.value = dec
            if (fromUser && dec.isNotZero()) {
                if (buyTotalFieldDecimal.value.isNotZero()) {
                    val amount = buyTotalFieldDecimal.value.divide(dec, quotePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    buyAmountFieldDecimal.value = amount
                    buyAmountFieldNoticed.value = amount.toPlainString()
                } else if (buyAmountFieldDecimal.value.isNotZero()) {
                    val amount = buyAmountFieldDecimal.value.multiply(dec).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    buyTotalFieldDecimal.value = amount
                    buyTotalFieldNoticed.value = amount.toPlainString()
                }
            }
        } else {
            sellPriceFieldDecimal.value = dec
            if (fromUser && dec.isNotZero()) {
                if (sellTotalFieldDecimal.value.isNotZero()) {
                    val amount = sellTotalFieldDecimal.value.divide(dec, quotePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    sellAmountFieldDecimal.value = amount
                    sellAmountFieldNoticed.value = amount.toPlainString()
                } else if (buyAmountFieldDecimal.value.isNotZero()) {
                    val amount = sellAmountFieldDecimal.value.multiply(dec).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    sellTotalFieldDecimal.value = amount
                    sellTotalFieldNoticed.value = amount.toPlainString()
                }
            }
        }
    }

    fun changeAmountField(text: String, fromUser: Boolean, buy: Boolean) {
        val dec = text.toBigDecimalOrNull()?.stripTrailingZerosFixes() ?: BigDecimal.ZERO
        if (buy) {
            buyAmountFieldDecimal.value = dec
            if (fromUser) {
                val amount = buyPriceFieldDecimal.value.multiply(dec).setScale(quotePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                buyTotalFieldDecimal.value = amount
                buyTotalFieldNoticed.value = amount.toPlainString()
            }
        } else {
            sellAmountFieldDecimal.value = dec
            if (fromUser) {
                val amount = sellPriceFieldDecimal.value.multiply(dec).setScale(quotePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                sellTotalFieldDecimal.value = amount
                sellTotalFieldNoticed.value = amount.toPlainString()
            }
        }
    }

    fun changeTotalField(text: String, fromUser: Boolean, buy: Boolean) {
        val dec = text.toBigDecimalOrNull()?.stripTrailingZerosFixes() ?: BigDecimal.ZERO
        if (buy) {
            buyTotalFieldDecimal.value = dec
            if (fromUser) {
                if (buyPriceFieldDecimal.value.isNotZero()) {
                    val amount = dec.divide(buyPriceFieldDecimal.value, quotePricis + basePricis, RoundingMode.HALF_EVEN).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    buyAmountFieldDecimal.value = amount
                    buyAmountFieldNoticed.value = amount.toPlainString()
                }
            }
        } else {
            sellTotalFieldDecimal.value = dec
            if (fromUser) {
                if (sellPriceFieldDecimal.value.isNotZero()) {
                    val amount = dec.divide(sellPriceFieldDecimal.value, quotePricis + basePricis, RoundingMode.HALF_EVEN).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                    sellAmountFieldDecimal.value = amount
                    sellAmountFieldNoticed.value = amount.toPlainString()
                }
            }
        }
    }

    fun switchRatio(scaled: Double, buy: Boolean) {
        if (buy) {
            val balance = buyBalanceDecimal.value ?: BigDecimal.ZERO
            val amount = balance.multiply(BigDecimal(scaled)).setScale(quotePricis, RoundingMode.HALF_DOWN).coerceAtMost(balance).stripTrailingZerosFixes()
            buyTotalFieldDecimal.value = amount
            buyTotalFieldNoticed.value = amount.toPlainString()
            if (buyPriceFieldDecimal.value.isNotZero()) {
                val amount1 = amount.divide(buyPriceFieldDecimal.value, quotePricis + basePricis, RoundingMode.HALF_EVEN).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                buyAmountFieldDecimal.value = amount1
                buyAmountFieldNoticed.value = amount1.toPlainString()
            }
        } else {
            val balance = sellBalanceDecimal.value ?: BigDecimal.ZERO
            val amount = balance.multiply(BigDecimal(scaled)).setScale(quotePricis, RoundingMode.HALF_DOWN).coerceAtMost(balance).stripTrailingZerosFixes()
            sellAmountFieldDecimal.value = amount
            sellAmountFieldNoticed.value = amount.toPlainString()
            if (sellPriceFieldDecimal.value.isNotZero()) {
//                val amount1 = amount.divide(buyPriceFieldDecimal.value, quotePricis + basePricis, RoundingMode.HALF_EVEN).setScale(basePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixed()
                val amount1 = sellPriceFieldDecimal.value.multiply(amount).setScale(quotePricis, RoundingMode.HALF_EVEN).stripTrailingZerosFixes()
                sellTotalFieldDecimal.value = amount1
                sellTotalFieldNoticed.value = amount1.toPlainString()
            }
        }
    }

    fun switchSlider(touched: Boolean, buy: Boolean) {
        (if (buy) isBuySliderOnTouch else isSellSliderOnTouch).value = touched
    }

    fun clearFields() {
        buyPriceFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
        buyAmountFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
        buyTotalFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
        sellPriceFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
        sellAmountFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
        sellTotalFieldNoticed.value = modulon.extensions.charset.EMPTY_SPACE
    }

    var isBuy = false


    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val limitOrderCreateOperation = transaction.map { it.operations.firstOrNull().asOrNull<LimitOrderCreateOperation>() }.filterNotNull()
    val limitOrderCancelOperation = transaction.map { it.operations.firstOrNull().asOrNull<LimitOrderCancelOperation>() }.filterNotNull()

    // FIXME: 9/10/2021 Refactor of val operation cause [const val KEY_OPERATION = "operation"] get renamed to "limitOrderCreateOperation", consider other potential impacts

    fun buildTransaction() = buildTransaction {
        addOperation {
            val seller = account.value!!
            val sells = if (isBuy) formatAssetAmount(buyTotalFieldDecimal.value!!, baseAsset.value!!) else formatAssetAmount(sellAmountFieldDecimal.value!!, quoteAsset.value!!)
            val receives = if (isBuy) formatAssetAmount(buyAmountFieldDecimal.value!!, quoteAsset.value!!) else formatAssetAmount(sellTotalFieldDecimal.value!!, baseAsset.value!!)
            val expiration = Date(ChainPropertyRepository.currentChainTime.value + ChainConfig.TWO_YEAR_MILLIS)
            LimitOrderCreateOperation(
                seller,
                sells,
                receives,
                expiration,
                false
            )
        }
        onSuccess { clearFields() }
        transactionBuilder.value = this
        checkFees()
    }

    fun createCancelOperation(order: LimitOrderObject): LimitOrderCancelOperation {
        return LimitOrderCancelOperation(account.value.orEmpty(), order.orEmpty())
    }

    private var precisionToChange = 0

    fun changePrecision(precision: Int) {
        precisionToChange = precision
    }


}