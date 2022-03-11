package com.bitshares.oases.ui.account.margin

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.formatAssetBalance
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.formatGrapheneRatio
import com.bitshares.oases.R
import com.bitshares.oases.extensions.text.appendAssetName
import com.bitshares.oases.extensions.viewbinder.bindPrice
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAssetPicker
import com.bitshares.oases.ui.transaction.bindTransaction
import modulon.component.buttonStyle
import modulon.component.isButtonEnabled
import modulon.dialog.section
import modulon.extensions.charset.toHexString
import modulon.extensions.compat.secureWindow
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.lifecycle.parentViewModels
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.distinctUntilChangedBy
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.parentViewGroup
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.layout.actionbar.subtitle
import modulon.layout.actionbar.title
import modulon.layout.recycler.section
import modulon.union.Union
import modulon.widget.doOnProgressChanged
import modulon.widget.doOnTrackingTouchChanged
import java.math.BigDecimal
import java.util.*

class CollateralFragment : ContainerFragment() {

    private val viewModel: CollateralViewModel by activityViewModels()

    override fun onViewCreated(savedInstanceState: Bundle?) {
        ByteArray(1).toHexString()
        secureWindow()
        setupAction {
            title(context.getString(R.string.collateral_title))
            networkStateMenu()
            walletStateMenu()
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupRecycler {
            val errorColor = context.getColor(R.color.component_error)
            val warningColor = context.getColor(R.color.component_warning)
            val inactiveColor = context.getColor(R.color.component_inactive)
            val primaryTextColor = context.getColor(R.color.text_primary)
            val secondaryTextColor = context.getColor(R.color.cell_text_secondary)
//            addVerticalCell {
//                addTopCorner()
//                addBaseTitleTextCell {
//                    title = context.getString(R.string.collateral_margin_asset)
//                    viewModel.debtAssetInternal.observe(viewLifecycleOwner) {
//                        if (it != null) {
//                            bindAsset(it)
//                            subtext = ""
//                        }
//                    }
//                }
//                addBottomCorner()
//                doOnClick {
//                    startAssetIdPicker { viewModel.debtAssetInternal.value = it }
//                }
//                isVisible = false
//                viewModel.debtAssetInternal.observe(viewLifecycleOwner) {
//                    isVisible = it != null
//                }
//            }

            section {
                header = context.getString(R.string.collateral_margin_title)
                viewModel.debtAssetInternal.observe(viewLifecycleOwner) {
                    header = context.getString(R.string.collateral_asset_margin, it.symbol)
                }
                cell {
                    isVisible = false
                    title = context.getString(R.string.collateral_margin_asset)
        //                    subtitleView.typeface = typefaceMonoRegular
                    viewModel.debtAssetInternal.observe(viewLifecycleOwner) {
                        subtitle = if (!it.isExist) context.getString(R.string.invalid_number) else buildContextSpannedString { appendAssetName(it, true) }
                        isVisible = true
                    }
        //                doOnClick {
        //                    startAssetIdPicker { viewModel.debtAssetInternal.value = it }
        //                }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.collateral_feed_price)
                    viewModel.feedPrice.observe(viewLifecycleOwner) {
                        bindPrice(it)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    textView.parentViewGroup.layoutTransition = null
                    title = context.getString(R.string.collateral_call_price)
        //                    fieldtextView.doAfterTextChanged { viewModel.changeCollField(it.toStringOrEmpty()) }
                    viewModel.callPrice.observe(viewLifecycleOwner) {
                        bindPrice(it)
                    }
                    viewModel.callPositionPercentage.observe(viewLifecycleOwner) {
                        subtitleView.textColor = when {
                            it.isNaN() -> primaryTextColor
                            it > 1f -> primaryTextColor
                            it < 0f -> errorColor
                            else -> ColorUtils.blendARGB(warningColor, primaryTextColor, it)
                        }
                    }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.collateral_select_asset)
        //                subtext = "Select smartcoin asset for collateral"
                    viewModel.debtAssetInternal.observe(viewLifecycleOwner) {
                        isVisible = it == null
                    }
                    doOnClick {
                        startAssetPicker {
                            viewModel.debtAssetInternal.value = it
                        }
                    }
                }
            }

            section {
                header = "Adjust"
                // TODO: 2020/9/11 pay max debt
                cell {
                    custom {
                        field {
                            inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                            transformationMethod = TABULAR_TRANSFORMATION_METHOD
                            doAfterTextChanged { viewModel.changeDebtField(it.toStringOrEmpty()) }
                            viewModel.debtField.observe(viewLifecycleOwner) {
                                fieldtext = formatAssetBigDecimal(it).toPlainString()
                            }
                            viewModel.debtAmount.observe(viewLifecycleOwner) {
                                fieldtext = formatAssetBigDecimal(it).toPlainString()
                            }
                        }
                        text {
                            viewModel.debtAssetInternal.observe(viewLifecycleOwner) { text = it.symbol }
                        }
                        toggle {
                            viewModel.isCollateralLocked.observe(viewLifecycleOwner) { setChecked(!it, true) }
                            doOnClick { viewModel.switchDebt(!isChecked) }
                        }
                    }
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.collateral_debt)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.debtLeft.observe(viewLifecycleOwner) {
                        subtitle = "${formatAssetBalance(it)} Available"
                        subtitleView.textColor = if (it.amount < 0) errorColor else secondaryTextColor
                    }
                }
                cell {
                    custom {
                        field {
                            inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                            transformationMethod = TABULAR_TRANSFORMATION_METHOD
                            doAfterTextChanged { viewModel.changeCollField(it.toStringOrEmpty()) }
                            viewModel.collField.observe(viewLifecycleOwner) {
                                fieldtext = formatAssetBigDecimal(it).toPlainString()
                            }
                            viewModel.collAmount.observe(viewLifecycleOwner) {
                                fieldtext = formatAssetBigDecimal(it).toPlainString()
                            }
                        }
                        text {
                            viewModel.collAssetInternal.observe(viewLifecycleOwner) { text = it.symbol }
                        }
                        toggle {
                            viewModel.isCollateralLocked.observe(viewLifecycleOwner) { setChecked(!it, true) }
                            doOnClick { viewModel.switchColl(!isChecked) }
                        }
                    }
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.collateral_collateral)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.collLeft.observe(viewLifecycleOwner) {
                        subtitle = "${formatAssetBalance(it)} Available"
                        subtitleView.textColor = if (it.amount < 0) errorColor else secondaryTextColor
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.collateral_collateral_ratio)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    textView.isSingleLine = false
                    viewModel.currentRatio.observe(viewLifecycleOwner) {
                        subtitle = when (it) {
                            BigDecimal(-1) -> "NaN"
                            BigDecimal(-2) -> ""
                            else -> it.toPlainString()
                        }
                    }
                    val livedata = combineNonNull(
                        viewModel.callPositionPercentage,
                        viewModel.lastCallPositionPercentage,
                        viewModel.lastRatio,
                        viewModel.debtAssetDetailed.distinctUntilChangedBy { it.symbol to it.bitassetData.currentFeed.maintenanceCollateralRatio },
                        viewModel.collAssetInternal.distinctUntilChangedBy { it.symbol }
                    )
                    slider {
                        max = 10000
                        doOnProgressChanged { progress: Int, fromUser: Boolean ->
                            if (fromUser) viewModel.switchRatio(1.0 * progress / max)
                        }
                        doOnTrackingTouchChanged {
                            viewModel.switchSlider(it)
                        }
                        viewModel.progress.observe(viewLifecycleOwner) {
                            setProgress((max * it).toInt().coerceIn(0..max), true)
                        }
                        progressActiveColor = inactiveColor
                        thumbColor = inactiveColor
                        livedata.observe(viewLifecycleOwner) { (it, last, lastRatio, debt, coll) ->
                            val component = when {
                                it.isNaN() -> inactiveColor
                                it > 1f -> inactiveColor
                                it < 0f -> errorColor
                                else -> ColorUtils.blendARGB(warningColor, inactiveColor, it)
                            }
                            progressActiveColor = component
                            thumbColor = component
                        }
                    }
                    livedata.observe(viewLifecycleOwner) { (it, last, lastRatio, debt, coll) ->
                        val title = when {
                            it.isNaN() -> secondaryTextColor
                            it > 1f -> secondaryTextColor
                            it < 0f -> errorColor
                            else -> ColorUtils.blendARGB(warningColor, secondaryTextColor, it)
                        }
                        val hint = when {
                            it.isNaN() -> ""
                            it > 1f -> ""
                            it < 0f && last < 0f && it <= last -> context.getString(R.string.collateral_collateral_ratio_below_original_hint, lastRatio.toPlainString())
                            it < 0f && last < 0f && it > last -> context.getString(R.string.collateral_collateral_ratio_already_below_hint, formatGrapheneRatio(debt.bitassetData.currentFeed.maintenanceCollateralRatio))
                            it < 0f -> context.getString(R.string.collateral_collateral_ratio_below_hint, formatGrapheneRatio(debt.bitassetData.currentFeed.maintenanceCollateralRatio))
                            else -> context.getString(R.string.collateral_collateral_ratio_close_hint, formatGrapheneRatio(debt.bitassetData.currentFeed.maintenanceCollateralRatio), coll.symbol)
                        }
                        subtitleView.textColor = title
                        textView.textColor = title
                        text = hint
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.collateral_target_collateral_ratio)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    textView.isSingleLine = false
                    viewModel.currentTargetRatio.observe(viewLifecycleOwner) {
                        subtitle = when (it) {
                            BigDecimal(-1) -> context.getString(R.string.collateral_tcr_invalid)
                            BigDecimal(-2) -> context.getString(R.string.collateral_tcr_disabled)
                            else -> it.toPlainString()
                        }
                    }
                    slider {
                        max = 10000
                        doOnProgressChanged { progress: Int, fromUser: Boolean ->
                            viewModel.switchTargetRatio(1.0 * progress / max)
                        }
                        doOnTrackingTouchChanged {
                            viewModel.switchTargetRatioSlider(it)
                        }
                        viewModel.tcrProgress.observe(viewLifecycleOwner) {
                            setProgress((max * it).toInt().coerceIn(0..max), true)
                        }
                        viewModel.tcrProgressInternal.observe(viewLifecycleOwner) {
                            setProgress((max * it).toInt().coerceIn(0..max), true)
                        }
                        progressActiveColor = inactiveColor
                        thumbColor = inactiveColor
                    }
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = "Auto Adjust"
                    doOnClick { showUpdatePositionDialog() }
                    isButtonEnabled = false
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.collateral_update_position)
                    doOnClick { showUpdatePositionDialog() }
                    isButtonEnabled = false
                    viewModel.isPositionUpdatable.observe(viewLifecycleOwner) { isButtonEnabled = it }
                }
            }
            hint {
                combineNonNull(
                    viewModel.debtAssetDetailed.distinctUntilChangedBy { it.symbol to it.bitassetData.currentFeed.maintenanceCollateralRatio },
                    viewModel.collAssetInternal.distinctUntilChangedBy { it.symbol },
                ).observe(viewLifecycleOwner) { (debt, coll) ->
                    text = context.getString(R.string.collateral_update_position_hint, debt.symbol, coll.symbol, formatGrapheneRatio(debt.bitassetData.currentFeed.maintenanceCollateralRatio))
                }
            }
            logo()

        }
    }

}

