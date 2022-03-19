package com.bitshares.oases.ui.wallet

import android.hardware.biometrics.BiometricPrompt.BIOMETRIC_ERROR_CANCELED
import android.hardware.fingerprint.FingerprintManager
import android.text.Editable
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import bitshareskit.extensions.ifNull
import com.bitshares.oases.R
import com.bitshares.oases.chain.Clipboard
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.walletPasswordFilter
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.extensions.text.StringFilter
import com.bitshares.oases.extensions.text.createStringFilterHint
import com.bitshares.oases.extensions.text.validateStringFilter
import com.bitshares.oases.extensions.viewbinder.bindUserV1
import com.bitshares.oases.extensions.viewbinder.bindUserV3
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.security.fingerprint.FingerprintAuthentication
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.settings.node.NodeSettingsFragment
import com.mattprecious.swirl.SwirlView
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.component.IconSize
import modulon.dialog.*
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.*
import modulon.extensions.coroutine.debounce
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.text.formatBinaryPrefix
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.union.Union

suspend fun Union.showWalletPasswordUnlockDialog1() = showBooleanSuspendedBottomDialog {
    if (globalWalletManager.unlock()) dismissWith(true)
    val viewModel: WalletManagerViewModel by viewModels()
    title = context.getString(R.string.wallet_unlock_wallet_title)
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.wallet_manager_wallet_password_title)
            field {
                typeface = typefaceMonoRegular
                inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
                filters = arrayOf(walletPasswordFilter)
                doAfterTextChanged {
                    viewModel.changePasswordField(it.toStringOrEmpty())
                }
                viewModel.isPasswordFieldError.observe(viewLifecycleOwner) {
                    isError = it
                }
            }
            subtextView.isVisible = false
            viewModel.isPasswordFieldError.observe(viewLifecycleOwner) {
                subtextView.isVisible = it
                subtext = if (viewModel.password.length < 6) context.getString(R.string.wallet_manager_password_too_short) else context.getString(R.string.wallet_manager_password_incorrect)
            }
        }
    }
    button {
        text = context.getString(R.string.wallet_manager_unlock_wallet_button)
        doOnClick { if (viewModel.unlock()) dismissWith(true) }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismissWith(false) }
    }
    doOnDismiss { resumeWith(false) }
    showSoftKeyboard()
}

suspend fun Union.showWalletBiometricUnlockDialog() = if (globalWalletManager.unlock()) true else showBooleanSuspendedBottomDialog {
    val bioManager = FingerprintAuthentication(context)
    val service = getSystemService<FingerprintManager>() ?: return@showBooleanSuspendedBottomDialog dismissWith(false)
    title = context.getString(R.string.wallet_unlock_wallet_title)
    message = context.getString(R.string.fingerprint_touch_sensor)
    isCancelableByButtons = true
    var usePassword = false
    lateinit var fingerprint: SwirlView
    section {
        fingerprint = create<SwirlView> {
            normalColor = context.getColor(R.color.component_inactive)
            errorColor = context.getColor(R.color.component_error)
        }
        fingerprint.setState(SwirlView.State.ON)
        frameLayout {
            addWrap(fingerprint, 60.dp, 60.dp, gravity = Gravity.CENTER)
            setParamsRow(height = 140.dp)
        }
    }
    button {
        text = context.getString(R.string.wallet_enter_password)
        doOnClick { usePassword = true }
    }
    button {
        text = context.getString(R.string.button_cancel)
    }
    val cipher = try {
        globalWalletManager.provider.bioSecureProvider.initDecryptionCipher(globalWalletManager.provider.fingerprintNonceBytes)
    } catch (e: RuntimeException) {
        globalWalletManager.disableFingerprint()
        lifecycleScope.launch { dismissWith(this@showWalletBiometricUnlockDialog.showWalletPasswordUnlockDialog()) }
        return@showBooleanSuspendedBottomDialog
    }
    bioManager.startAuthentication(service, FingerprintManager.CryptoObject(cipher)) {
        val revert = debounce(lifecycleScope, 960) {
            message = context.getString(R.string.fingerprint_touch_sensor)
            fingerprint.setState(SwirlView.State.ON)
        }
        doOnAuthenticationSucceeded {
            dismissWith(globalWalletManager.unlockFingerprint())
        }
        doOnAuthenticationError { errorCode, errString ->
            message = errString.toString()
            fingerprint.setState(SwirlView.State.ERROR)
            lifecycleScope.launch {
                if (!usePassword && errorCode == BIOMETRIC_ERROR_CANCELED) {
                    dismissWith(false)
                } else {
                    dismissWith(this@showWalletBiometricUnlockDialog.showWalletPasswordUnlockDialog())
                }
            }
        }
        doOnAuthenticationFailed {
            message = context.getString(R.string.fingerprint_not_recognized)
            fingerprint.setState(SwirlView.State.ERROR)
            revert.invoke()
        }
        doOnAuthenticationHelp { helpCode, helpString ->
            message = helpString.toString()
            fingerprint.setState(SwirlView.State.ERROR)
            revert.invoke()
        }
    }
    doOnDismiss(bioManager::cancelAuthentication)
}

