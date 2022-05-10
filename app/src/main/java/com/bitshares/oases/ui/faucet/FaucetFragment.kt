package com.bitshares.oases.ui.faucet

import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.createAccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.accountNameFilter
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.blockchainNetworkScope
import com.bitshares.oases.database.entities.toUser
import com.bitshares.oases.extensions.text.StringFilter
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.text.createStringFilterHint
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.netowrk.faucets.*
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.wallet.startWalletUnlock
import kotlinx.coroutines.launch
import modulon.component.cell.buttonStyle
import modulon.dialog.DialogState
import modulon.dialog.button
import modulon.dialog.section
import modulon.dialog.updateButton
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.activity
import modulon.extensions.compat.finishActivity
import modulon.extensions.compat.secureWindow
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.appendColored
import modulon.extensions.text.appendItem
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section
import modulon.union.Union

class FaucetFragment : ContainerFragment() {

    val viewModel: FaucetViewModel by activityViewModels()

    override fun onCreateView() {
        secureWindow()
        setupAction {
            titleConnectionState(getString(R.string.faucet_register_title))
            websocketStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {
                header = "Faucet"
                cell {
                    title = context.getString(R.string.faucet_register_faucet)
                    viewModel.faucet.observe(viewLifecycleOwner) {
                        subtitle = it.faucetName
                    }
                    doOnClick { showFaucetSelectDialog() }
                }
            }
            section {
                header = "Detail"
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.faucet_register_account)
                    field {
                        filters = arrayOf(accountNameFilter)
                        inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                        typeface = typefaceMonoRegular
                        isSingleLine = false
                        doAfterTextChanged { viewModel.changeNameField(it.toStringOrEmpty()) }
                        viewModel.isAccountNameFieldError.observe(viewLifecycleOwner) { isError = it }
                    }
                    combineNonNull(viewModel.accountExist, viewModel.accountNameField).observe(viewLifecycleOwner) { (isExist, field) ->
                        subtextView.textWithVisibility = buildContextSpannedString {
                            if (isExist) {
                                appendColored(context.getString(R.string.faucet_register_account_exist), context.getColor(R.color.component_error))
                            } else {
                                append(createStringFilterHint(field, StringFilter.FILTER_CHEAP_ACCOUNT_NAME))
                            }
                        }
                        if (field.isEmpty()) subtextView.isVisible = false
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.faucet_register_password)
                    field {
                        inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                        typeface = typefaceMonoRegular
                        isSingleLine = false
                        doAfterTextChanged { viewModel.changePasswordField(it.toStringOrEmpty()) }
                        viewModel.isAccountPasswordFieldError.observe(viewLifecycleOwner) { isError = it }
                        viewModel.accountPasswordFieldNoticed.observe(viewLifecycleOwner) { fieldtext = it.toStringOrEmpty() }
                    }
                    viewModel.accountPasswordField.observe(viewLifecycleOwner) {
                        subtextView.textWithVisibility = createStringFilterHint(it, StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER)
                        if (it.isEmpty()) subtextView.isVisible = false

                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.faucet_register_password_repeat)
                    field {
                        inputType = InputTypeExtended.TYPE_PASSWORD_VISIBLE
                        typeface = typefaceMonoRegular
                        isSingleLine = false
                        doAfterTextChanged { viewModel.changePasswordRepeatField(it.toStringOrEmpty()) }
                        viewModel.isAccountPasswordRepeatFieldError.observe(viewLifecycleOwner) { isError = it }
                        viewModel.accountPasswordRepeatFieldNoticed.observe(viewLifecycleOwner) { fieldtext = it.toStringOrEmpty() }
                    }
                    combineNonNull(viewModel.accountPasswordField, viewModel.accountPasswordRepeatField).observe(viewLifecycleOwner) { (field1, field2) ->
                        subtextView.textWithVisibility = createStringFilterHint(field1, field2, StringFilter.FILTER_REQUIRE_EQUALS)
                        if (field2.isEmpty()) subtextView.isVisible = false
                    }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.faucet_register_random_button)
                    doOnClick { viewModel.generateRandom() }
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.faucet_register_next_button)
                    doOnClick { if (viewModel.validate()) showFaucetRegisterDialog(viewModel.generateRegisterInfo()) }
                }
            }

        }
    }

}

