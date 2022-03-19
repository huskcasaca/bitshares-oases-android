package com.bitshares.oases.ui.account.importer

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.accountNameFilter
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.ui.account.importer.ImportViewModel.State
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.wallet.startWalletRestore
import com.bitshares.oases.ui.wallet.startWalletUnlock
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.component.*
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.compat.activity
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.appendTag
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.isTextError
import modulon.layout.recycler.construct
import modulon.layout.recycler.data
import modulon.layout.recycler.list
import modulon.layout.recycler.section

class ImportFragment_LoginTabs : ContainerFragment() {

    private val tab by lazy { requireArguments().getSerializable(IntentParameters.KEY_TAB_TYPE) as ImportFragment.Tabs }
    private val viewModel: ImportViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                header = "Account to Import"
                list<ComponentCell, AccountObject> {
                    construct { updatePaddingVerticalHalf() }
                    data { bindAccountV3(it, true, IconSize.COMPONENT_0) }
                    viewModel.accountList.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
                }
                viewModel.accountList.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                header = "Login"
                when (tab) {
                    ImportFragment.Tabs.CLOUD -> {
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.import_account)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                isSingleLine = false
                                typeface = typefaceMonoRegular
                                filters = arrayOf(accountNameFilter)
                                doAfterTextChanged { viewModel.changeAccountText(it) }
                                showSoftKeyboard()
                            }
                            viewModel.checkingState.observe(viewLifecycleOwner) { state ->
//                                isError = state == State.INVALID_NAME || state == State.NETWORK_ERROR
                                subtitle = when (state) {
                                    State.EMPTY -> EMPTY_SPACE
                                    State.CHECKING -> context.getString(R.string.import_checking)
                                    State.NETWORK_ERROR -> context.getString(R.string.import_network_error)
                                    State.INVALID_NAME -> context.getString(R.string.import_invalid_account_name_or_id)
                                    State.INVALID_SECRET -> EMPTY_SPACE
                                    State.COMPLETE -> EMPTY_SPACE
                                }
                            }
//                            fieldtextView.showSoftKeyboard()
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(com.bitshares.oases.R.string.import_password)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_FILTER or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                typeface = typefaceMonoRegular
                                isSingleLine = false
                                doAfterTextChanged { viewModel.changePasswordText(it) }
                                viewModel.isObserveModeEnabled.observe(viewLifecycleOwner) { isVisible = !it }
                            }
//                            viewModel.checkingState.observe(viewLifecycleOwner) { state ->
//                                subtitle = when (state) {
//                                    State.EMPTY -> EMPTY_SPACE
//                                    State.CHECKING -> EMPTY_SPACE
//                                    State.NETWORK_ERROR -> EMPTY_SPACE
//                                    State.INVALID_NAME -> EMPTY_SPACE
//                                    State.INVALID_SECRET -> context.getString(R.string.import_invalid_password)
//                                    State.COMPLETE -> EMPTY_SPACE
//                                }
//                            }
                            combineNonNull(viewModel.checkingState, viewModel.isObserveModeEnabled).observe(viewLifecycleOwner) { (state, mode) ->
//                                isError = state == State.INVALID_SECRET
                                subtitle = if (mode) "Disabled" else when (state) {
                                    State.EMPTY -> EMPTY_SPACE
                                    State.CHECKING -> EMPTY_SPACE
                                    State.NETWORK_ERROR -> EMPTY_SPACE
                                    State.INVALID_NAME -> EMPTY_SPACE
                                    State.INVALID_SECRET -> context.getString(R.string.import_invalid_password)
                                    State.COMPLETE -> EMPTY_SPACE
                                }
                            }
                            viewModel.cloudPermissions.observe(viewLifecycleOwner) {
                                if (viewModel.checkingState.value == com.bitshares.oases.ui.account.importer.ImportViewModel.State.COMPLETE) {
                                    subtitle = buildContextSpannedString {
                                        if (it.contains(Authority.OWNER)) appendTag(context.getString(com.bitshares.oases.R.string.tag_owner).uppercase(), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.ACTIVE)) appendTag(context.getString(R.string.tag_active).uppercase(), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.MEMO)) appendTag(context.getString(R.string.tag_memo).uppercase(), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                    }
                                }
                            }