suspend fun Union.showWalletBiometricSetupDialog() = showBooleanSuspendedBottomDialog {
    if (!globalWalletManager.unlock()) dismissWith(false)
    val bioManager = FingerprintAuthentication(context)
    val service = getSystemService<FingerprintManager>() ?: return@showBooleanSuspendedBottomDialog dismissWith(false)
    title = context.getString(R.string.wallet_unlock_wallet_title)
    message = context.getString(R.string.fingerprint_touch_sensor)
    isCancelableByButtons = true
    lateinit var fingerprint: SwirlView
    section {
        fingerprint = create<SwirlView> {
            normalColor = context.getColor(R.color.component_inactive)
            errorColor = context.getColor(R.color.component_error)
        }
        fingerprint.setState(SwirlView.State.ON)
        frameLayout {
            addWrap(fingerprint, 60.dp, 60.dp, gravity = Gravity.CENTER)
            setParamsRow(height = 140.dp)
        }
    }
    button { text = context.getString(R.string.button_cancel) }
    val cipher = try {
        globalWalletManager.provider.bioSecureProvider.initEncryptionCipher()
    } catch (e: RuntimeException) {
        globalWalletManager.disableFingerprint()
        try {
            globalWalletManager.provider.bioSecureProvider.initEncryptionCipher()
        } catch (e: RuntimeException) {
            dismissWith(false)
            return@showBooleanSuspendedBottomDialog
        }
    }
    bioManager.startAuthentication(service, FingerprintManager.CryptoObject(cipher)) {
        val revert = debounce(lifecycleScope, 960) {
            message = context.getString(R.string.fingerprint_touch_sensor)
            fingerprint.setState(SwirlView.State.ON)
        }
        doOnAuthenticationSucceeded {
            globalWalletManager.enableFingerprint()
            dismissWith(true)
        }
        doOnAuthenticationError { errorCode, errString ->
            message = errString.toString()
            fingerprint.setState(SwirlView.State.ERROR)
//            resumeSafe(false)
        }
        doOnAuthenticationFailed {
            message = context.getString(R.string.fingerprint_not_recognized)
            fingerprint.setState(SwirlView.State.ERROR)
            revert.invoke()
        }
        doOnAuthenticationHelp { helpCode, helpString ->
            message = helpString.toString()
            fingerprint.setState(SwirlView.State.ERROR)
            revert.invoke()
        }
    }
    doOnDismiss {
        bioManager.cancelAuthentication()
        resumeWith(false)
    }
}

suspend fun Union.showWalletCorruptedResetDialog() = showBooleanSuspendedBottomDialog {
    title = context.getString(R.string.wallet_corrupted_dialog_title)
    message = context.getString(R.string.wallet_corrupted_dialog_message)
    button {
        text = context.getString(R.string.button_reset)
        textColor = context.getColor(R.color.component_error)
        doOnClick {
            globalWalletManager.reset()
            dismissWith(true)
        }
    }
    button {
        text = context.getString(R.string.button_cancel)
        textColor = context.getColor(R.color.component)
        doOnClick { dismissWith(false) }
    }
    doOnDismiss { resumeWith(false) }
}