fun Union.showFaucetSelectDialog() = showBottomDialog {
    val viewModel: FaucetViewModel by activityViewModels()
    title = context.getString(R.string.faucet_register_faucet_dialog_title)
    section {
        Faucet.values().forEach {
            cell {
                text = it.faucetName
                subtext = it.url
                doOnClick { viewModel.faucet.value = it; dismiss() }
            }
        }
    }
}

fun Union.showFaucetRegisterDialog(info: FaucetRegister) = showBottomDialog {
    title = context.getString(R.string.faucet_register_register_title)
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.faucet_register_faucet)
            subtitle = info.faucet.faucetName
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.faucet_register_account)
            subtitle = createAccountSpan(createAccountObject(ChainConfig.EMPTY_INSTANCE, info.name))
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.faucet_register_password)
            subtitle = info.password
            subtitleView.typeface = typefaceMonoRegular
            subtitleView.isSingleLine = false
        }
//            addBaseTitleCell {
//                stripVerticalPaddingAll()
//                title = "Owner Key"
//            }
//            addBaseTextIconCell {
//                stripVerticalPaddingAll()
//                bindPrivateKey(info.ownerKey)
//            }
//            addBaseTitleCell {
//                stripVerticalPaddingAll()
//                title = "Active Key"
//            }
//            addBaseTextIconCell {
//                stripVerticalPaddingAll()
//                bindPrivateKey(info.activeKey)
//            }
//            addBaseTitleCell {
//                stripVerticalPaddingAll()
//                title = "Memo Key"
//            }
//            addBaseTextIconCell {
//                stripVerticalPaddingAll()
//                bindPrivateKey(info.memoKey)
//            }
    }
    button {
        text = context.getString(R.string.faucet_register_register_button)
        doOnClick {
            title = context.getString(R.string.faucet_register_registering_account)
            subtitle = EMPTY_SPACE
            text = context.getString(R.string.faucet_register_registering_button)
            isEnabled = false
            isCancelable = false
            updateButton(1) { isVisible = false }
            state = DialogState.PENDING
            lifecycleScope.launch {
                runCatching { FaucetRepository.registerAccountInFaucet(info) }.onSuccess {
                    when (it) {
                        is RegisterSuccess -> {
                            state = DialogState.SUCCESS
                            title = context.getString(R.string.faucet_register_register_success_title)
                            text = context.getString(R.string.button_import)
                            isEnabled = true
                            doOnClick {
                                hide()
                                lifecycleScope.launch {
                                    if (startWalletUnlock()) {
                                        blockchainNetworkScope.launch network@{
                                            val account = AccountRepository.getAccountOrNull(info.name) ?: return@network
                                            val user = account.toUser(ChainPropertyRepository.chainId).copy(
                                                ownerKeys = setOf(info.ownerKey),
                                                activeKeys = setOf(info.activeKey),
                                                memoKeys = setOf(info.memoKey),
                                            )
                                            blockchainDatabaseScope.launch { LocalUserRepository.add(globalWalletManager, user) }
                                        }
                                        finishActivity()
                                        dismiss()
                                        activity
                                    } else {
                                        show()
                                    }
                                }
                            }
                        }
                        is RegisterFailure -> {
                            state = DialogState.FAILURE
                            title = context.getString(R.string.faucet_register_register_failed_title)
                            subtitle = buildContextSpannedString { appendItem(*it.error.base.toTypedArray()) }
                            isEnabled = true
                            isVisible = false
                        }
                    }
                    isCancelable = true
                    updateButton(1) {
                        text = context.getString(R.string.button_dismiss)
                        isVisible = true
                    }
                }.onFailure {
                    title = context.getString(R.string.faucet_register_unknown_error_title)
                    subtitle = it.localizedMessage.toStringOrEmpty()
                    state = DialogState.FAILURE
                    text = context.getString(R.string.button_retry)
                    isCancelable = true
                    isEnabled = true
                    updateButton(1) { isVisible = true }
                }
            }
        }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismiss() }
    }
}