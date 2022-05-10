package com.bitshares.oases.ui.settings
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import bitshareskit.extensions.formatAssetBigDecimal
import bitshareskit.extensions.symbolOrId
import com.bitshares.oases.R
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.preference.DarkMode
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import modulon.component.cell.toggleEnd
import modulon.dialog.button
import modulon.dialog.section
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.lifecycle.parentViewModels
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.union.Union
import modulon.widget.RadioView

fun Union.showClearBlockchainCachesDialog() = showBottomDialog {
    val viewModel: SettingsViewModel by parentViewModels()
    title = context.getString(R.string.storage_settings_clear_blockchain_caches_title)
    message = context.getString(R.string.storage_settings_clear_blockchain_caches_message)
    isCancelableByButtons = true
    button {
        text = context.getString(R.string.button_clear)
        textColor = context.getColor(R.color.component_error)
        doOnClick { viewModel.clearBlockchainDatabase() }
    }
    button { text = context.getString(R.string.button_cancel) }
}

fun Union.showDarkModeSelectDialog() = showBottomDialog {
    title = context.getString(R.string.appearance_settings_dark_mode_title)
    isCancelableByButtons = true
    section {
        context.resources.getStringArray(R.array.settings_dark_mode_entries).forEachIndexed { index, name ->
            cell {
                customViewStart = create<RadioView> {
                    setColors(context.getColor(R.color.component_disabled), context.getColor(R.color.component))
                    layoutWidth = context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                    layoutHeight = context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                    layoutGravityFrame = Gravity.START or Gravity.CENTER_VERTICAL
                    globalPreferenceManager.DARK_MODE.observe(viewLifecycleOwner) {
                        setChecked(index == it.ordinal, true)
                    }
                }
                text = name
                doOnClick {
                    globalPreferenceManager.DARK_MODE.value = DarkMode.values()[index]
                    dismissNow()
                }
            }
        }
    }
    button { text = context.getString(R.string.button_cancel) }
}

fun Union.showFeeReservedDialog() = showBottomDialog {
    val viewModel: SettingsViewModel by viewModels()
    title = context.getString(R.string.appearance_settings_fee_reserved_title)
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.appearance_settings_fee_reserved_amount)
            customHorizontal {
                field {
                    inputType = InputTypeExtended.TYPE_NUMBER_DECIMAL
                    transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    doAfterTextChanged { viewModel.feeReservedFieldText = it.toStringOrEmpty() }
                    viewModel.isFeeReservedFieldError.observe(viewLifecycleOwner) { isError = it }
                    viewModel.feeReserved.observe(viewLifecycleOwner) {
                        fieldtext = formatAssetBigDecimal(it).toPlainString()
                        viewModel.feeReservedFieldText = fieldtext.toStringOrEmpty()
                    }
                }
                text {
                    Graphene.KEY_CORE_ASSET.observe(viewLifecycleOwner) { text = it.symbol }
                }
            }
            viewModel.isAutoReserveChecked.observe(viewLifecycleOwner) { isVisible = !it }
        }
        cell {
            title = context.getString(R.string.appearance_settings_fee_reserve_auto)
            toggleEnd {
                Settings.KEY_AUTO_RESERVE_FEE.observe(viewLifecycleOwner) { setChecked(it, false) }
            }
            doOnClick {
                toggleEnd {
                    viewModel.isAutoReserveChecked.value = !isChecked
                    setChecked(!isChecked, true)
                }
            }
        }
    }
    button {
        text = context.getString(R.string.button_confirm)
        doOnClick { if (viewModel.checkDismiss()) dismiss() }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismiss() }
    }
    showSoftKeyboard()
}
fun Union.showPriceUnitDialog() = showBottomDialog {
    val viewModel: SettingsViewModel by parentViewModels()
    title = context.getString(R.string.appearance_settings_price_unit_title)
    message = "Loading..."
    section {
        viewModel.priceUnits.observe(viewLifecycleOwner) {
            message = EMPTY_SPACE
            removeAllViews()
            it.forEach {
                cell {
                    title = it.symbolOrId
                    doOnClick { Settings.KEY_BALANCE_UNIT.value = it.symbol; dismiss() }
                }
            }
        }
    }
}