// showWalletChangePasswordDialog

suspend fun Union.startWalletUnlock(changePassword: Boolean = false) = when {
    globalWalletManager.isCorrupted -> showWalletCorruptedResetDialog()
    !globalWalletManager.unlock() -> if (globalPreferenceManager.USE_BIO.value) showWalletBiometricUnlockDialog() else showWalletPasswordUnlockDialog(changePassword)
    else -> true
}

suspend fun Union.startWalletPasswordChange() = startWalletUnlock(true) && showWalletChangePasswordDialog()

// TODO: 2/10/2021 replace with operation and specify permission for each operation
// TODO: 30/10/2021 removed
@Deprecated("useless")
suspend fun Union.startPermissionCheck(vararg authority: Authority): Boolean = startWalletUnlock()
//suspend fun Union.startPermissionCheck1(vararg authority: Authority): Boolean {
//    if (WalletService.isCorrupted || !startWalletUnlock()) return false
//    return authority.all {
//        when (it) {
//            Authority.OWNER -> AuthorityService.isOwnerAuthorized.value
//            Authority.ACTIVE -> AuthorityService.isActiveAuthorized.value
//            Authority.MEMO -> AuthorityService.isMemoAuthorized.value
//        }
//    }.also {
//        if (!it) buildBottomDialog {
//            title = context.getString(R.string.transaction_keys_insufficient_title)
//            isCancelableByButtons = true
//            message = when {
//                authority.contains(Authority.OWNER) && authority.contains(Authority.ACTIVE) -> context.getString(R.string.transaction_keys_insufficient_message)
//                authority.contains(Authority.OWNER) -> context.getString(R.string.transaction_owner_keys_insufficient_message)
//                authority.contains(Authority.ACTIVE) -> context.getString(R.string.transaction_active_keys_insufficient_message)
//                else -> context.getString(R.string.transaction_keys_insufficient_message)
//            }
//            addButton {
//                text = context.getString(R.string.keychain_import_key_button)
//                doOnClick { startKeychain(AuthorityService.currentUser.value?.uid ?: ChainConfig.EMPTY_INSTANCE) }
//            }
//            addButton {
//                text = context.getString(R.string.button_dismiss)
//            }
//        }
//
//    }
//}


fun Union.showUserOptionDialog(user: User) = showBottomDialog {
    title = "Local User"
    section {
        cell {
            bindUserV3(user, IconSize.COMPONENT_1)
        }
    }
    button {
        text = "Switch To"
        doOnClick { showUserSwitchDialog(user); dismiss() }
    }
    button {
        text = "Remove"
        textColor = context.getColor(R.color.component_error)
        doOnClick { showUserRemovalDialog(user); dismiss() }
    }
    buttonCancel()
}


fun Union.showUserSwitchDialog(user: User) = showBottomDialog {
    if (ChainPropertyRepository.chainId == user.chainId) {
        LocalUserRepository.switch(user)
        dismiss()
        return@showBottomDialog
    }
    title = "Switch User"
    isCancelableByButtons = true
    section {
        cell {
            bindUserV3(user, IconSize.NORMAL)
            updatePaddingVerticalV6()
        }
    }
    message = "You have to switch to a [Testnet] node for this account"
    button {
        text = "Switch Node"
        doOnClick { startFragment<NodeSettingsFragment>() }
    }
    button { text = context.getString(R.string.button_cancel) }
}

//fun Union.showUserSwitchDialog(user: User) = buildBottomDialog {
//    title = "Switch User"
//    isCancelableByButtons = true
//    customView = createBaseTextAvatarCell {
//        iconSize = BaseComponentCell.Size.NORMAL
//        bindUser(user)
//        stripVerticalPaddingAll()
//    }
//    ChainPropertyRepository.currentChainId.observe(viewLifecycleOwner) {
//        message = if (user.chainId == it) "Are you sure to switch to this account?" else "You have to switch to a [Testnet] node for this account"
//    }
//    addButton {
//        text = "Switch"
//        isEnabled = false
//        ChainPropertyRepository.currentChainId.observe(viewLifecycleOwner) {
//            isEnabled = true
//            if (user.chainId == it) {
//                text = "Switch"
//                doOnClick { LocalUserRepository.switch(user) }
//            } else {
//                text = "Switch Node"
//                doOnClick { startActivity<NodeSettingsActivity>() }
//            }
//        }
//    }
//    addButton { text = context.getString(R.string.button_cancel) }
//}


