package com.bitshares.oases.ui.account.voting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import bitshareskit.extensions.isProxyToSelf
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showChangesDiscardDialog
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindCommitteeSimple
import com.bitshares.oases.extensions.viewbinder.bindWitnessSimple
import com.bitshares.oases.extensions.viewbinder.bindWorkerSimple
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.ui.account.permission.PermissionFragment
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.layout.actionbar.subtitle
import com.bitshares.oases.ui.transaction.bindTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.component.IconSize
import modulon.dialog.section
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.arguments
import modulon.extensions.compat.finish
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.extensions.viewbinder.verticalLayout
import modulon.layout.actionbar.SearchLayout
import modulon.layout.actionbar.doOnCollapse
import modulon.layout.actionbar.menu
import java.util.*

class VotingFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        PROXY(R.string.voting_tab_proxy),
        COMMITTEE_MEMBER(R.string.voting_tab_committee_member),
        WITNESS(R.string.voting_tab_witness),
        WORKER_PROPOSAL(R.string.voting_tab_worker_proposal),
    }

    private val viewModel: VotingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction {
            titleConnectionState(getString(R.string.voting_title))
            networkStateMenu()
            walletStateMenu()
            menu {
                isVisible = false
                icon = R.drawable.ic_test_search_24.contextDrawable()
                doOnClick {
                    actionView = create<SearchLayout> {
                        queryHint = context.getString(R.string.account_picker_search)
                        fieldtextView.doAfterTextChanged {
                            viewModel.filter.value = it.toStringOrEmpty()
                        }
                        fieldtextView.showSoftKeyboard()
                        isExpanded = true
                    }
                }
                doOnCollapse {
                    viewModel.filter.value = EMPTY_SPACE
                }
                viewModel.viewPage.observe(viewLifecycleOwner) {
                    isExpanded = false
                    isVisible = it != 0
                }
            }
            broadcastMenu {
                doOnClick {
                    showVotingChangeDialog()
                }
                viewModel.isModified.distinctUntilChanged().observe(viewLifecycleOwner) {
                    isClickable = it
                    isVisible = it
                }
            }
            viewModel.accountName.observe(viewLifecycleOwner) {
                subtitle(it.toUpperCase(Locale.ROOT))
            }
            viewModel.refresh()
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    when (it) {
                        Tabs.PROXY -> VotingFragment_Proxy()
                        else -> VotingFragment_Tabs().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                    }
                }
                doOnPageSelected {
                    viewModel.viewPage.value = it
                }
                offscreenPageLimit = PermissionFragment.Tabs.values().size
            }
        }
        doOnBackPressed {
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                if (viewModel.isModified()) {
                    if (showChangesDiscardDialog()) showVotingChangeDialog()
                } else finish()
            }
            false
        }
    }

    private fun showVotingChangeDialog() = showBottomDialog {
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
                viewModel.operation.map { it.options.field }.filterNotNull().observe(viewLifecycleOwner) {
                    removeAllViews()
                    cell {
                        updatePaddingVerticalV6()
                        isVisible = it.vote.isEmpty()
                        title = context.getString(R.string.voting_new_proxy)
                        subtitle = if (it.votingAccount.isProxyToSelf()) context.getString(R.string.voting_no_proxy) else createAccountSpan(it.votingAccount)
                    }
                    viewModel.filterCommitteeList(it.vote).apply {
                        cell {
                            updatePaddingVerticalV6()
                            isVisible = isNotEmpty()
                            title = context.getString(R.string.voting_committee_member)
                        }
                        forEach {
                            cell {
                                updatePaddingVerticalV6()
                                iconSize = IconSize.COMPONENT_0
                                bindCommitteeSimple(it)
                            }
                        }
                    }
                    viewModel.filterWitnessList(it.vote).apply {
                        cell {
                            updatePaddingVerticalV6()
                            isVisible = isNotEmpty()
                            title = context.getString(R.string.voting_witness)
                        }
                        forEach {
                            cell {
                                updatePaddingVerticalV6()
                                iconSize = IconSize.COMPONENT_0
                                bindWitnessSimple(it)
                            }
                        }
                    }
                    viewModel.filterWorkerList(it.vote).apply {
                        cell {
                            updatePaddingVerticalV6()
                            isVisible = isNotEmpty()
                            title = context.getString(R.string.voting_workers)
                        }
                        forEach {
                            cell {
                                updatePaddingVerticalV6()
                                bindWorkerSimple(it)
                            }
                        }
                    }
                }
            }
            feeCell(union, viewModel.transactionBuilder)
        }
    }

}