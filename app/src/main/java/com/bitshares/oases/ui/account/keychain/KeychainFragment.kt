package com.bitshares.oases.ui.account.keychain

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import bitshareskit.models.BrainKey
import bitshareskit.models.PrivateKey
import com.bitshares.oases.R
import com.bitshares.oases.chain.BrainKeyDict
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.extensions.viewbinder.bindPrivateKey
import com.bitshares.oases.extensions.viewbinder.bindPublicKey
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.account.permission.PermissionViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.wallet.startWalletUnlock
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.component.cell.ComponentCell
import modulon.component.cell.buttonStyle
import modulon.dialog.button
import modulon.dialog.doOnDismiss
import modulon.dialog.section
import modulon.extensions.compat.secureWindow
import modulon.extensions.compat.setClipboardToast
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.text.appendTag
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.text.toStringOrDefault
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.component.appbar.subtitle
import modulon.layout.lazy.construct
import modulon.layout.lazy.data
import modulon.layout.lazy.list
import modulon.layout.lazy.section
import java.util.*

class KeychainFragment : ContainerFragment() {

    private val viewModel: PermissionViewModel by activityViewModels()

    override fun onCreateView() {
        secureWindow()
        setupAction {
            titleConnectionState(getString(R.string.keychain_title))
            websocketStateMenu()
            walletStateMenu()
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupRecycler {
            Authority.values().forEach { permission ->
                section {
                    val authority = when (permission) {
                        Authority.OWNER -> viewModel.account.map { it?.owner }
                        Authority.ACTIVE -> viewModel.account.map { it?.active }
                        Authority.MEMO -> viewModel.account.map { it?.options }
                    }
                    header = when (permission) {
                        Authority.OWNER -> context.getString(R.string.permission_settings_owner_keys)
                        Authority.ACTIVE -> context.getString(R.string.permission_settings_active_keys)
                        Authority.MEMO -> context.getString(R.string.permission_settings_memo_keys)
                    }
                    cell { // ownerThresholdRow
                        updatePaddingVerticalHalf()
                        title = context.getString(R.string.permission_settings_threshold)
            //                        subtext = when (permission) {
            //                            Authority.OWNER -> context.getString(R.string.permission_settings_owner_threshold_hint)
            //                            Authority.ACTIVE -> context.getString(R.string.permission_settings_active_threshold_hint)
            //                            Authority.MEMO -> context.getString(R.string.permission_settings_memo_threshold_hint)
            //                        }
                        when (permission) {
                            Authority.OWNER -> viewModel.ownerMinThreshold
                            Authority.ACTIVE -> viewModel.activeMinThreshold
                            Authority.MEMO -> viewModel.memoMinThreshold
                        }.observe(viewLifecycleOwner) { subtitle = it.toStringOrDefault(context.getString(R.string.authority_threshold_unknown)) }

                        when (permission) {
                            Authority.OWNER -> viewModel.isOwnerSufficientLocal
                            Authority.ACTIVE -> viewModel.isActiveSufficientLocal
                            Authority.MEMO -> viewModel.isMemoSufficientLocal
                        }.observe(viewLifecycleOwner) {
                            subtitleView.setTextColor(
                                context.getColor(
                                    when (it) {
                                        true -> R.color.component
                                        false -> R.color.component_error
                                        null -> R.color.component_disabled
                                    }
                                )
                            )
                        }
                    }
                    // TODO: 2020/10/10 replace pair with payloads and apply to all
                    list<ComponentCell, Pair<PrivateKey, UShort?>> {
                        construct {
                        }
                        data { (key, threshold) ->
                            bindPrivateKey(key, threshold)
                            doOnClick { showPrivateKeyDialog(key, permission) }
                            doOnLongClick {
                                showRemoveKeyDialog(key, permission)
                            }
                        }
                        when (permission) {
                            Authority.OWNER -> viewModel.ownerKeyAuthsLocalWithWeightOrEmpty
                            Authority.ACTIVE -> viewModel.activeKeyAuthsLocalWithWeightOrEmpty
                            Authority.MEMO -> viewModel.memoKeyAuthsLocalWithWeightOrEmpty
                        }.observe(viewLifecycleOwner) { submitList(it) }
                    }
                    cell {
                        buttonStyle()
                        title = context.getString(R.string.keychain_import_key_button)
                        doOnClick { lifecycleScope.launch { if (startWalletUnlock()) showImportKeyDialog(permission) } }
                        isVisible = false
                        authority.observe(viewLifecycleOwner) { isVisible = it != null }
                    }
                }
                hint {
                    text = when (permission) {
                        Authority.OWNER -> context.getString(R.string.permission_settings_owner_permission_tip)
                        Authority.ACTIVE -> context.getString(R.string.permission_settings_active_permission_tip)
                        Authority.MEMO -> context.getString(R.string.permission_settings_memo_permission_tip)
                    }
                }
            }
        }
    }

    private fun showRemoveKeyDialog(key: PrivateKey, type: Authority) = showBottomDialog {
        title = context.getString(R.string.permission_remove_key_title)
        message = context.getString(R.string.permission_remove_key_message)
        isCancelableByButtons = true
        section {
            cell {
                icon = R.drawable.ic_tab_private_key_mode.contextDrawable()
                textView.typeface = typefaceMonoRegular
                subtextView.typeface = typefaceMonoRegular
                text = key.address
                subtext = key.wif
                updatePaddingVerticalV6()
            }
        }
        button {
            text = context.getString(R.string.button_remove)
            textColor = context.getColor(R.color.component_error)
            viewModel.user.observe(viewLifecycleOwner) {
                doOnClick {
                    blockchainDatabaseScope.launch {
                        if (it != null) LocalUserRepository.removeKey(globalWalletManager, it, key, type)
                    }
                }
            }
        }
        button { text = context.getString(R.string.button_cancel) }
    }

    private fun showPrivateKeyDialog(key: PrivateKey, type: Authority) = showBottomDialog {
        title = "Private Key"
        isCancelableByButtons = true
        section {
            cell {
                updatePaddingVerticalV6()
                icon = R.drawable.ic_tab_private_key_mode.contextDrawable()
                textView.typeface = typefaceMonoRegular
                subtextView.typeface = typefaceMonoRegular
                text = key.address
                subtext = key.wif
            }
        }
        button {
            text = context.getString(R.string.permission_button_copy_private)
            textColor = context.getColor(R.color.component_error)
            doOnClick { setClipboardToast("Private Key", key.wif) }
        }
        button {
            text = context.getString(R.string.permission_button_copy_address)
            doOnClick { setClipboardToast("Address", key.address) }
        }
        button { text = context.getString(R.string.button_cancel) }
    }

    private fun showImportKeyDialog(authority: Authority) = showBottomDialog {
        title = context.getString(R.string.permission_import_key_title)
        section {
            cell {
                icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
                title = context.getString(R.string.permission_cloud)
                doOnClick {
                    // TODO: 27/1/2022 test
    //                    this@showBottomDialog.customView = createVerticalLayout {
                    section {
                        cell {
                            updatePaddingVerticalHalf()
                            isVisible = false
                            viewModel.dialogKeyGenerated.observe(viewLifecycleOwner) {
                                isVisible = it.isNotEmpty()
                                if (it.isNotEmpty()) bindPublicKey(it.first().publicKey)
                            }
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.permission_password)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                isSingleLine = false
                                doAfterTextChanged { if (it != null) viewModel.generateDialogKeyFromSeed(it.toStringOrEmpty(), authority) }
                                viewModel.isFieldError.observe(viewLifecycleOwner) { isError = it }
                            }
                        }
                    }
                    button {
                        text = context.getString(R.string.button_add)
                        doOnClick { if (viewModel.importDialogKeys(authority)) dismiss() }
                    }
                    button {
                        text = context.getString(R.string.button_cancel)
                        doOnClick { dismiss() }
                    }
                }
            }
            cell {
                icon = R.drawable.ic_tab_private_key_mode.contextDrawable()
                title = context.getString(R.string.permission_private_key)
                doOnClick {
                    // TODO: 27/1/2022 test
    //                    this@showBottomDialog.customView = createVerticalLayout {
                    section {
                        cell {
                            updatePaddingVerticalHalf()
                            isVisible = false
                            viewModel.dialogKeyGenerated.observe(viewLifecycleOwner) {
                                isVisible = it.isNotEmpty()
                                if (it.isNotEmpty()) bindPublicKey(it.first().publicKey)
                            }
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.permission_private_key)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                isSingleLine = false
                                doAfterTextChanged { if (it != null) viewModel.generateDialogKeyFromWif(it.toStringOrEmpty()) }
                                viewModel.isFieldError.observe(viewLifecycleOwner) { isError = it }
                            }
                        }
                    }
                    button {
                        text = context.getString(R.string.button_add)
                        doOnClick { if (viewModel.importDialogKeys(authority)) dismiss() }
                    }
                    button {
                        text = context.getString(R.string.button_cancel)
                        doOnClick { dismiss() }
                    }
                }
            }
            cell {
                icon = R.drawable.ic_tab_brain_key_mode.contextDrawable()
                title = context.getString(R.string.permission_brain_key)
                doOnClick {
                    // TODO: 27/1/2022 test
    //                    this@showBottomDialog.customView = createVerticalLayout {
                    section {
                        cell {
                            updatePaddingVerticalHalf()
                            viewModel.dialogKeyGenerated.observe(viewLifecycleOwner) {
                                isVisible = it.isNotEmpty()
                                if (it.isNotEmpty()) bindPublicKey(it.first().publicKey)
                            }
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.permission_brain_key)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                isSingleLine = false
                                doAfterTextChanged {
                                    if (it != null) viewModel.generateDialogKeyFromMnemonic(it.toStringOrEmpty())
                                }
                                viewModel.isFieldError.observe(viewLifecycleOwner) { isError = it }
                            }
                        }
                        cell {
                            updatePaddingVerticalV6()
                            titleView.isSingleLine = false
                            viewModel.dialogKeyGenerated.observe(viewLifecycleOwner) {
                                isVisible = it.isNotEmpty() && it.first() is BrainKey
    //                                subtitleView.lineSpacingMultiplier = 1.1F
                                title = if (isVisible) {
                                    buildContextSpannedString {
                                        (it.first() as BrainKey).words.forEach {
                                            val color = if (BrainKeyDict.DICT_ALL.contains(it)) R.color.component else R.color.component_error
                                            appendTag(it.toUpperCase(Locale.ROOT), context.getColor(color), context.getColor(R.color.text_primary_inverted))
                                        }
                                    }
                                } else ""
                            }
                        }
                    }
                    button {
                        text = context.getString(R.string.button_add)
                        doOnClick { if (viewModel.importDialogKeys(authority)) dismiss() }
                    }
                    button {
                        text = context.getString(R.string.button_cancel)
                        doOnClick { dismiss() }
                    }
                }
            }
        }
        doOnDismiss { viewModel.clearDialogKey() }
    }


}