fun Union.showUserRemovalDialog(user: User) = showBottomDialog {
    title = context.getString(R.string.wallet_settings_remove_user_title)
    isCancelableByButtons = true
    message = context.getString(R.string.wallet_settings_remove_user_message)
    section {
        cell {
            bindUserV3(user, IconSize.NORMAL)
            updatePaddingVerticalV6()
        }
    }
    button {
        text = context.getString(R.string.button_remove)
        textColor = context.getColor(R.color.component_error)
        doOnClick {
            blockchainDatabaseScope.launch {
                LocalUserRepository.remove(user)
            }
        }
    }
    button { text = context.getString(R.string.button_cancel) }
}


fun Union.showWalletResetDialog() = showBottomDialog {
    title = context.getString(R.string.wallet_settings_wallet_reset_title)
    message = context.getString(R.string.wallet_settings_reset_wallet_dialog_message)
    isCancelableByButtons = true
    button {
        text = context.getString(R.string.button_reset)
        textColor = context.getColor(R.color.component_error)
        doOnClick { globalWalletManager.reset() }
    }
    button { text = context.getString(R.string.button_cancel) }
}

fun Union.showWalletBackupDialog() = showBottomDialog {
    val viewModel: WalletManagerViewModel by viewModels()
    viewModel.buildBackup()
    title = context.getString(R.string.wallet_settings_backup_dialog)
    message = context.getString(R.string.wallet_settings_backup_dialog_message)
    section {
        cell {
            updatePaddingVerticalV6()
            icon = R.drawable.ic_cell_about.contextDrawable()
            viewModel.file.observe(viewLifecycleOwner) {
                title = it.name
                if (it.serialized != null) {
                    subtext = if (it == null) context.getString(R.string.wallet_settings_backup_checking) else context.getString(R.string.wallet_settings_backup_complete, it.serialized.accounts.size.toString(), it.serialized.privateKeys.size.toString())
                }
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.wallet_settings_backup_password)
            field {
                typeface = typefaceMonoRegular
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                doAfterTextChanged {
                    viewModel.changeNewPasswordField(it.toStringOrEmpty())
                }
                viewModel.isNewPasswordFieldError.observe(viewLifecycleOwner) {
                    isError = it
                }
            }
            viewModel.newPasswordField.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    subtext = createStringFilterHint(it, StringFilter.FILTER_PASSWORD_STRENGTH_NORMAL)
                    subtextView.isVisible = !validateStringFilter(it, StringFilter.FILTER_PASSWORD_STRENGTH_NORMAL)
                }
            }
            viewModel.state.observe(viewLifecycleOwner) {
                isVisible = it == DialogState.EMPTY
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.wallet_settings_backup_password_repeat)
            field {
                typeface = typefaceMonoRegular
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                doAfterTextChanged {
                    viewModel.changeRepeatPasswordField(it.toStringOrEmpty())
                }
                viewModel.isRepeatPasswordFieldError.observe(viewLifecycleOwner) {
                    isError = it
                }
            }
            viewModel.isRepeatPasswordFieldError.observe(viewLifecycleOwner) {
                subtextView.isTextError = it
                subtextView.isVisible = it
                subtext = if (it) context.getString(R.string.password_not_match) else EMPTY_SPACE
            }
            viewModel.state.observe(viewLifecycleOwner) {
                isVisible = it == DialogState.EMPTY
            }
        }
        showSoftKeyboard()
    }
    button {
        isVisible = false
        text = context.getString(R.string.button_confirm)
        viewModel.file.observe(viewLifecycleOwner) {
            if (it.serialized != null) {
                isVisible = true
                doOnClick { viewModel.createBackup() }
            }
        }
        viewModel.state.observe(viewLifecycleOwner) {
            isVisible = it == DialogState.EMPTY
        }
    }
    button {
        isVisible = false
        text = context.getString(R.string.wallet_settings_backup_save_file_button)
        viewModel.file.observe(viewLifecycleOwner) {
            if (it.serialized != null) {
                doOnClick {
                    lifecycleScope.launch {
                        val uri = suspendActivityForCreateDocument(it.name) ?: return@launch
                        viewModel.saveBackup(uri, context.contentResolver)
                    }
                }
            }
        }
        viewModel.state.observe(viewLifecycleOwner) {
            isVisible = it == DialogState.PENDING
        }
    }
    button {
        isVisible = false
        text = context.getString(R.string.wallet_settings_backup_save_text_button)
        viewModel.state.observe(viewLifecycleOwner) {
            isVisible = it == DialogState.PENDING
        }
        doOnClick {
            val plain = viewModel.sharePlainBackup()
            message = context.getString(R.string.wallet_settings_backup_save_text_message)
            section {
                cell {
                    title = context.getString(R.string.wallet_settings_backup_dialog)
                    textView.typeface = typefaceMonoRegular
                    textView.isSingleLine = false
                    textView.isSelectable = true
                    text = plain
                }
            }
            updateButton(1) { isVisible = false }
            updateButton(2) {
                text = context.getString(R.string.button_copy)
                doOnClick {
                    setClipboardToast(Clipboard.LABEL_WALLET_BACKUP, plain)
                    dismiss()
                }
            }
        }

    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismiss() }
        viewModel.state.observe(viewLifecycleOwner) {
            if (it == DialogState.SUCCESS || it == DialogState.FAILURE) {
                text = context.getString(R.string.button_dismiss)
            }
        }
    }
    viewModel.state.observe(viewLifecycleOwner) {
        when (it) {
            DialogState.SUCCESS -> {
                title = context.getString(R.string.wallet_settings_backup_success_dialog)
                message = context.getString(R.string.wallet_settings_backup_dialog_success_message)
            }
            DialogState.FAILURE -> {
                title = context.getString(R.string.wallet_settings_backup_failed_dialog)
                message = context.getString(R.string.wallet_settings_backup_dialog_failed_message)
            }
            DialogState.PENDING -> hideSoftKeyboard()
        }
        messageView.isVisible = it == DialogState.EMPTY
    }
}

