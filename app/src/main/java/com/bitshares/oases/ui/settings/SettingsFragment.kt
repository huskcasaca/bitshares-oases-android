package com.bitshares.oases.ui.settings

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.nameOrEmpty
import com.bitshares.oases.R
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.compat.startPermission
import com.bitshares.oases.extensions.compat.startWhitelist
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.preference.old.I18N
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.settings.appearance.AppearanceSettingsFragment
import com.bitshares.oases.ui.settings.node.NodeSettingsFragment
import com.bitshares.oases.ui.settings.storage.StorageSettingsFragment
import com.bitshares.oases.ui.wallet.WalletSettingsFragment
import modulon.component.IconSize
import modulon.dialog.button
import modulon.dialog.section
import modulon.extensions.compat.recreate
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.lifecycle.parentViewModels
import modulon.extensions.livedata.distinctUntilChangedBy
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.extensions.viewbinder.spacer
import modulon.layout.actionbar.title
import modulon.layout.recycler.section
import modulon.union.Union
import modulon.widget.RadioView

class SettingsFragment : ContainerFragment() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                cell {
                    viewModel.account.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
                        if (it != null) {
                            bindAccountV3(it, true, IconSize.COMPONENT_1)
                            doOnThrottledClick { startAccountBrowser(it.uid) }
                        }
                    }
                }
                viewModel.account.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
                    isVisible = it != null
                }
            }
            section {
                header = context.getString(R.string.settings_account_title)
                cell {
                    text = context.getString(R.string.whitelist_setting_title)
//                    subtext = context.getString(R.string.whitelist_setting_hint)
                    icon = R.drawable.ic_cell_whitelist.contextDrawable()
                    doOnThrottledClick { startWhitelist(Settings.KEY_CURRENT_ACCOUNT_ID.value) }
                }
                cell {
                    text = context.getString(R.string.permission_settings_title)
//                    subtext = context.getString(R.string.permission_settings_hint)
                    icon = R.drawable.ic_cell_authority.contextDrawable()
                    doOnThrottledClick { startPermission(Settings.KEY_CURRENT_ACCOUNT_ID.value) }
                }
                viewModel.account.observe(viewLifecycleOwner) {
                    isVisible = it != null
                }
            }
            section {
                header = context.getString(R.string.settings_default_title)
                cell {
                    text = context.getString(R.string.appearance_settings_title)
//                    subtext = context.getString(R.string.appearance_settings_hint)
                    icon = R.drawable.ic_cell_appearance.contextDrawable()
                    doOnThrottledClick { startFragment<AppearanceSettingsFragment>() }
                }
                cell {
                    text = context.getString(R.string.wallet_settings_title)
//                    subtext = context.getString(R.string.wallet_settings_hint)
                    icon = R.drawable.ic_cell_wallet.contextDrawable()
                    doOnThrottledClick { startFragment<WalletSettingsFragment>() }
                }
                cell {
                    text = context.getString(R.string.storage_settings_title)
//                    subtext = "Local databases and blockchain caches"
                    icon = R.drawable.ic_cell_database.contextDrawable()
                    doOnThrottledClick { startFragment<StorageSettingsFragment>() }
                }
                cell {
                    text = context.getString(R.string.node_settings_title)
//                    subtext = context.getString(R.string.node_settings_hint)
                    icon = R.drawable.ic_cell_nodes.contextDrawable()
                    doOnThrottledClick { startFragment<NodeSettingsFragment>() }
                }
                cell {
                    text = context.getString(R.string.language_settings_title)
//                    subtext = "Languages and locals"
                    icon = R.drawable.ic_cell_language.contextDrawable()
                    doOnThrottledClick { showLanguageSettingDialog() }
                }
            }
            spacer {
                backgroundColor = context.getColor(R.color.background)
            }
            hint {
                textView.gravity = Gravity.CENTER_HORIZONTAL
                viewModel.appDescription.observe { text = it }
            }
        }

        setupAction {
            title(context.getString(R.string.settings_title))
            networkStateMenu()
            walletStateMenu()
        }
    }

}

fun Union.showLanguageSettingDialog() = showBottomDialog {
    val viewModel: SettingsViewModel by parentViewModels()
    title = context.getString(R.string.language_settings_title)
    isCancelableByButtons = true
    section {
        I18N.values().forEach { locale ->
            cell {
                customViewStart = create<RadioView> {
                    setColors(context.getColor(R.color.component_disabled), context.getColor(R.color.component))
                    setFrameParams {
                        width =  context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                        height = context.resources.getDimensionPixelSize(modulon.R.dimen.icon_size_tiny)
                        gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    }
                    viewModel.language.observe(viewLifecycleOwner) {
                        setChecked(it == locale, true)
                    }
                }
                updatePaddingVerticalHalf()
                text = when (locale) {
                    I18N.DEFAULT                -> context.getString(R.string.language_settings_default)
                    else                        -> locale.localizedName
                }
                subtext = when (locale) {
                    I18N.DEFAULT                -> context.getString(R.string.language_settings_follow_system)
                    I18N.ENGLISH                -> context.getString(R.string.language_settings_english)
                    I18N.RUSSIAN                -> context.getString(R.string.language_settings_russian)
                    I18N.SIMPLIFIED_CHINESE     -> context.getString(R.string.language_settings_simplified_chinese)
                    I18N.TRADITIONAL_CHINESE    -> context.getString(R.string.language_settings_traditional_chinese)
                }
                doOnClick {
                    globalPreferenceManager.LANGUAGE.value = locale
                    dismissNow()
                    recreate()
                }
            }
        }
    }
    button { text = context.getString(R.string.button_cancel) }
}