package com.bitshares.oases.ui.asset

import android.app.Application
import android.content.Intent
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.formatPercentage
import bitshareskit.extensions.formatTradePrice
import bitshareskit.extensions.logcat
import bitshareskit.extensions.symbolOrEmpty
import bitshareskit.models.AssetAmount
import bitshareskit.models.Price
import bitshareskit.models.toFormattedPrice
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.resolveAssetPath
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.Source
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.ui.base.BaseViewModel
import com.bitshares.oases.ui.base.getJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.max

open class AssetViewModel(application: Application) : BaseViewModel(application) {

    private val assetUid = NonNullMutableLiveData(ChainConfig.EMPTY_INSTANCE)
    private val coroutineContext = viewModelScope.coroutineContext + Dispatchers.IO

    val asset = assetUid.switchMap { AssetRepository.getAssetLiveFromDatabase(it) }.distinctUntilChanged()
    val assetNonNull = asset.filterNotNull()
    val assetSymbol = asset.map { it.symbolOrEmpty }.distinctUntilChanged()
    val issuer = assetNonNull.mapSuspend(coroutineContext) { AccountRepository.getAccountObject(it.issuer.uid) }.filterNotNull()
    val coreExchangeRate = assetNonNull.map(viewModelScope) { AssetRepository.getPriceDetail(it.coreExchangeRate) }.filterNotNull()
    val dynamicData = assetNonNull.map { it.dynamicData.uid }.switchMap { AssetRepository.getAssetDynamicDataLive(it) }.distinctUntilChanged().filterNotNull()

    // TODO: 14/9/2021 merge bitassetData
    val bitassetData = assetNonNull.map { it.bitassetData.uid }.switchMap { AssetRepository.getAssetBitassetDataLive(it) }.distinctUntilChanged().filterNotNull()
    private val bitassetDataNonNull = bitassetData.filterNotNull()
    val assetWithExtraData = combineFirst(asset, dynamicData, bitassetData) { asset, dynamic, bitasset ->
        asset?.apply {
            if (dynamic != null && dynamicData.uid == dynamic.uid) dynamicData = dynamic
            if (bitasset != null && bitassetData.uid == bitasset.uid) bitassetData = bitasset
        }
    }.filterNotNull()
    val assetType = assetWithExtraData.map { it.assetType }.distinctUntilChanged()
    val backingAsset = bitassetDataNonNull.map(viewModelScope) { AssetRepository.getAsset(it.shortBackingAsset.uid) }.distinctUntilChanged()

    // Force Settlement Offset Percent
    val bitassetForceSettlementDelaySec = bitassetDataNonNull.map { it.forceSettlementDelaySec }.distinctUntilChanged()

    // Force Settlement Offset Percent
    val bitassetForceSettlementOffsetPercent = bitassetDataNonNull.map { it.forceSettlementOffsetPercent }.distinctUntilChanged()

    // Maximum Force Settlement Volume Percent
    val bitassetMaximumForceSettlementVolumePercent = bitassetDataNonNull.map { it.maximumForceSettlementVolumePercent }.distinctUntilChanged()

    // Core Exchange Rate
    val bitassetCER = bitassetDataNonNull.map(viewModelScope) { AssetRepository.getPriceDetail(it.currentFeed.coreExchangeRate) }.distinctUntilChanged()

    // Feed Price
    val bitassetFP = bitassetDataNonNull.map(viewModelScope) { AssetRepository.getPriceDetail(it.currentFeed.settlementPrice) }.distinctUntilChanged()

    // Maintenance Collateral Ratio
    val bitassetMCR = bitassetDataNonNull.map { it.currentFeed.maintenanceCollateralRatio }.distinctUntilChanged()

    // Maximum Short Squeeze Ratio
    val bitassetMSSR = bitassetDataNonNull.map { it.currentFeed.maximumShortSqueezeRatio }.distinctUntilChanged()

    // Margin Call Fee Ratio
    val bitassetMCFR = bitassetDataNonNull.map { it.marginCallFeeRatio }.distinctUntilChanged()

    // Force Settle Fee Percent
    val bitassetFSFP = bitassetDataNonNull.map { it.forceSettleFeePercent }.distinctUntilChanged()

    // Initial Collateral Ratio
    val bitassetICR = bitassetDataNonNull.map { it.currentFeed.initialCollateralRatio }.distinctUntilChanged()