fun Union.showWalletRestoreDialog() = showBottomDialog {
    title = context.getString(R.string.wallet_settings_restore_dialog)
    section {
        cell {
            text = context.getString(R.string.wallet_settings_restore_from_text)
            icon = R.drawable.ic_cell_wallet.contextDrawable()
            doOnClick {
            // TODO: 27/1/2022 test
//                    this@showBottomDialog.customView = createVerticalLayout {
                section {
                    cell {
                        // TODO: 25/1/2022 remove
                        val isErrorLive = NonNullMutableLiveData(false)
                        var field: Editable? = null
                        title = context.getString(R.string.wallet_settings_wallet_backup_text)
                        field {
                            maxLines = 5
                            doAfterTextChanged { field = it; isError = false }
                            isErrorLive.observe(viewLifecycleOwner) { isError = it }
                        }
                        button {
                            text = context.getString(R.string.button_import)
                            doOnClick {
                                lifecycleScope.launch {
                                    val file = WalletManagerViewModel.decodeFile(field.toStringOrEmpty())
                                    if (file != WalletManagerViewModel.BackupFile.INVALID) {
                                        showWalletFileRestoreDialog(file)
                                        dismiss()
                                    } else {
                                        isErrorLive.value = true
                                    }
                                }
                            }
                        }
                        button {
                            text = context.getString(R.string.button_cancel)
                            doOnClick { dismiss() }
                        }
                    }
                }
                showSoftKeyboard()
            }
        }
        cell {
            text = context.getString(R.string.wallet_settings_restore_from_file)
            icon = R.drawable.ic_cell_wallet_warning.contextDrawable()
            doOnClick {
                lifecycleScope.launch {
                    if (startWalletUnlock()) {
                        val uri = suspendActivityForOpenDocument(arrayOf("application/octet-stream")).ifNull { return@launch dismiss() }
                        val file = WalletManagerViewModel.decodeFile(uri, context.contentResolver)
                        if (file == WalletManagerViewModel.BackupFile.INVALID) showWalletRestoreErrorDialog() else showWalletFileRestoreDialog(file)
                        dismiss()
                    }
                }
            }
        }
    }
}

