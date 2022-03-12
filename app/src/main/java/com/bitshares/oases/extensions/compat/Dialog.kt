package com.bitshares.oases.extensions.compat
import androidx.core.view.isVisible
import bitshareskit.extensions.extractOperationComponents
import bitshareskit.extensions.isSmartcoin
import bitshareskit.extensions.symbolOrId
import bitshareskit.objects.*
import bitshareskit.operations.Operation
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.operationNameStringResMap
import com.bitshares.oases.extensions.text.createAssetName
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import kotlinx.coroutines.launch
import modulon.dialog.*
import modulon.extensions.compat.*
import modulon.extensions.view.doOnClick
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.startScrolling
import modulon.union.Union
import java.util.*

suspend fun Union.showChangesDiscardDialog(msg: String? = null) = showBooleanSuspendedBottomDialog {
    title = context.getString(R.string.transaction_discard_changes_title)
    message = msg ?: context.getString(R.string.transaction_discard_changes_message)
    isCancelableByButtons = true
    button {
        text = context.getString(R.string.button_broadcast)
        doOnClick { dismissWith(true) }
    }
    button {
        text = context.getString(R.string.button_discard)
        doOnClick { activity.finish() }
    }
    doOnDismiss { resumeWith(false) }
}

fun Union.showAccountBrowserDialog(account: AccountObject) = showBottomDialog {
    title = context.getString(R.string.account_dialog_title)
    subtitle = account.name.toUpperCase(Locale.ROOT)
    section {
        cell {
            text = context.getString(R.string.account_option_account_detail)
            icon = R.drawable.ic_cell_membership.contextDrawable()
            doOnClick {
                startAccountBrowser(account.uid)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_add_observer)
            icon = R.drawable.ic_menu_add_observer.contextDrawable()
            doOnClick {
                blockchainDatabaseScope.launch { LocalUserRepository.addForObserve(account) }
                toast(context.getString(R.string.account_observe_tip))
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_copy_account_name)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_ACCOUNT_NAME, account.name)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_transfer)
            icon = R.drawable.ic_cell_transfer.contextDrawable()
            doOnClick {
                startTransferTo(account)
                dismiss()
            }
            isVisible = false
            LocalUserRepository.decryptCurrentUserOnly(globalWalletManager).observe(lifecycleOwner) {
                isVisible = it?.uid != account.uid
            }
        }
        cell {
            text = context.getString(R.string.option_share)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_ACCOUNT_NAME, account.name)
                dismiss()
            }
        }
    }
}

fun Union.showWitnessBrowserDialog(witness: WitnessObject) = showBottomDialog {
    title = context.getString(R.string.witness_dialog_title)
    subtitle = witness.witnessAccount.name.toUpperCase(Locale.ROOT)
    section {
        cell {
            text = context.getString(R.string.witness_option_witness_detail)
            icon = R.drawable.ic_cell_membership.contextDrawable()
            doOnClick {
                startAccountBrowser(witness.witnessAccount.uid)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_add_observer)
            icon = R.drawable.ic_menu_add_observer.contextDrawable()
            doOnClick {
                blockchainDatabaseScope.launch { LocalUserRepository.addForObserve(witness.witnessAccount) }
                toast(context.getString(R.string.account_observe_tip))
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_copy_account_name)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_ACCOUNT_NAME, witness.witnessAccount.name)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_transfer)
            icon = R.drawable.ic_cell_transfer.contextDrawable()
            doOnClick {
                startTransferTo(witness.witnessAccount)
                dismiss()
            }
            isVisible = false
            LocalUserRepository.decryptCurrentUserOnly(globalWalletManager).observe(lifecycleOwner) {
                isVisible = it?.uid != witness.witnessAccount.uid
            }
        }
        cell {
            text = context.getString(R.string.option_goto_website)
            subtext = witness.url.toString()
            subtextView.startScrolling()
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            isVisible = witness.url.isAbsolute
            doOnClick {
                startUriBrowser(witness.url)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.option_share)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_ACCOUNT_NAME, witness.witnessAccount.name)
                dismiss()
            }
        }
    }
}

