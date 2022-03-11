package com.bitshares.oases.ui.account.margin

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.SimplePrice
import bitshareskit.objects.AssetObject
import bitshareskit.operations.CallOrderUpdateOperation
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.getJson
import kotlinx.coroutines.launch
import modulon.extensions.livedata.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.log
import kotlin.math.pow

class CollateralViewModel(application: Application) : AccountViewModel(application) {

    companion object {
        private const val RATIO_DIVIDER_P0 = 0.0
        private const val RATIO_DIVIDER_P1 = 0.15
        private const val RATIO_DIVIDER_P2 = 0.9
        private const val RATIO_DIVIDER_P3 = 1.0
        private const val RATIO_FACTOR = 1.1

        private const val RATIO_SCALE = ChainConfig.GRAPHENE_RATIO_SCALE
        private const val RATIO_WARNING = 800

        private const val ERROR_RATIO = -1
        private const val UNDEFINED_RATIO = -2

    }

    enum class State {
        ERROR, WARNING, NORMAL
    }

    private val ratioScale = BigDecimal(RATIO_SCALE)
    private val errorRatio = BigDecimal(ERROR_RATIO)
    private val undefinedRatio = BigDecimal(UNDEFINED_RATIO)

    val debtAssetInternal = MutableLiveData<AssetObject>()
    val collAssetInternal = Graphene.KEY_CORE_ASSET

    val coreExchangeRate = debtAssetInternal.filterNotNull().map(viewModelScope) { AssetRepository.getPriceDetail(it.coreExchangeRate) }

    private val bitassetData = debtAssetInternal.filterNotNull().map { it.bitassetData.uid }.switchMap { AssetRepository.getAssetBitassetDataLive(it) }

    val debtAssetDetailed = combineNonNull(debtAssetInternal, bitassetData) { asset, bitasset ->
        asset.apply { if (asset.bitassetData.uid == bitasset.uid) bitassetData = bitasset }
    }

    private val mcr = bitassetData.filterNotNull().map { it.currentFeed.maintenanceCollateralRatio }.distinctUntilChanged()
    private val mssr = bitassetData.filterNotNull().map { it.currentFeed.maximumShortSqueezeRatio }.distinctUntilChanged()

    val feedPrice = bitassetData.filterNotNull().map(viewModelScope) { AssetRepository.getPriceDetail(it.currentFeed.settlementPrice) }.map { if (it.quote.asset.uid != AssetObject.CORE_ASSET_UID) it.invertedPair else it }.distinctUntilChanged()

    val isCollateralLocked = NonNullMutableLiveData(false)

    private val debtBalance = combineLatest(account, accountBalance, debtAssetInternal) { account, balances, asset ->
        if (account == null || asset == null) null else AssetAmount(balances.orEmpty().find { it.assetUid == asset.uid && it.ownerUid == account.uid }?.balance ?: 0L, asset)
    }.filterNotNull()

    private val collBalance = combineLatest(account, accountBalance, collAssetInternal) { account, balances, asset ->
        if (account == null || asset == null) null else AssetAmount(balances.orEmpty().find { it.assetUid == asset.uid && it.ownerUid == account.uid }?.balance ?: 0L, asset)
    }.filterNotNull()

    private val debtFieldDecimal = MutableLiveData<BigDecimal>()
    private val collFieldDecimal = MutableLiveData<BigDecimal>()

    private val callOrder = combineNonNull(callOrders, debtAssetInternal) { orders, debt ->
        orders.find { it.debtAmount.asset.uid == debt.uid }
    }

    val debtAmount = combineNonNull(callOrders, debtAssetInternal) { orders, asset ->
        val amount = orders.find { it.debtAmount.asset.uid == asset.uid }?.debtAmount?.amount ?: 0L
        AssetAmount(amount, asset)
    }.distinctUntilChanged().afterEmit { debtFieldDecimal.value = it.formattedValue }

    val collAmount = combineNonNull(callOrders, debtAssetInternal, collAssetInternal) { orders, debt, asset ->
        val amount = orders.find { it.debtAmount.asset.uid == debt.uid }?.collateralAmount?.amount ?: 0L
        AssetAmount(amount, asset)
    }.distinctUntilChanged().afterEmit { collFieldDecimal.value = it.formattedValue }

    private val isSliderOnTouch = NonNullMutableLiveData(false)
    private val isTcrSliderOnTouch = NonNullMutableLiveData(false)