fun Union.showWalletFileRestoreDialog(file: WalletManagerViewModel.BackupFile) = showBottomDialog {
    val viewModel: WalletManagerViewModel by viewModels()
    title = context.getString(R.string.wallet_settings_restore_dialog)
    message = context.getString(R.string.wallet_settings_restore_dialog_message)
    section {
        cell {
            updatePaddingVerticalV6()
            isVisible = false
            icon = R.drawable.ic_cell_about.contextDrawable()
            title = "backup_file.bin"
            viewModel.file.observe(viewLifecycleOwner) {
                if (it.stream != null) {
                    title = it.name
                    subtext = it.size.formatBinaryPrefix()
                    isVisible = true
                } else {
                    showWalletRestoreErrorDialog()
                    dismiss()
                }
            }
            viewModel.state.observe(viewLifecycleOwner) {
                if (it == DialogState.SUCCESS) {
                    isVisible = false
                }
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.wallet_settings_backup_password)
            field {
                typeface = typefaceMonoRegular
                inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                doAfterTextChanged {
                    state = DialogState.EMPTY
                    viewModel.changePasswordField(it.toStringOrEmpty())
                }
                viewModel.isPasswordFieldError.observe(viewLifecycleOwner) { isError = it }
            }
            viewModel.state.observe(viewLifecycleOwner) {
                if (it == DialogState.SUCCESS) {
                    isVisible = false
                }
            }
        }
        verticalLayout {
            viewModel.users.observe(viewLifecycleOwner) {
                removeAllViews()
                it.forEach {
                    cell {
                        updatePaddingVerticalV6()
                        bindUserV1(it, IconSize.NORMAL)
                    }
                }
            }
        }
    }
    button {
        text = context.getString(R.string.button_unlock)
        doOnClick {
            hideSoftKeyboard()
            viewModel.unlockBackup()
        }
        viewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                DialogState.PENDING -> {
                    isEnabled = false
                    text = context.getString(R.string.button_unlocking)
                }
                DialogState.SUCCESS -> {
                    isEnabled = true
                    text = context.getString(R.string.button_import)
                    doOnClick {
                        viewModel.importBackup()
                        dismiss()
                    }
                }
                DialogState.FAILURE -> {
                    text = context.getString(R.string.button_unlock)
                    isEnabled = true
                    doOnClick {
                        hideSoftKeyboard()
                        viewModel.unlockBackup()
                    }
                    showSoftKeyboard()
                }
                DialogState.EMPTY -> {
                }
            }
        }
        viewModel.file.observe(viewLifecycleOwner) {
            isVisible = it.stream != null
        }
    }
    button {
        text = context.getString(R.string.button_cancel)
        viewModel.state.observe(viewLifecycleOwner) {
            if (it == DialogState.SUCCESS) {
                text = context.getString(R.string.button_dismiss)
            }
        }
        doOnClick { dismiss() }
    }
    viewModel.setBackupFile(file)
    viewModel.state.observe(viewLifecycleOwner) {
        state = it
    }
}

fun Union.showWalletCreateErrorDialog() = showBottomDialog {
    title = context.getString(R.string.wallet_settings_backup_dialog)
    message = context.getString(R.string.wallet_settings_backup_dialog_failed_message)
    isCancelableByButtons = true
    button { text = context.getString(R.string.button_confirm) }
}

fun Union.showWalletRestoreErrorDialog() = showBottomDialog {
    title = context.getString(R.string.wallet_settings_restore_dialog)
    message = context.getString(R.string.wallet_settings_restore_dialog_failed_message)
    isCancelableByButtons = true
    button { text = context.getString(R.string.button_confirm) }
}

suspend fun Union.startWalletRestore() {
    if (startWalletUnlock()) showWalletRestoreDialog()
}