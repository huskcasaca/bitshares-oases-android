package com.bitshares.oases.ui.account.permission

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.chain.Authority.*
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.viewbinder.bindAccountAuth
import com.bitshares.oases.extensions.viewbinder.bindPublicKey
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.permission.PermissionFragment.Tabs
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAccountPicker
import bitshareskit.chain.Authority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.component.buttonStyle
import modulon.dialog.button
import modulon.dialog.dismissWith
import modulon.dialog.doOnDismiss
import modulon.dialog.section
import modulon.extensions.compat.showBooleanSuspendedBottomDialog
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.emptyLiveData
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.layout.recycler.*

class PermissionFragment_Tabs : ContainerFragment() {

    private val tab by lazy { requireArguments().get(IntentParameters.KEY_TAB_TYPE) as Tabs }
    private val permission by lazy {
        when (tab) {
            Tabs.OWNER -> OWNER
            Tabs.ACTIVE -> ACTIVE
            Tabs.MEMO -> MEMO
            else -> OWNER
        }
    }

    private val viewModel: PermissionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (tab == Tabs.CLOUD_WALLET) setupRecycler {
            section {
                header = "New Password"
                cell {
                    updatePaddingVerticalHalf()
//                        title = context.getString(R.string.permission_settings_new_password)
                    field {
                        inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                        isSingleLine = false
                        typeface = typefaceMonoRegular
                        doAfterTextChanged { if (it != null) viewModel.generateKeyFromSeed(it.toStringOrEmpty()) }
                        viewModel.newPasswordField.observe(viewLifecycleOwner) {
                            fieldtext = it
                        }
                    }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.permission_settings_generate_random_button)
                    doOnClick { viewModel.randomPassword() }
                }
            }
            Authority.values().forEach { authority ->
                section {
                    header = when (authority) {
                        OWNER -> "Owner"
                        ACTIVE -> "Active"
                        MEMO -> "Memo"
                    }
                    cell {
                        when (authority) {
                            OWNER -> viewModel.ownerGenerated
                            ACTIVE -> viewModel.activeGenerated
                            MEMO -> viewModel.memoGenerated
                        }.observe(viewLifecycleOwner) {
                            if (it.isNotEmpty()) bindPublicKey(it.first().publicKey)
                        }
                    }
                    cell {
                        buttonStyle()
                        when (authority) {
                            OWNER -> viewModel.isOwnerKey
                            ACTIVE -> viewModel.isActiveKey
                            MEMO -> viewModel.isMemoKey
                        }.observe(viewLifecycleOwner) {
                            val stringRes = when (authority) {
                                OWNER -> if (it) R.string.permission_settings_remove_owner_auth else R.string.permission_settings_add_owner_auth
                                ACTIVE -> if (it) R.string.permission_settings_remove_active_auth else R.string.permission_settings_add_active_auth
                                MEMO -> if (it) R.string.permission_settings_remove_memo_auth else R.string.permission_settings_replace_memo_auth
                            }
                            title = context.getString(stringRes)
                            titleView.textColor = context.getColor(if (it) R.color.component_error else R.color.component)
                            doOnClick {
                                when (authority) {
                                    OWNER -> if (it) viewModel.removeOwnerKeyAuths() else viewModel.addOwnerKeyAuths()
                                    ACTIVE -> if (it) viewModel.removeActiveKeyAuths() else viewModel.addActiveKeyAuths()
                                    MEMO -> if (it) viewModel.removeMemoKeyAuths() else viewModel.addMemoKeyAuths()
                                }
                            }
                        }
                    }
                    isVisible = false
                    when (authority) {
                        OWNER -> viewModel.ownerGenerated
                        ACTIVE -> viewModel.activeGenerated
                        MEMO -> viewModel.memoGenerated
                    }.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                }
            }
            logo()
        } else setupRecycler {
            section {
                header = "Threshold"
                cell { // ownerThresholdRow
                    title = context.getString(R.string.permission_settings_threshold)
        //                    subtext = when (permission) {
        //                        OWNER -> context.getString(R.string.permission_settings_owner_threshold_hint)
        //                        ACTIVE -> context.getString(R.string.permission_settings_active_threshold_hint)
        //                        MEMO -> context.getString(R.string.permission_settings_memo_threshold_hint)
        //                    }
                    when (permission) {
                        OWNER -> viewModel.ownerThresholdChanged
                        ACTIVE -> viewModel.activeThresholdChanged
                        MEMO -> viewModel.memoThresholdChanged
                    }.observe(viewLifecycleOwner) { (old, new) ->
                        subtitle = (new ?: old).toString()
                        title = context.getString(if (new == null) R.string.permission_settings_threshold else R.string.permission_new_threshold)
                    }
                    when (permission) {
                        OWNER -> viewModel.isOwnerSufficient
                        ACTIVE -> viewModel.isActiveSufficient
                        MEMO -> viewModel.isMemoSufficient
                    }.observe(viewLifecycleOwner) {
                        subtitleView.setTextColor(context.getColor(if (it) R.color.component else R.color.component_error))
                    }
                    doOnClick {
                        val threshold = when (permission) {
                            OWNER -> viewModel.ownerThresholdChanged
                            ACTIVE -> viewModel.activeThresholdChanged
                            MEMO -> viewModel.memoThresholdChanged
                        }.value
                        if (permission != MEMO) showThresholdChangeDialog(null, threshold?.second ?: threshold?.first)
                    }
                }
            }
            section {
                val keySource = when (permission) {
                    OWNER -> viewModel.ownerKeyAuthsChecked
                    ACTIVE -> viewModel.activeKeyAuthsChecked
                    MEMO -> viewModel.memoKeyAuthsChecked
                }
                header = context.getString(R.string.permission_settings_key_auths)
                list<ComponentCell, Triple<PublicKey, UShort, Boolean>> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data { (key, threshold, local) ->
                        bindPublicKey(key, threshold)
                        if (local) iconView.setColorFilter(context.getColor(R.color.component))
                        doOnClick {
                            if (permission != MEMO) showThresholdChangeDialog(key, threshold.toUInt())
                        }
                        doOnLongClick {
                            if (permission != MEMO) showKeyAuthRemoveDialog(key)
                        }
                    }
                    distinctItemsBy { it.first.address }
                    distinctContentBy { it }
                    keySource.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                cell {
                    buttonStyle()
                    title = context.getString(if (permission == MEMO) R.string.permission_settings_replace_key else R.string.permission_settings_add_key_auth)
                    doOnClick { lifecycleScope.launch { if (permission != MEMO || permission == MEMO && showReplaceMemoDialog()) showAddKeyDialog() } }
                }
//                keySource.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                header = context.getString(R.string.permission_settings_account_auths)
                val accountSource = when (permission) {
                    OWNER -> viewModel.ownerAccountAuthsChanged
                    ACTIVE -> viewModel.activeAccountAuthsChanged
                    MEMO -> emptyLiveData()
                }
                list<ComponentCell, Pair<AccountObject, UShort>> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data { (account, threshold) ->
                        bindAccountAuth(account, threshold)
                        doOnClick {
                            showThresholdChangeDialog(account, threshold.toUInt())
                        }
                        doOnLongClick {
                            showAccountAuthRemovalDialog(account)
                        }
                    }
                    distinctItemsBy { it.first.uid }
                    distinctContentBy { it.second }
                    accountSource.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.permission_settings_add_account_auths)
                    doOnClick {
                        lifecycleScope.launch(Dispatchers.Main) {
                            startAccountPicker { if (it != null) viewModel.addAccountAuths(it, permission) }
                        }
                    }
                }
                if (permission == MEMO) isVisible = false
        //                accountSource.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            hint {
                text = when (permission) {
                    OWNER -> context.getString(R.string.permission_settings_owner_permission_tip)
                    ACTIVE -> context.getString(R.string.permission_settings_active_permission_tip)
                    MEMO -> context.getString(R.string.permission_settings_memo_permission_tip)
                }
            }
            logo()
        }
    }

    private fun showThresholdChangeDialog(component: Any?, default: UInt?) = showBottomDialog {
        val viewModel: PermissionViewModel by activityViewModels()
        title = context.getString(R.string.permission_new_threshold)
        section {
            when (component) {
                is PublicKey -> cell {
                    updatePaddingVerticalV6()
                    bindPublicKey(component)
                }
                is AccountObject -> cell {
                    updatePaddingVerticalV6()
                    bindAccountAuth(component)
                }
            }
            cell {
                updatePaddingVerticalHalf()
                title = when (component) {
                    is PublicKey -> context.getString(R.string.permission_new_threshold)
                    is AccountObject -> context.getString(R.string.permission_new_threshold)
                    else -> when (permission) {
                        OWNER -> context.getString(R.string.permission_new_owner_threshold)
                        ACTIVE -> context.getString(R.string.permission_new_active_threshold)
                        MEMO -> context.getString(R.string.permission_new_memo_threshold)
                    }
                }
                field {
                    inputType = InputTypeExtended.TYPE_NUMBER_DECIMAL
                    doAfterTextChanged { viewModel.thresholdField = it.toStringOrEmpty() }
                    viewModel.isThresholdFieldError.observe(viewLifecycleOwner) {
                        isError = it
                        requestFocus()
                    }
                    showSoftKeyboard()
                    fieldtext = default.toString()
                }
            }
        }
        button {
            text = context.getString(R.string.button_confirm)
            doOnClick { if (viewModel.changeThreshold(permission, component)) dismiss() }
        }
        button {
            text = context.getString(R.string.button_cancel)
            doOnClick { dismiss() }
        }
    }

    private fun showKeyAuthRemoveDialog(key: PublicKey) = showBottomDialog {
        val viewModel: PermissionViewModel by activityViewModels()
        title = context.getString(R.string.permission_remove_key_title)
        message = context.getString(R.string.permission_remove_key_message)
        isCancelableByButtons = true
        section {
            cell {
                bindPublicKey(key)
                updatePaddingVerticalV6()
            }
        }
        button {
            text = context.getString(R.string.button_remove)
            textColor = context.getColor(R.color.component_error)
            doOnClick { viewModel.removeKeyAuths(key, permission) }
        }
        button {
            text = context.getString(R.string.button_cancel)
        }
    }

    private fun showAccountAuthRemovalDialog(account: AccountObject) = showBottomDialog {
        val viewModel: PermissionViewModel by activityViewModels()
        title = context.getString(R.string.permission_remove_account_title)
        message = context.getString(R.string.permission_remove_account_message)
        isCancelableByButtons = true
        section {
            cell {
                iconSize = IconSize.NORMAL
                bindAccountAuth(account)
                updatePaddingVerticalV6()
            }
        }
        button {
            text = context.getString(R.string.button_remove)
            textColor = context.getColor(R.color.component_error)
            doOnClick { viewModel.removeAccountAuths(account, permission) }
        }
        button {
            text = context.getString(R.string.button_cancel)
        }
    }

    enum class ImportKeyType { CLOUD, WIF, BRAIN }

    private fun showAddKeyDialog() = showBottomDialog {
        val viewModel: PermissionViewModel by activityViewModels()
        title = context.getString(if (permission == MEMO) R.string.permission_settings_replace_key else R.string.permission_settings_add_key_auth)
        subtitle = when (permission) {
            OWNER -> context.getString(R.string.permission_owner_key)
            ACTIVE -> context.getString(R.string.permission_active_key)
            MEMO -> context.getString(R.string.permission_memo_key)
        }
        section {
            ImportKeyType.values().forEach { keyType ->
                cell {
                    icon = when (keyType) {
                        ImportKeyType.CLOUD -> R.drawable.ic_tab_cloud_mode
                        ImportKeyType.WIF -> R.drawable.ic_tab_private_key_mode
                        ImportKeyType.BRAIN -> R.drawable.ic_tab_brain_key_mode
                    }.contextDrawable()
                    text = when (keyType) {
                        ImportKeyType.CLOUD -> R.string.permission_cloud
                        ImportKeyType.WIF -> R.string.permission_private_key
                        ImportKeyType.BRAIN -> R.string.permission_brain_key
                    }.contextString()
                    doOnClick {
                        // TODO: 27/1/2022 test
    //                    this@showBottomDialog.customView = createVerticalLayout {
                        section {
                            cell {
                                updatePaddingVerticalV6()
                                isVisible = false
                                viewModel.dialogKeyGenerated.observe(viewLifecycleOwner) {
                                    isVisible = it.isNotEmpty()
                                    if (it.isNotEmpty()) bindPublicKey(it.first().publicKey)
                                }
                            }
                            cell {
                                updatePaddingVerticalV6()
                                title = when (keyType) {
                                    ImportKeyType.CLOUD -> context.getString(R.string.permission_password)
                                    ImportKeyType.WIF -> context.getString(R.string.permission_private_key)
                                    ImportKeyType.BRAIN -> context.getString(R.string.permission_brain_key)
                                }
                                field {
                                    inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                    isSingleLine = false
                                    doAfterTextChanged {
                                        if (it != null) when (keyType) {
                                            ImportKeyType.CLOUD -> viewModel.generateDialogKeyFromSeed(it.toStringOrEmpty(), permission)
                                            ImportKeyType.WIF -> viewModel.generateDialogKeyFromWif(it.toStringOrEmpty())
                                            ImportKeyType.BRAIN -> viewModel.generateDialogKeyFromMnemonic(it.toStringOrEmpty())
                                        }
                                    }
                                    viewModel.randomField.observe(viewLifecycleOwner) { fieldtext = it }
                                    viewModel.isFieldError.observe(viewLifecycleOwner) { isError = it }
                                }
                            }
                            cell {
                                updatePaddingVerticalV6()
                                if (permission == MEMO) {
                                    isVisible = false
                                    viewModel.changeThreshold("1")
                                }
                                title = context.getString(R.string.permission_settings_threshold)
                                field {
                                    inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                                    doAfterTextChanged { viewModel.changeThreshold(it.toStringOrEmpty()) }
                                    viewModel.isThresholdError.observe(viewLifecycleOwner) { isError = it }
                                }
                            }
                        }
                        button {
                            text = context.getString(R.string.button_add)
                            doOnClick { if (viewModel.addDialogKeyAuths(permission)) dismiss() }
                        }
                        button {
                            text = context.getString(R.string.button_random)
                            doOnClick { viewModel.generateRandom(keyType) }
                        }
                        button {
                            text = context.getString(R.string.button_cancel)
                            doOnClick { dismiss() }
                        }
                        doOnDismiss { viewModel.clearDialogKey() }
                    }
                }

            }
        }
    }

    private suspend fun showReplaceMemoDialog() = showBooleanSuspendedBottomDialog {
        title = context.getString(R.string.word_warning)
        message = context.getString(R.string.permission_replace_memo_key_message)
        isCancelableByButtons = true
        button {
            text = context.getString(R.string.button_confirm)
            textColor = context.getColor(R.color.component_error)
            doOnClick { dismissWith(true) }
        }
        button {
            text = context.getString(R.string.button_cancel)
            textColor = context.getColor(R.color.component)
        }
    }

}