    private val debtAmountChanged = combineNonNull(debtAssetInternal, debtFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset.precision), asset) }
    private val collAmountChanged = combineNonNull(collAssetInternal, collFieldDecimal) { asset, number -> AssetAmount(formatAssetInteger(number, asset.precision), asset) }

    val debtLeft = combineNonNull(debtBalance, debtAmount, debtAmountChanged) { balance, amount, changed -> balance - amount + changed }
    val collLeft = combineNonNull(collBalance, collAmount, collAmountChanged) { balance, amount, changed -> balance + amount - changed }

    val debtField = combineFirst(debtAmount, debtAmountChanged, isSliderOnTouch) { amount, field, touch ->
        if (amount == null || touch == false) null else field ?: amount
    }.filterNotNull().distinctUntilChanged()

    val collField = combineFirst(collAmount, collAmountChanged, isSliderOnTouch) { amount, field, touch ->
        if (amount == null || touch == false) null else field ?: amount
    }.filterNotNull().distinctUntilChanged()

    val callPrice = combineNonNull(debtAmountChanged, collAmountChanged, mcr) { debt, coll, mcr ->
        SimplePrice(debt * mcr, coll * RATIO_SCALE)
    }

    val currentRatio = combineNonNull(debtAmountChanged, collAmountChanged, feedPrice) { debt, coll, feed ->
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) BigDecimal(-1) else (feed.value / it) }
        // TODO: 2020/9/4 correct
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) BigDecimal(-1) else feed.value.divide(it, 5, RoundingMode.HALF_EVEN) }
        val price = SimplePrice(debt, coll).value
        when {
            debt.amount == 0L && coll.amount == 0L -> undefinedRatio
            debt.amount == 0L || coll.amount == 0L -> errorRatio
            price.isZero() || feed.value.isZero() -> undefinedRatio
            else -> coll.formattedValue.divide(debt.formattedValue.divide(feed.value, 6, RoundingMode.HALF_EVEN), 4, RoundingMode.HALF_EVEN)
        }
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) errorRatio else coll.formattedValue.divide(debt.formattedValue.divide(feed.value, 6, RoundingMode.HALF_EVEN), 4, RoundingMode.HALF_EVEN) }
    }

    val lastRatio = combineNonNull(debtAmount, collAmount, feedPrice) { debt, coll, feed ->
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) BigDecimal(-1) else (feed.value / it) }
        // TODO: 4/9/2021 correct
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) BigDecimal(-1) else feed.value.divide(it, 5, RoundingMode.HALF_EVEN) }
        val price = SimplePrice(debt, coll).value
        when {
            debt.amount == 0L && coll.amount == 0L -> undefinedRatio
            debt.amount == 0L || coll.amount == 0L -> errorRatio
            price.isZero() || feed.value.isZero() -> undefinedRatio
            else -> coll.formattedValue.divide(debt.formattedValue.divide(feed.value, 6, RoundingMode.HALF_EVEN), 4, RoundingMode.HALF_EVEN)
        }
//        SimplePrice(debt, coll).value.let { if (it.compareTo(BigDecimal.ZERO) == 0) errorRatio else coll.formattedValue.divide(debt.formattedValue.divide(feed.value, 6, RoundingMode.HALF_EVEN), 4, RoundingMode.HALF_EVEN) }
    }

    private val ratioThreshold = BigDecimal(64)

    private val interceptor = combineNonNull(mcr, currentRatio.distinctUntilChangedBy { it > ratioThreshold }) { mcr, currentRatio ->
        if (currentRatio > ratioThreshold) createInterceptor(mcr.toDouble(), 64.0 * RATIO_SCALE) else createInterceptor(mcr.toDouble(), 64.0 * RATIO_SCALE)
    }

    private val interceptorReversed = combineNonNull(mcr, currentRatio.distinctUntilChangedBy { it > ratioThreshold }) { mcr, currentRatio ->
        if (currentRatio > ratioThreshold) createInterceptorReversed(mcr.toDouble(), 64.0 * RATIO_SCALE) else createInterceptorReversed(mcr.toDouble(), 64.0 * RATIO_SCALE)
    }.sources(interceptor)

    val progress = combineNonNull(currentRatio, isSliderOnTouch, interceptorReversed) { ratio, touch, interceptor ->
        if (!touch) interceptor.invoke(ratio.toDouble()) else null
    }.filterNotNull()

    val callState = combineNonNull(mcr, currentRatio) { mcr, ratio ->
        when ((ratio * ratioScale).toInt() - mcr) {
            in Int.MIN_VALUE..0 -> State.ERROR
            in 0..500 -> State.WARNING
            else -> State.NORMAL
        }
    }


    val callPositionPercentage = combineNonNull(mcr, currentRatio) { mcr, ratio ->
        if (ratio == undefinedRatio) Float.NaN else (ratio.toFloat() * RATIO_SCALE - mcr) / RATIO_WARNING
    }

    val lastCallPositionPercentage = combineNonNull(mcr, lastRatio) { mcr, ratio ->
        if (ratio == undefinedRatio) Float.NaN else (ratio.toFloat() * RATIO_SCALE - mcr) / RATIO_WARNING
    }

    private val tcrInterceptor = mcr.map { createInterceptor(it.toDouble(), UShort.MAX_VALUE.toDouble()) }

    private val tcrInterceptorReversed = mcr.map { createInterceptorReversed(it.toDouble(), UShort.MAX_VALUE.toDouble()) }.sources(tcrInterceptor)

