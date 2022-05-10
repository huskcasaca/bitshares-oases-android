package com.bitshares.oases.ui.asset.browser

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.formatAssetBalance
import bitshareskit.extensions.formatGrapheneRatio
import bitshareskit.extensions.formatPercentage
import bitshareskit.extensions.formatRatio
import com.bitshares.oases.R
import com.bitshares.oases.extensions.viewbinder.bindPrice
import com.bitshares.oases.ui.asset.AssetViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.formatTimeStringFromSec
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section

class AssetBrowserFragment_Bitasset : ContainerFragment() {

    private val viewModel: AssetViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            section {
                header = context.getString(R.string.asset_price_feed_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_feed_price)
                    viewModel.bitassetFP.observe(viewLifecycleOwner) {
                        if (it.isValid) bindPrice(it) else subtitle = context.getString(R.string.asset_price_feed_invalid_feed_price)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_feed_lifetime)
                    viewModel.bitassetData.observe(viewLifecycleOwner) {
                        subtitle = formatTimeStringFromSec(it.feedLifetimeSec)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_minimum_feeds)
                    viewModel.bitassetData.observe(viewLifecycleOwner) {
                        subtitle = it.minimumFeeds.toString()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_maintenance_collateral_ratio)
                    viewModel.bitassetMCR.observe(viewLifecycleOwner) {
                        subtitle = formatGrapheneRatio(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_maximum_short_squeeze_ratio)
                    viewModel.bitassetMSSR.observe(viewLifecycleOwner) {
                        subtitle = formatGrapheneRatio(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_maximum_short_squeeze_price)
                    isVisible = false
                    viewModel.bitassetMSSP.observe(viewLifecycleOwner) {
                        isVisible = it.isValid
                        bindPrice(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_price_feed_margin_call_order_price)
                    isVisible = false
                    viewModel.bitassetMCOP.observe(viewLifecycleOwner) {
                        isVisible = it.isValid
                        bindPrice(it)
                    }
                }
            }
            section {
                header = context.getString(R.string.asset_settlement_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_price)
                    isVisible = false
                    viewModel.bitassetForceSettlementPrice.observe(viewLifecycleOwner) {
                        isVisible = it.isValid
                        bindPrice(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_delay)
                    viewModel.bitassetForceSettlementDelaySec.observe(viewLifecycleOwner) {
                        subtitle = formatTimeStringFromSec(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_settlement_funds)
                    viewModel.bitassetSettledFund.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it)
                    }
                    isVisible = false
                    viewModel.isGlobalSettle.observe(viewLifecycleOwner) {
                        isVisible = it
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_settlement_funds_collateral_ratio)
                    combineNonNull(viewModel.bitassetSettledFundCollateralRatio, viewModel.dynamicData).observe(viewLifecycleOwner) { (amount, data) ->
                        subtitle = formatRatio(amount.amount, data.currentSupply, 8)
                    }
                    isVisible = false
                    viewModel.isGlobalSettle.observe(viewLifecycleOwner) {
                        isVisible = it
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_volume_current)
                    viewModel.bitassetFSV.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it)
                    }
                    viewModel.isGlobalSettle.observe(viewLifecycleOwner) {
                        isVisible = !it
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_volume_maximum)
                    viewModel.bitassetMFSV.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it)
                    }
                    viewModel.isGlobalSettle.observe(viewLifecycleOwner) {
                        isVisible = !it
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_settlement_remaining_percent)
                    combineNonNull(viewModel.bitassetMFSV, viewModel.bitassetFSV).observe(viewLifecycleOwner) { (max, current) ->
                        subtitle = formatPercentage(max.amount - current.amount, max.amount, 2)
                    }
                }
            }
        }
    }

}