fun Union.showUpdatePositionDialog() = showBottomDialog {
    val viewModel: CollateralViewModel by parentViewModels()
    val errorColor = context.getColor(R.color.component_error)
    val warningColor = context.getColor(R.color.component_warning)
    val inactiveColor = context.getColor(R.color.component_inactive)
    val primaryTextColor = context.getColor(R.color.text_primary)
    val secondaryTextColor = context.getColor(R.color.cell_text_secondary)

    bindTransaction(viewModel.buildTransaction(), viewModel)
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_call_order_update_debt_changes)
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            viewModel.operation.observe(viewLifecycleOwner) {
                subtitle = it.deltaDebt.toString()
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_call_order_update_collateral_changes)
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            viewModel.operation.observe(viewLifecycleOwner) {
                subtitle = it.deltaCollateral.toString()
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_call_order_call_price)
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            viewModel.callPrice.observe(viewLifecycleOwner) {
                bindPrice(it)
            }
            viewModel.callPositionPercentage.observe(viewLifecycleOwner) {
                subtitleView.textColor = when {
                    it.isNaN() -> subtitleView.textColor
                    it > 1f -> subtitleView.textColor
                    it < 0f -> errorColor
                    else -> ColorUtils.blendARGB(warningColor, subtitleView.textColor, it)
                }
            }
        }
        cell {
            updatePaddingVerticalV6()
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            title = context.getString(R.string.operation_call_order_collateral_ratio)
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            viewModel.currentRatio.observe(viewLifecycleOwner) {
                subtitle = when (it) {
                    BigDecimal(-1) -> "NaN"
                    BigDecimal(-2) -> "Off"
                    else -> it.toPlainString()
                }

            }
            viewModel.callPositionPercentage.observe(viewLifecycleOwner) {
                subtitleView.textColor = when {
                    it.isNaN() -> primaryTextColor
                    it > 1f -> primaryTextColor
                    it < 0f -> errorColor
                    else -> ColorUtils.blendARGB(warningColor, primaryTextColor, it)
                }
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.operation_call_order_update_target_collateral_ratio)
            subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
            viewModel.operation.observe(viewLifecycleOwner) {
                subtitle = if (it.targetCollateralRatio.isPresent) formatGrapheneRatio(it.targetCollateralRatio.get()) else context.getString(R.string.operation_call_order_update_tcr_disabled)
            }
            viewModel.currentRatio.observe(viewLifecycleOwner) {
                isVisible = it != BigDecimal(-2)
            }
        }
        feeCell(union, viewModel.transactionBuilder)
    }

}

