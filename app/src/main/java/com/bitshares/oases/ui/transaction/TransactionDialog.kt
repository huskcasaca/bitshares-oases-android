package com.bitshares.oases.ui.transaction
import androidx.core.view.isVisible
import androidx.lifecycle.distinctUntilChanged
import bitshareskit.errors.ErrorCode
import bitshareskit.errors.GrapheneException
import bitshareskit.errors.TransactionBroadcastException
import com.bitshares.oases.R
import com.bitshares.oases.chain.operationNameStringResMap
import com.bitshares.oases.extensions.compat.startBlockBrowser
import com.bitshares.oases.extensions.compat.startKeychain
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.wallet.startWalletUnlock
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.dialog.BottomDialogFragment
import modulon.dialog.DialogState
import modulon.dialog.button
import modulon.dialog.updateButton
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.emptyLiveData
import modulon.extensions.view.doOnClick

fun BottomDialogFragment.bindTransaction(builder: TransactionBuilder, accountViewModel: AccountViewModel) {
    title = context.getString(R.string.transaction_broadcast_title)
    if (builder.operationTypes.isNotEmpty()) {
        subtitle = context.getString(operationNameStringResMap.getValue(builder.operationTypes.first()))
    }
    button {
        text = context.getString(R.string.button_broadcast)
        fun broadcastClick() {
            builder.apply {
                onStart {
                    title = context.getString(R.string.transaction_broadcast_broadcasting)
                    isCancelable = false
                    state = DialogState.PENDING
                    updateButton(0) { isVisible = false }
                    updateButton(1) { isVisible = false }
                    doOnClick()
                }
                onConfirm {
                    title = context.getString(R.string.transaction_broadcast_confirming)
                }
                onSuccess {
                    title = context.getString(R.string.transaction_broadcast_success_title)
                    subtitle = "${context.getString(R.string.transaction_broadcast_block_height)} ${it.blockNum}"
                    state = DialogState.SUCCESS
                    isCancelable = true
                    isCancelableByButtons = true
                    updateButton(0) {
                        text = context.getString(R.string.button_detail)
                        isVisible = true
                        doOnClick { startBlockBrowser(it.blockNum) }
                    }
                    updateButton(1) {
                        text = context.getString(R.string.button_dismiss)
                        isVisible = true
                    }
                }
                onFailure {
                    it.printStackTrace()
                    title = when (it) {
                        is TransactionBroadcastException -> when (it.code) {
                            ErrorCode.NO_CONNECTION -> context.getString(R.string.transaction_broadcast_no_connection_title)
                            ErrorCode.BROADCAST_TIMEOUT -> context.getString(R.string.transaction_broadcast_timeout_title)
                            ErrorCode.MISSING_ACTIVE_AUTH, ErrorCode.MISSING_OWNER_AUTH, ErrorCode.MISSING_OTHER_AUTH -> context.getString(R.string.transaction_broadcast_missing_authority)
                            else -> context.getString(R.string.transaction_broadcast_failed_title)
                        }
                        else -> context.getString(R.string.transaction_broadcast_failed_title)
                    }
                    if (it is GrapheneException) {
                        subtitle = """${context.getString(R.string.transaction_broadcast_error_code)} ${it.code.code}"""
                    }
                    state = DialogState.FAILURE
                    isCancelable = true
                    isCancelableByButtons = true
                    updateButton(1) {
                        text = context.getString(R.string.button_dismiss)
                        isVisible = true
                    }
                }
                broadcast()
            }
        }
        isEnabled = false
        val list = builder.requiredAuthority
        val isAuthorized = if (list.contains(Authority.OWNER)) accountViewModel.isOwnerAuthorizedLive else if (list.contains(
                Authority.ACTIVE)) accountViewModel.isActiveAuthorizedLive else emptyLiveData(true)
        // FIXME: 17/10/2021 Cannot add the same observer with different lifecycles
        //  distinctUntilChanged() added to fix this
        accountViewModel.isAuthorized.distinctUntilChanged().observe(viewLifecycleOwner){ }
        isAuthorized.distinctUntilChanged().observe(viewLifecycleOwner){
            if (!builder.isBroadcast && it) {
                when {
                    list.contains(Authority.OWNER) -> builder.replaceKeys(accountViewModel.ownerRequiredAuths.value)
                    list.contains(Authority.ACTIVE) -> builder.replaceKeys(accountViewModel.activeRequiredAuths.value)
                }
            }
        }
        combineNonNull(accountViewModel.accountUid, isAuthorized, globalWalletManager.isUnlocked).observe(viewLifecycleOwner) { (uid, isAuthorized, isUnlocked) ->
            if (!builder.isBroadcast) {
                isEnabled = true
                text = when {
                    isUnlocked && isAuthorized -> context.getString(R.string.button_broadcast)
                    !isUnlocked -> context.getString(R.string.wallet_manager_unlock_wallet_button)
                    list.contains(Authority.OWNER) -> context.getString(R.string.keychain_import_owner_key_button)
                    list.contains(Authority.ACTIVE) -> context.getString(R.string.keychain_import_active_key_button)
                    list.contains(Authority.MEMO) -> context.getString(R.string.keychain_import_add_memo_auth_button)
                    else -> context.getString(R.string.keychain_import_key_button)
                }
                doOnClick {
                    when {
                        isUnlocked && isAuthorized -> broadcastClick()
                        !isUnlocked -> lifecycleScope.launch {
                            hide()
                            startWalletUnlock()
                            show()
                        }
                        else -> startKeychain(uid)
                    }
                }
            }
        }
        doOnClick { broadcastClick() }
    }
    button {
        text = context.getString(R.string.button_cancel)
        doOnClick { dismiss() }
    }

}