    // Force Settlement Price
    private val bitassetForceSettlementPriceNormal = combineNonNull(bitassetFP, bitassetForceSettlementOffsetPercent) { feed, offset ->
        feed.toFormattedPrice(BigDecimal(1.0 - 1.0 * offset / ChainConfig.Asset.GRAPHENE_100_PERCENT)) as Price
    }.distinctUntilChanged()
//    private val bitassetForceSettlementPriceNormal1 = combineNonNull(bitassetFP, bitassetMSSR) { feed, mssr ->
//        feed.toFormattedPrice(BigDecimal(1.0 - 1.0 * offset / ChainConfig.Asset.GRAPHENE_100_PERCENT )) as Price
//    }.distinctUntilChanged()

    private val bitassetForceSettlementPriceGlobalSettle = bitassetDataNonNull.map(viewModelScope) {
        AssetRepository.getPriceDetail(it.settlementPrice) as Price
    }.distinctUntilChanged()

    val bitassetSettledFund = bitassetDataNonNull.map { AssetAmount(it.settlementFund, Graphene.KEY_CORE_ASSET.value) }.distinctUntilChanged()
    val isGlobalSettle = bitassetSettledFund.map { it.amount > 0 }.distinctUntilChanged()
    val bitassetForceSettlementPrice = isGlobalSettle.switchMap { if (it) bitassetForceSettlementPriceGlobalSettle else bitassetForceSettlementPriceNormal }.distinctUntilChanged()

    val bitassetSettledFundCollateralRatio = combineNonNull(bitassetSettledFund, bitassetFP) { fund, feed ->
        formatTradePrice(fund, feed)
    }.distinctUntilChanged()

    // Maximum Short Squeeze Price
    val bitassetMSSP = combineNonNull(bitassetFP, bitassetMSSR) { feed, mssr ->
        feed.toFormattedPrice(BigDecimal(mssr / ChainConfig.Asset.GRAPHENE_10_PERCENT.toDouble()))
    }.distinctUntilChanged()

    // Margin Call Order Price
    val bitassetMCOP = combineNonNull(bitassetFP, bitassetMSSR, bitassetMCFR) { feed, mssr, mcfr ->
        feed.toFormattedPrice(BigDecimal(max(mssr - mcfr.toInt(), 1000) / ChainConfig.Asset.GRAPHENE_10_PERCENT.toDouble()))
    }.filterNotNull().distinctUntilChanged()

    // Maximum Force Settlement Volume
    val bitassetMFSV = combineNonNull(assetWithExtraData, bitassetMaximumForceSettlementVolumePercent) { asset, mfsvp ->
        AssetAmount(BigDecimal(asset.dynamicData.currentSupply * mfsvp / ChainConfig.Asset.GRAPHENE_100_PERCENT.toDouble()).setScale(0, RoundingMode.HALF_EVEN).longValueExact(), asset)
    }.distinctUntilChanged()

    // Bitasset Force Settled Volume
    val bitassetFSV = combineNonNull(asset, bitassetData) { asset, data ->
        AssetAmount(data.forceSettledVolume, asset)
    }.distinctUntilChanged()

    // Remaining Settle Volume Percent
    val bitassetRSVP = combineNonNull(bitassetMFSV, bitassetFSV) { mfsv, fsv ->
        formatPercentage(fsv.amount, mfsv.amount, 2)
    }.distinctUntilChanged()

    val bitassetFeeds = bitassetDataNonNull.map { it.feeds }.onEachChildParallel(viewModelScope) {
        provider = AccountRepository.getAccountObject(provider.uid) ?: provider
        AssetRepository.getPriceDetail(settlementPrice)
        AssetRepository.getPriceDetail(coreExchangeRate)
    }.map { it.sortedBy { it.settlementPrice.value } }

    val averageSettlementPrice = bitassetFeeds.map { it.sumByDouble { it.settlementPrice.value.toDouble() }.div(it.size) }

    fun setAssetUid(uid: Long) {
        assetUid.value = uid
        viewModelScope.launch(Dispatchers.IO) { AssetRepository.getAsset(uid, Source.CHAIN_ONLY) }
    }

    override fun onActivityIntent(intent: Intent?) {
        super.onActivityIntent(intent)
        intent ?: return
        logcat("onActivityIntent", intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> {
                val uri = intent.data?.normalizeScheme()
                if (uri != null) {
                    viewModelScope.launch {
                        val assetPath = uri.pathSegments.firstOrNull()
                        val asset = resolveAssetPath(assetPath)
                        withContext(Dispatchers.Main) { setAssetUid(asset.uid) }
                    }
                }
            }
            null -> intent.getJson(IntentParameters.Asset.KEY_UID, ChainConfig.EMPTY_INSTANCE).let { setAssetUid(it) }
            else -> return
        }

    }


}