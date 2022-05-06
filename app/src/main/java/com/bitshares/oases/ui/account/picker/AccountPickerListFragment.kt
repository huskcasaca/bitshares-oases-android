package com.bitshares.oases.ui.account.picker

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.ui.base.ContainerFragment

class AccountPickerListFragment : ContainerFragment() {

    private val viewModel: AccountPickerViewModel by activityViewModels()
    private val tab by lazy { requireArguments().get(IntentParameters.KEY_TAB_TYPE) as AccountPickerFragment.Tabs }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setupRecycler {
//            when (tab) {
//                AccountPickerFragment.Tabs.HISTORY -> {
//                    addViewList<AvatarComponentCell, AccountObject> {
//                        initView { stripVerticalPaddingHalf() }
//                        bindData {
//                            bindAccount(it, false)
//                            doOnClick {
//                                setActivityResult(Activity.RESULT_OK) { putLocalExtra(IntentParameters.Account.KEY_ACCOUNT, it) }
//                                finishActivity()
//                            }
//                        }
//                        viewModel.historyAccounts.observe(viewLifecycleOwner) { submitList(it) }
//                    }
//                    addBaseShadowCell { viewModel.historyAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() } }
//                    addBaseTextCell {
//                        text = context.getString(R.string.account_picker_clear_history)
//                        textColor = context.getColor(R.color.component)
//                        subtitleView.typeface = FontBook.Inter.BOLD
//                        doOnClick { viewModel.clearSearchHistory() }
//                    }
//                }
//                AccountPickerFragment.Tabs.LOCAL -> {
//                    addViewList<AvatarComponentCell, User> {
//                        initView { stripVerticalPaddingHalf() }
//                        bindData {
//                            bindUser(it)
//                            doOnClick {
//                                setActivityResult(Activity.RESULT_OK) { putLocalExtra(IntentParameters.Account.KEY_ACCOUNT, it.toAccount()) }
//                                finishActivity()
//                            }
//                        }
//                        viewModel.localAccounts.observe(viewLifecycleOwner) { submitList(it) }
//                    }
//                    addBaseShadowCell { viewModel.localAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() } }
//                }
//                AccountPickerFragment.Tabs.WHITELIST -> {
//                    addViewList<AvatarComponentCell, AccountObject> {
//                        initView { stripVerticalPaddingHalf() }
//                        bindData {
//                            bindAccount(it, false)
//                            doOnClick {
//                                setActivityResult(Activity.RESULT_OK) { putLocalExtra(IntentParameters.Account.KEY_ACCOUNT, it) }
//                                finishActivity()
//                            }
//                        }
//                        viewModel.whitelistAccounts.observe(viewLifecycleOwner) { submitList(it) }
//                    }
//                    addBaseShadowCell { viewModel.whitelistAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() } }
//                }
//            }
//        }
//    }

}