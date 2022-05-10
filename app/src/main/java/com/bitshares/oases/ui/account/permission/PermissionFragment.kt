package com.bitshares.oases.ui.account.permission

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showChangesDiscardDialog
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindAccountAuth
import com.bitshares.oases.extensions.viewbinder.bindPublicKey
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.appbar.subtitle
import modulon.component.appbar.title
import com.bitshares.oases.ui.transaction.bindTransaction
import kotlinx.coroutines.launch
import modulon.dialog.button
import modulon.dialog.doOnDismiss
import modulon.dialog.resumeWith
import modulon.dialog.section
import modulon.extensions.compat.*
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.extensions.viewbinder.verticalLayout
import java.util.*

class PermissionFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        CLOUD_WALLET(R.string.permission_settings_tab_cloud),
        OWNER(R.string.permission_settings_tab_owner),
        ACTIVE(R.string.permission_settings_tab_active),
        MEMO(R.string.permission_settings_tab_memo),
    }

    private val viewModel: PermissionViewModel by activityViewModels()

    override fun onCreateView() {
        secureWindow()
        setupAction {
            title(context.getString(R.string.permission_settings_title))
            websocketStateMenu()
            walletStateMenu()
            broadcastMenu {
                doOnClick {
                    lifecycleScope.launch {
                        if (showThresholdCheckDialog()) showPermissionChangeDialog()
                    }
                }
                viewModel.isModified.distinctUntilChanged().observe(viewLifecycleOwner) {
                    isClickable = it
                    isVisible = it
                }
            }
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    PermissionFragment_Tabs().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                }
                offscreenPageLimit = Tabs.values().size
            }
        }
        
        doOnBackPressed {
            lifecycleScope.launch {
                if (viewModel.isModified()) {
                    if (showChangesDiscardDialog() && showThresholdCheckDialog()) showPermissionChangeDialog()
                } else finishActivity()
            }
            false
        }
    }

    private fun showPermissionChangeDialog() = showBottomDialog {
        bindTransaction(viewModel.buildTransaction(), viewModel)
        section {
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.transaction_creator)
                viewModel.operation.map { it.account }.observe(viewLifecycleOwner) {
                    subtitle = createAccountSpan(it)
                }
            }
            verticalLayout {
                viewModel.operation.map { it.owner.field }.distinctUntilChanged().filterNotNull().observe(viewLifecycleOwner) {
                    removeAllViews()
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.permission_owner_threshold)
                        subtitle = it.weightThreshold.toString()
                    }
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.permission_owner_authority)
                    }
                    it.keyAuths.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindPublicKey(it.key, it.value)
                        }
                    }
                    it.accountAuths.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountAuth(it.key, it.value)
                        }
                    }
                }
            }
            verticalLayout {
                viewModel.operation.map { it.active.field }.distinctUntilChanged().filterNotNull().observe(viewLifecycleOwner) {
                    removeAllViews()
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.permission_active_threshold)
                        subtitle = it.weightThreshold.toString()
                    }
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.permission_active_authority)
                    }
                    it.keyAuths.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindPublicKey(it.key, it.value)
                        }
                    }
                    it.accountAuths.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountAuth(it.key, it.value)
                        }
                    }
                }
            }
            verticalLayout {
                viewModel.operation.map { it.options.field }.distinctUntilChanged().filterNotNull().observe(viewLifecycleOwner) {
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.permission_memo_authority)
                    }
                    cell {
                        updatePaddingVerticalV6()
                        isVisible = false
                        isVisible = true
                        bindPublicKey(it.memoKey)
                    }
                }
            }
            feeCell(union, viewModel.transactionBuilder)
        }
    }

    private suspend fun showThresholdCheckDialog() = if (viewModel.checkSufficient()) true else showBooleanSuspendedBottomDialog {
        title = context.getString(R.string.permission_invalid_weight_title)
        isCancelableByButtons = true
        message = when {
            viewModel.isOwnerSufficient.value != true -> {
                val threshold = viewModel.ownerThreshold.value ?: 0U
                val weight = viewModel.ownerThresholdChanged.value?.let { it.second ?: it.first } ?: 0U
                context.getString(R.string.permission_owner_threshold_warning, threshold.toString(), weight.toString())
            }
            viewModel.isActiveSufficient.value != true -> {
                val threshold = viewModel.activeThreshold.value ?: 0U
                val weight = viewModel.activeThresholdChanged.value?.let { it.second ?: it.first } ?: 0U
                context.getString(R.string.permission_active_threshold_warning, threshold.toString(), weight.toString())
            }
            else -> context.getString(R.string.permission_memo_threshold_warning)
        }
        button { text = context.getString(R.string.button_confirm) }
        doOnDismiss { resumeWith(false) }
    }

}