fun Union.showCommitteeBrowserDialog(committee: CommitteeMemberObject) = showBottomDialog {
    title = context.getString(R.string.committee_member_dialog_title)
    subtitle = committee.committeeMemberAccount.name.toUpperCase(Locale.ROOT)
    section {
        cell {
            text = context.getString(R.string.option_committee_detail)
            icon = R.drawable.ic_cell_membership.contextDrawable()
            doOnClick {
                startAccountBrowser(committee.committeeMemberAccount.uid)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_add_observer)
            icon = R.drawable.ic_menu_add_observer.contextDrawable()
            doOnClick {
                blockchainDatabaseScope.launch { LocalUserRepository.addForObserve(committee.committeeMemberAccount) }
                toast(context.getString(R.string.account_observe_tip))
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_copy_account_name)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_ACCOUNT_NAME, committee.committeeMemberAccount.name)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_transfer)
            icon = R.drawable.ic_cell_transfer.contextDrawable()
            doOnClick {
                startTransferTo(committee.committeeMemberAccount)
                dismiss()
            }
            isVisible = false
            LocalUserRepository.decryptCurrentUserOnly(globalWalletManager).observe(lifecycleOwner) {
                isVisible = it != null && it.uid != committee.committeeMemberAccount.uid
            }
        }
        cell {
            text = context.getString(R.string.option_goto_website)
            subtext = committee.url.toString()
            subtextView.startScrolling()
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            isVisible = committee.url.isAbsolute
            doOnClick {
                startUriBrowser(committee.url)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.option_share)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
            }
        }
    }
}

fun Union.showWorkerBrowserDialog(worker: WorkerObject) = showBottomDialog {
    title = context.getString(R.string.worker_dialog_title)
    subtitle = worker.name.toUpperCase(Locale.ROOT)
    section {
        cell {
            text = context.getString(R.string.option_worker_detail)
            icon = R.drawable.ic_cell_membership.contextDrawable()
            doOnClick {
            }
        }
        cell {
            text = context.getString(R.string.worker_option_copy_worker_name)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
                setClipboardToast(IntentParameters.KEY_WORKER_NAME, worker.name)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.option_goto_website)
            subtext = worker.url.toString()
            subtextView.startScrolling()
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            isVisible = worker.url.isAbsolute
            doOnClick {
                startUriBrowser(worker.url)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.option_share)
            icon = R.drawable.ic_tab_cloud_mode.contextDrawable()
            doOnClick {
            }
        }
    }
}

fun Union.showAccountBalanceBrowserDialog(accountBalance: AccountBalanceObject) = showBottomDialog {
    title = context.getString(R.string.account_balance_dialog_title)
    subtitle = createAssetName(accountBalance.asset)
    section {
        cell {
            text = context.getString(R.string.option_asset_detail)
            icon = R.drawable.ic_cell_membership.contextDrawable()
            doOnClick {
                startAssetBrowser(accountBalance.asset.uid)
                dismiss()
            }
        }
        cell {
            text = context.getString(R.string.account_option_transfer)
            icon = R.drawable.ic_cell_transfer.contextDrawable()
            doOnClick {
                startTransferBalance(accountBalance)
                dismiss()
            }
            LocalUserRepository.decryptCurrentUserOnly(globalWalletManager).observe(lifecycleOwner) {
                isVisible = it != null && it.uid == accountBalance.ownerUid
            }
        }
        cell {
            text = "Borrow"
            icon = R.drawable.ic_cell_collateral.contextDrawable()
            isVisible = accountBalance.asset.isSmartcoin()
            doOnClick {
                startCollateral(accountBalance.ownerUid, accountBalance.assetUid)
                dismiss()
            }
        }
    }
}

fun Union.showOperationBrowserDialog(op: Operation) = showBottomDialog {
    title = context.getString(R.string.operation_dialog_title)
    subtitle = context.getString(operationNameStringResMap.getValue(op.operationType))
    section {
        cell {
            text = context.getString(R.string.option_operation_detail)
            icon = R.drawable.ic_cell_about.contextDrawable()
            doOnClick {
                startOperationBrowser(op)
                dismiss()
            }
        }
        extractOperationComponents(op).forEach {
            when (it) {
                is AccountObject -> cell {
                    icon = R.drawable.ic_cell_about.contextDrawable()
                    text = context.getString(R.string.option_account_detail)
                    subtext = it.name.toUpperCase(Locale.ROOT).ifBlank { it.id }
                    doOnClick {
                        startAccountBrowser(it.uid)
                        dismiss()
                    }
                }
                is AssetObject -> cell {
                    icon = R.drawable.ic_cell_about.contextDrawable()
                    text = context.getString(R.string.option_asset_detail)
                    subtext = it.symbolOrId
                    doOnClick {
                        startAssetBrowser(it.uid)
                        dismiss()
                    }
                }
            }
        }
    }
}

