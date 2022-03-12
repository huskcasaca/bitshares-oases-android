package com.bitshares.oases.ui.settings.appearance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.formatAssetBalance
import bitshareskit.models.AssetAmount
import com.bitshares.oases.R
import com.bitshares.oases.extensions.text.createAssetName
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.settings.SettingsViewModel
import com.bitshares.oases.ui.settings.showDarkModeSelectDialog
import com.bitshares.oases.ui.settings.showFeeReservedDialog
import com.bitshares.oases.ui.settings.showPriceUnitDialog
import modulon.component.toggleEnd
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.actionbar.title
import modulon.layout.recycler.section

class AppearanceSettingsFragment : ContainerFragment() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title(context.getString(R.string.appearance_settings_title))
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {
                header = context.getString(R.string.appearance_settings_theme_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.appearance_settings_dark_mode)
                    val modeString = context.resources.getStringArray(R.array.settings_dark_mode_entries)
                    globalPreferenceManager.DARK_MODE.observe(viewLifecycleOwner) {
                        subtitle = modeString.getOrElse(it.ordinal) { EMPTY_SPACE }
                    }
                    doOnClick { showDarkModeSelectDialog() }
                }
                cell {
                    text = context.getString(R.string.appearance_settings_show_indicator)
                    subtext = context.getString(R.string.appearance_settings_show_indicator_hint)
                    toggleEnd {
                        setChecked(globalPreferenceManager.INDICATOR.value, false)
                        globalPreferenceManager.INDICATOR.observe(viewLifecycleOwner) { setChecked(it, true) }
                    }
                    doOnClick { globalPreferenceManager.INDICATOR.value = !globalPreferenceManager.INDICATOR.value }
                }
            }
            section {
                header = context.getString(R.string.appearance_settings_market_and_trade_title)
                cell {
                    text = context.getString(R.string.appearance_settings_invert_trade_pair_color)
//                subtext = context.getString(R.string.appearance_settings_show_indicator_hint)
                    toggleEnd {
                        setChecked(globalPreferenceManager.INVERT_COLOR.value, false)
                        globalPreferenceManager.INVERT_COLOR.observe(viewLifecycleOwner) { setChecked(it, true) }
                    }
                    doOnClick { globalPreferenceManager.INVERT_COLOR.value = !globalPreferenceManager.INVERT_COLOR.value }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.appearance_settings_fee_reserved)
                    viewModel.feeReservedExtended.observe(viewLifecycleOwner) {
                        subtitle = if (it == AssetAmount.EMPTY) context.getString(R.string.appearance_settings_fee_reserve_auto) else formatAssetBalance(it)
                    }
                    doOnClick { showFeeReservedDialog() }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.appearance_settings_price_unit)
                    viewModel.priceUnit.observe(viewLifecycleOwner) {
                        subtitle = createAssetName(it)
                    }
//                viewModel.priceUnits.observe(viewLifecycleOwner) { }
                    doOnClick { showPriceUnitDialog() }
                }
            }
        }
    }

}