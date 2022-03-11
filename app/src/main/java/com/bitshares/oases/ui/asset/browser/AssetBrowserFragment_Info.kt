package com.bitshares.oases.ui.asset.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.objects.AssetObjectType
import com.bitshares.oases.R
import com.bitshares.oases.chain.Clipboard.LABEL_ACCOUNT_NAME
import com.bitshares.oases.chain.Clipboard.LABEL_ASSET_NAME
import com.bitshares.oases.chain.Clipboard.LABEL_GRAPHENE_ID
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindPrice
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.extensions.viewbinder.setupTag
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.ui.asset.AssetViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.compat.setClipboardToast
import modulon.extensions.livedata.observeNonNull
import modulon.extensions.text.appendSimpleColoredSpan
import modulon.extensions.text.appendSimpleSpan
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.layout.flow.FlowLayout
import modulon.layout.recycler.section
import modulon.widget.PlainTextView
import java.util.*


class AssetBrowserFragment_Info : ContainerFragment() {

    private val viewModel: AssetViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler {
            section {
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_symbol)
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        subtitle = it.symbol.toUpperCase(Locale.ROOT)
                        doOnLongClick { setClipboardToast(LABEL_ASSET_NAME, it.symbol) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_graphene_id)
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        subtitle = it.id
                        doOnLongClick { setClipboardToast(LABEL_GRAPHENE_ID, it.id) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_issuer)
                    viewModel.issuer.observe(viewLifecycleOwner) {
                        subtitle = createAccountSpan(it)
                        doOnClick { startAccountBrowser(it.uid) }
                        doOnLongClick { setClipboardToast(LABEL_ACCOUNT_NAME, it.name) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_asset_type)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = when (it.assetType) {
                            AssetObjectType.UNDEFINED -> context.getString(R.string.asset_type_undefined)
                            AssetObjectType.CORE -> context.getString(R.string.asset_type_core)
                            AssetObjectType.UIA -> context.getString(R.string.asset_type_user)
                            AssetObjectType.MPA -> context.getString(R.string.asset_type_market)
                            AssetObjectType.PREDICTION -> context.getString(R.string.asset_type_prediction)
                        }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_precision)
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBigDecimal(1, it.precision).formatNumber()
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_backing_asset)
                    isVisible = false
                    viewModel.backingAsset.observeNonNull(viewLifecycleOwner) {
                        isVisible = true
                        subtitle = it.symbol
                        doOnClick { startAssetBrowser(it.uid) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_description)
                    isVisible = false
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        isVisible = it.descriptionMain.isNotBlank()
                        subtitle = it.descriptionMain
                    }
                }
            }
            section {
                header = context.getString(R.string.asset_supply_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_supply_current_supply)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.dynamicData.currentSupply, it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_supply_stealth_supply)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.dynamicData.confidentialSupply, it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_supply_max_supply)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.maxSupply, it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_supply_current_supply_rate)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatPercentage(1.0 * it.dynamicData.currentSupply / it.maxSupply, 6)
                    }
                }
                isVisible = false
                viewModel.assetWithExtraData.observe(viewLifecycleOwner) { isVisible = true }
            }
            section {
                header = context.getString(R.string.asset_permission_title)
                cell {
                    title = context.getString(R.string.asset_permission_title)
                    customView = create<FlowLayout> {
                        viewModel.assetNonNull.observe(viewLifecycleOwner) {
                            val stringRes = listOf(
                                R.string.asset_permission_charge_market_fee,
                                R.string.asset_permission_white_list,
                                R.string.asset_permission_override_authority,
                                R.string.asset_permission_transfer_restricted,
                                R.string.asset_permission_disable_force_settle,
                                R.string.asset_permission_global_settle,
                                R.string.asset_permission_disable_confidential,
                                R.string.asset_permission_witness_fed_asset,
                                R.string.asset_permission_committee_fed_asset
                            )
                            val flags = listOf(
                                ChainConfig.Asset.PermissionFlags.CHARGE_MARKET_FEE,
                                ChainConfig.Asset.PermissionFlags.WHITE_LIST,
                                ChainConfig.Asset.PermissionFlags.OVERRIDE_AUTHORITY,
                                ChainConfig.Asset.PermissionFlags.TRANSFER_RESTRICTED,
                                ChainConfig.Asset.PermissionFlags.DISABLE_FORCE_SETTLE,
                                ChainConfig.Asset.PermissionFlags.GLOBAL_SETTLE,
                                ChainConfig.Asset.PermissionFlags.DISABLE_CONFIDENTIAL,
                                ChainConfig.Asset.PermissionFlags.WITNESS_FED_ASSET,
                                ChainConfig.Asset.PermissionFlags.COMMITTEE_FED_ASSET
                            )
                            removeAllViews()
                            flags.forEachIndexed { index, flag ->
                                if (it.issuerPermissions.toBitMaskBoolean(flag)) {
                                    view<PlainTextView> {
                                        setupTag()
                                        text = buildContextSpannedString {
                                            if (it.flags.toBitMaskBoolean(flag)) {
                                                appendSimpleColoredSpan(context.getString(stringRes[index]).toUpperCase(Locale.ROOT), context.getColor(R.color.tag_component))
                                            } else {
                                                appendSimpleSpan(context.getString(stringRes[index]).toUpperCase(Locale.ROOT))
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
                viewModel.assetNonNull.observe(viewLifecycleOwner) {
                    isVisible = it.issuerPermissions != 0
                }
            }
            section {
                header = context.getString(R.string.asset_market_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_market_fee_percent)
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        subtitle = formatGraphenePercentage(it.marketFeePercent, 2)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_market_fee_referral_reward_percent)
                    isVisible = false
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        isVisible = it.isCore()
                        subtitle = formatGraphenePercentage(it.marketRewardPercent, 2)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_max_market_fee)
                    viewModel.assetNonNull.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.maxMarketFee, it)
                    }
                }
            }
            section {
                header = context.getString(R.string.asset_fee_pool_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_core_exchange_rate)
                    isVisible = false
                    viewModel.coreExchangeRate.observe(viewLifecycleOwner) {
                        isVisible = true
                        it.isInverted = it.base.asset.uid == ChainConfig.GLOBAL_INSTANCE
                        bindPrice(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_pool_balance)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.dynamicData.feePool, Graphene.KEY_CORE_ASSET.value)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.asset_unclaimed_owner_income)
                    viewModel.assetWithExtraData.observe(viewLifecycleOwner) {
                        subtitle = formatAssetBalance(it.dynamicData.accumulatedFees, it)
                    }
                }
            }
            hint {
                viewModel.coreExchangeRate.observe(viewLifecycleOwner) {
                    if (it.isValid && it.base.asset.uid != it.quote.asset.uid) text = context.getString(R.string.asset_fee_pool_hint, if (it.base.asset.isCore()) it.base.asset.symbol else it.quote.asset.symbol, if (!it.base.asset.isCore()) it.base.asset.symbol else it.quote.asset.symbol)
                }
            }
            logo()
        }
    }

}