//                            viewModel.isObserveModeEnabled.observe(viewLifecycleOwner) { isExpanded = !it }
                        }
                        cell {
                            updatePaddingVerticalHalf()
                            toggleEnd {
                                viewModel.isObserveModeEnabled.observe(viewLifecycleOwner) { setChecked(it, true) }
                            }
                            text = context.getString(R.string.import_observer_mode)
                            subtext = context.getString(R.string.import_observer_mode_description)
                            doOnClick {
                                viewModel.switchObserveMode(!viewModel.isObserveModeEnabled.value)
                            }
                        }
                    }
                    ImportFragment.Tabs.BRAIN -> {
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.import_brain_key)
                            field {
//                            typeface = typefaceMonoRegular
                                isSingleLine = false
                                doAfterTextChanged { viewModel.changeBrainText(it) }
                            }
                            viewModel.checkingState.observe(viewLifecycleOwner) { state ->
//                                isError = state == State.INVALID_NAME || state == State.INVALID_SECRET || state == State.NETWORK_ERROR
                                subtitle = when (state) {
                                    State.EMPTY -> EMPTY_SPACE
                                    State.CHECKING -> context.getString(R.string.import_checking)
                                    State.NETWORK_ERROR -> context.getString(R.string.import_network_error)
                                    State.INVALID_NAME -> context.getString(R.string.import_no_matched_account)
                                    State.INVALID_SECRET -> context.getString(R.string.import_invalid_brain_key)
                                    State.COMPLETE -> EMPTY_SPACE
                                }
                            }
                            viewModel.privateKeyPermissions.observe(viewLifecycleOwner) {
                                if (viewModel.checkingState.value == State.COMPLETE) {
                                    subtitle = buildContextSpannedString {
                                        if (it.contains(Authority.OWNER)) appendTag(context.getString(R.string.tag_owner), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.ACTIVE)) appendTag(context.getString(R.string.tag_active), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.MEMO)) appendTag(context.getString(R.string.tag_memo), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                    }
                                }
                            }
                        }
                    }
                    ImportFragment.Tabs.WIF -> {
                        cell {
                            updatePaddingVerticalHalf()
                            title = context.getString(R.string.import_private_key)
                            field {
                                inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                                typeface = typefaceMonoRegular
                                isSingleLine = false
                                doAfterTextChanged { viewModel.changePrivateText(it) }
                            }
                            viewModel.checkingState.observe(viewLifecycleOwner) { state ->
                                subtitleView.isTextError = state == State.INVALID_NAME || state == State.INVALID_SECRET || state == State.NETWORK_ERROR
                                subtitle = when (state) {
                                    State.EMPTY -> EMPTY_SPACE
                                    State.CHECKING -> context.getString(R.string.import_checking)
                                    State.NETWORK_ERROR -> context.getString(R.string.import_network_error)
                                    State.INVALID_NAME -> context.getString(R.string.import_no_matched_account)
                                    State.INVALID_SECRET -> context.getString(R.string.import_invalid_private_key)
                                    State.COMPLETE -> EMPTY_SPACE
                                }
                            }
                            viewModel.privateKeyPermissions.observe(viewLifecycleOwner) {
                                if (viewModel.checkingState.value == State.COMPLETE) {
                                    subtitle = buildContextSpannedString {
                                        if (it.contains(Authority.OWNER)) appendTag(context.getString(R.string.tag_owner), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.ACTIVE)) appendTag(context.getString(R.string.tag_active), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                        if (it.contains(Authority.MEMO)) appendTag(context.getString(R.string.tag_memo), context.getColor(R.color.tag_default), context.getColor(R.color.cell_text_secondary))
                                    }
                                }
                            }
                        }
                    }
                    ImportFragment.Tabs.BIN -> {
                    }
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.import_import_button)
                    NetworkService.isConnectedLive.observe(viewLifecycleOwner) {
                        isClickable = it
                        isButtonEnabled = it
                        doOnClick { lifecycleScope.launch { if (tab == ImportFragment.Tabs.BIN) startWalletRestore() else if (viewModel.checkForImport() && startWalletUnlock() && viewModel.import()) activity.finish() } }
                    }
                }
            }
            logo()
        }
    }

}