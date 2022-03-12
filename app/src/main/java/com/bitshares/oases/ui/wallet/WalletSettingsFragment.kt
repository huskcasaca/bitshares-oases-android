package com.bitshares.oases.ui.wallet

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.uidOrEmpty
import com.bitshares.oases.extensions.compat.startKeychain
import com.bitshares.oases.extensions.viewbinder.bindUserV3
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.security.fingerprint.FingerprintAuthentication
import com.bitshares.oases.ui.base.ContainerFragment
import kotlinx.coroutines.launch
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.component.buttonStyle
import modulon.component.toggleEnd
import modulon.dialog.buttonCancel
import modulon.dialog.setupRecyclerLayout
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.graphics.alphaColor
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.actionbar.title
import modulon.layout.recycler.*
import modulon.union.Union

class WalletSettingsFragment : ContainerFragment() {

    private val viewModel: WalletViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title(context.getString(R.string.wallet_settings_title))
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {
                cell {
                    textView.apply {
                        textSize = 32f
                        textColor = R.color.cell_text_secondary.contextColor()
                    }
                    text = """
                        
                        
                        
                        Default Wallet
                    """.trimIndent()
                    foreground = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(R.color.background_dark.contextColor().alphaColor(0.8f), R.color.background_component.contextColor().alphaColor(0.8f)))
                    doOnClick {
                        showWalletUsers()
                    }
                }
            }
            section {
//                header = context.getString(R.string.wallet_settings_info_title)
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.wallet_settings_wallet_status)
                    globalWalletManager.isUnlocked.observe(viewLifecycleOwner) {
                        // TODO: 21/1/2022 textColor
                        subtextView.setTextColor(context.getColor(if (globalWalletManager.isCorrupted) R.color.component_error else modulon.R.color.component))
                        subtitle = context.getString(if (globalWalletManager.isCorrupted) R.string.wallet_settings_corrupted else if (it) R.string.wallet_settings_unlocked else R.string.wallet_settings_locked)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    text = context.getString(R.string.wallet_settings_passcode_lock)
//                    subtext = context.getString(R.string.wallet_settings_passcode_lock_hint)
                    toggleEnd {
                        viewModel.usePassword.observe(viewLifecycleOwner) {
                            if (!it && isChecked != it) setChecked(it, true) else setChecked(it, false)
                        }
                    }
                    doOnClick {
                        lifecycleScope.launch {
                            if (viewModel.usePassword.value) {
                                if (startWalletUnlock()) globalWalletManager.removePassword()
                            } else {
                                startWalletPasswordChange()
                            }
                        }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    text = context.getString(R.string.wallet_settings_fingerprint_unlock)
//                    subtext = context.getString(R.string.wallet_settings_use_fingerprint_hint)
                    isVisible = false
                    viewModel.usePassword.observe(viewLifecycleOwner) {
                        isVisible = it && FingerprintAuthentication.canAuthenticate(context)
                    }
                    toggleEnd {
                        viewModel.useBio.observe(viewLifecycleOwner) {
                            setChecked(it, true)
                        }
                    }
                    doOnClick {
                        if (viewModel.useBio.value) {
                            globalWalletManager.disableFingerprint()
                        } else {
                            lifecycleScope.launch {
                                if (startWalletUnlock()) showWalletBiometricSetupDialog()
                            }
                        }
                    }
                }
                expandable<ComponentCell> {
                    construct {
                        buttonStyle()
                        text = context.getString(R.string.wallet_settings_change_password)
                        isVisible = false
                        doOnClick {
                            lifecycleScope.launch {
                                startWalletPasswordChange()
                            }
                        }
                    }
                    globalPreferenceManager.USE_PASSWORD.observe(viewLifecycleOwner) { isExpanded = it }
                }
                cell {
                    updatePaddingVerticalHalf()
                    buttonStyle()
                    title = "Unlock Wallet"
                    globalWalletManager.isUnlocked.observe { isUnlocked ->
                        title = if (isUnlocked) "Lock Wallet" else "Unlock Wallet"
                        doOnClick {
                            if (isUnlocked) globalWalletManager.lock() else lifecycleScope.launch { startWalletUnlock() }
                        }
                    }
                }
            }
            section {
                cell {
                    buttonStyle()
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.wallet_settings_backup)
//                    subtext = context.getString(R.string.wallet_settings_backup_hint)
                    doOnClick {
                        lifecycleScope.launch {
                            if (startWalletUnlock()) showWalletBackupDialog()
                        }
                    }
                }
                cell {
                    buttonStyle()
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.wallet_settings_restore)
//                    subtext = context.getString(R.string.wallet_settings_restore_hint)
                    doOnClick { lifecycleScope.launch { startWalletRestore() } }
                }
            }
            section {
                cell {
                    buttonStyle()
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.wallet_settings_reset_wallet)
                    titleView.textColor = context.getColor(R.color.component_error)
//                subtext = context.getString(R.string.wallet_settings_wallet_reset_hint)
                    doOnClick { showWalletResetDialog() }
                }
            }
            logo()
        }
    }

}

private fun Union.showWalletUsers() = showBottomDialog {
    val viewModel: WalletViewModel by activityViewModels()
    title = "Default Wallet"
    setupRecyclerLayout {
        section {
            cell {
                textView.apply {
                    textSize = 32f
                    textColor = R.color.cell_text_secondary.contextColor()
                }
                text = """
                        
                        
                        
                        Default Wallet
                    """.trimIndent()
                foreground = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(R.color.background_dark.contextColor().alphaColor(0.8f), R.color.background_component.contextColor().alphaColor(0.8f)))
            }
        }
        section {
            header = "Default Wallet"
            list<ComponentCell, User> {
                construct {
                    updatePaddingVerticalHalf()
                }
                data { user ->
                    bindUserV3(user, IconSize.COMPONENT_0)
                    doOnClick { startKeychain(user) }
                    doOnLongClick { showUserOptionDialog(user) }
                }
                payload { data, payload -> isChecked = data.uid == payload as Long }
                distinctItemsBy { it.uid }
                viewModel.userList.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
                viewModel.currentUser.observe(viewLifecycleOwner) { adapter.submitPayload(it.uidOrEmpty) }
            }
        }
        section {  }
    }
    buttonCancel()
}