//    val currentTargetRatioProgress = NonNullMutableLiveData(0)

    //    val currentTargetRatio = currentTargetRatioProgress.map { BigDecimal(it).setScale(3).divide(BigDecimal(RATIO_SCALE)) }
    private val currentTargetRatioInternal = NonNullMutableLiveData(errorRatio)

    val currentTargetRatio = combineNonNull(currentTargetRatioInternal, mcr) { ratio, mcr ->
        if (ratio.multiply(ratioScale).toDouble() <= mcr * 0.7) undefinedRatio else if (ratio.multiply(ratioScale).toDouble() <= mcr) mcr.toBigDecimal().divide(ratioScale, 4, RoundingMode.HALF_EVEN) else ratio
    }

    private val tcr = callOrder.filterNotNull().map { if (it.tcr.toInt() == 0) UNDEFINED_RATIO else it.tcr.toInt() }

    val tcrProgressInternal = combineFirst(tcrInterceptorReversed, tcr) { interceptor, tcr ->
        if (interceptor == null || tcr == null) 0.0 else interceptor.invoke(1.0 * tcr / RATIO_SCALE)
    }

    val tcrProgress = combineNonNull(currentTargetRatio, isTcrSliderOnTouch) { ratio, touch ->
        if (ratio == undefinedRatio && !touch) 0.0 else null
    }.filterNotNull()


    fun changeDebtField(text: String) {
        if (!isSliderOnTouch.value) debtFieldDecimal.value = text.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    fun changeCollField(text: String) {
        if (!isSliderOnTouch.value) collFieldDecimal.value = text.toBigDecimalOrNull() ?: BigDecimal.ZERO
    }

    fun switchColl(checked: Boolean) {
        isCollateralLocked.value = checked
    }

    fun switchDebt(checked: Boolean) {
        isCollateralLocked.value = !checked
    }

    fun switchRatio(scaled: Double) {
        interceptor.value?.takeIf { isSliderOnTouch.value }?.invoke(scaled)?.let {
            val ratio = it.toBigDecimal()
            val feed = feedPrice.value?.value
            val coll = collFieldDecimal.value
            val debt = debtFieldDecimal.value
            val collPre = collAssetInternal.value.precision
            val debtPre = debtAssetInternal.value?.precision ?: 0
            if (feed != null && coll != null && debt != null) {
                if (isCollateralLocked.value) {
                    debtFieldDecimal.value = if (ratio.isNotZero()) (feed.setScale(debtPre + collPre, RoundingMode.HALF_EVEN) * coll).divide(ratio, debtPre, RoundingMode.HALF_EVEN) else BigDecimal.ZERO
                } else {
                    collFieldDecimal.value = if (feed.isNotZero()) (debt.setScale(debtPre + collPre, RoundingMode.HALF_EVEN) * ratio).divide(feed, collPre, RoundingMode.HALF_EVEN) else BigDecimal.ZERO
                }
            }
        }
    }

    fun switchTargetRatio(scaled: Double) {
        tcrInterceptor.value?.invoke(scaled)?.let { currentTargetRatioInternal.value = BigDecimal.valueOf(it).setScale(3, RoundingMode.HALF_EVEN) }
    }

    fun switchSlider(touched: Boolean) {
        isSliderOnTouch.value = touched
    }

    fun switchTargetRatioSlider(touched: Boolean) {
        isTcrSliderOnTouch.value = touched
    }

    private fun createInterceptor(mcr: Double, max: Double) = { base: Double ->
        val mcp = mcr / max
        val rfp = (RATIO_DIVIDER_P2 * RATIO_FACTOR + 1 - RATIO_DIVIDER_P1 * RATIO_FACTOR) * mcp
        val quote = base.coerceIn(0.0..1.0)
        when (base.coerceIn(0.0..1.0)) {
            RATIO_DIVIDER_P0 -> 0.0
            RATIO_DIVIDER_P3 -> 1.0
            in RATIO_DIVIDER_P0..RATIO_DIVIDER_P1 -> quote.pow(1 / log(RATIO_DIVIDER_P1, mcp))
            in RATIO_DIVIDER_P1..RATIO_DIVIDER_P2 -> (quote * RATIO_FACTOR + 1 - RATIO_FACTOR * RATIO_DIVIDER_P1) * mcp
            else -> quote.pow(log(rfp, RATIO_DIVIDER_P2))
        } * max / 1000
    }

    private fun createInterceptorReversed(mcr: Double, max: Double) = { base: Double ->
        val mcp = mcr / max
        val rfp = (RATIO_DIVIDER_P2 * RATIO_FACTOR + 1 - RATIO_DIVIDER_P1 * RATIO_FACTOR) * mcp
        val quote = (base * 1000 / max).coerceIn(0.0..1.0)
        when (quote) {
            RATIO_DIVIDER_P0 -> 0.0
            RATIO_DIVIDER_P3 -> 1.0
            in RATIO_DIVIDER_P0..mcp -> quote.pow(log(RATIO_DIVIDER_P1, mcp))
            in mcp..rfp -> quote / mcp / RATIO_FACTOR + RATIO_DIVIDER_P1 - 1 / RATIO_FACTOR
            else -> quote.pow(1 / log(rfp, RATIO_DIVIDER_P2))
        }
    }

    private val isDebtBalanceSufficient = debtLeft.map { it.amount >= 0L }
    private val isCollBalanceSufficient = collLeft.map { it.amount >= 0L }
    private val isCollateralRatioAboveMaintained = combineNonNull(callPositionPercentage, lastCallPositionPercentage, currentRatio).map { (current, last, ratio) -> current >= 0 || (current > last) || ratio == undefinedRatio }

    private val isDebtModified = combineNonNull(debtAmountChanged, debtAmount) { changed, original -> changed.asset.uid == original.asset.uid && changed.amount != original.amount }
    private val isCollModified = combineNonNull(collAmountChanged, collAmount) { changed, original -> changed.asset.uid == original.asset.uid && changed.amount != original.amount }

    private val isTargetRatioModified = combineNonNull(currentTargetRatio, tcr) { changed, original ->
        changed.multiply(ratioScale).toInt() != original
    }

    val isModified = combineBooleanAny(isDebtModified, isCollModified)
    val isPositionUpdatable = combineBooleanAll(isDebtBalanceSufficient, isCollBalanceSufficient, isCollateralRatioAboveMaintained, isModified)
    fun isModified() = isPositionUpdatable.value.orFalse()

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val feeState = transactionBuilder.switchMap { it.feeState }
    val fee = transactionBuilder.switchMap { it.fee }.mapSuspend { AssetRepository.getAssetAmountDetail(it) }
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as CallOrderUpdateOperation? }.filterNotNull()

    fun buildTransaction(): TransactionBuilder = buildTransaction {
        addOperation {
            val account = account.value!!
            val deltaColl = collAmountChanged.value!! - collAmount.value!!
            val deltaDebt = debtAmountChanged.value!! - debtAmount.value!!
            val tcr = currentTargetRatio.value?.takeIf { it > BigDecimal.ZERO }?.multiply(ratioScale)?.toLong()?.toUShort()
            CallOrderUpdateOperation(account, deltaColl, deltaDebt, tcr)
        }
//        addKeys(AuthorityService.ownerRequiredAuths)
        transactionBuilder.value = this
        checkFees()
    }
    
    override fun onActivityIntent(intent: Intent?) {
        intent ?: return
        logcat("onActivityIntent", intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> return
            null -> {
                val accountInstance = intent.getJson(IntentParameters.Account.KEY_ACCOUNT, ChainConfig.EMPTY_INSTANCE)
                if (accountInstance != ChainConfig.EMPTY_INSTANCE) {
                    accountUid.value = accountInstance
                    userUid.value = accountInstance
                }
                val assetInstance = intent.getJson(IntentParameters.Asset.KEY_ASSET, ChainConfig.EMPTY_INSTANCE)
                if (assetInstance != ChainConfig.EMPTY_INSTANCE) {
                    viewModelScope.launch {
                        debtAssetInternal.value = AssetRepository.getAssetOrDefault(assetInstance)
                    }
                }
            }
            else -> return
        }

    }

}

