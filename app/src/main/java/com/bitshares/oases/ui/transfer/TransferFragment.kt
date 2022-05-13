package com.bitshares.oases.ui.transfer

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.formatAssetBigDecimal
import com.bitshares.oases.R
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.bindNothing
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAccountPicker
import com.bitshares.oases.ui.base.suspendAccountBalanceIdPicker
import com.bitshares.oases.ui.transaction.bindTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.component.cell.isButtonEnabled
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.font.typefaceMonoRegular
import modulon.extensions.livedata.observeNonNull
import modulon.extensions.text.TABULAR_TRANSFORMATION_METHOD
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.component.appbar.subtitle
import modulon.layout.lazy.construct
import modulon.layout.lazy.section
import modulon.union.Union
import java.util.*

class TransferFragment : ContainerFragment() {

    private val viewModel: TransferViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState(getString(R.string.transfer_title))
            websocketStateMenu()
            walletStateMenu()
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupRecycler {
            section {
                header = context.getString(R.string.transfer_from)
                cell {
                    doOnClick { startAccountPicker { if (it != null) viewModel.setAccountUid(it.uid) } }
                    viewModel.sender.observe(viewLifecycleOwner) {
                        if (it != null) bindAccountV3(it, true, IconSize.COMPONENT_0) else bindNothing()
                    }
                }
                viewModel.sender.observe(viewLifecycleOwner) { isVisible = it != null }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.transfer_set_sender)
                    doOnClick { startAccountPicker { if (it != null) viewModel.setAccountUid(it.uid) } }
                }
                viewModel.sender.observe(viewLifecycleOwner) {
                    isVisible = it == null
                }
            }
            section {
                header = context.getString(R.string.transfer_to)
                cell {
                    doOnClick { startAccountPicker { if (it != null) viewModel.changeReceiver(it) } }
                    viewModel.receiver.observe(viewLifecycleOwner) {
                        if (it != null) bindAccountV3(it, true, IconSize.COMPONENT_0) else bindNothing()
                    }
                }
                viewModel.receiver.observe(viewLifecycleOwner) {
                    isVisible = it != null
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.transfer_set_sender)
                    doOnClick { startAccountPicker { if (it != null) viewModel.setAccountUid(it.uid) } }
                }
                viewModel.sender.observe(viewLifecycleOwner) {
                    isVisible = it == null
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.transfer_set_receiver)
                    doOnClick { startAccountPicker { if (it != null) viewModel.changeReceiver(it) } }
                }
                viewModel.receiver.observe(viewLifecycleOwner) {
        //                        this@addSection.title = if (it == null) EMPTY_SPACE else context.getString(R.string.transfer_to)
                    isVisible = it == null
                }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.transfer_set_asset)
                    subtitleView.doOnClick { viewModel.setFullBalance() }
                    viewModel.sender.observe(viewLifecycleOwner) {
                        doOnClick {
                            if (it != null) lifecycleScope.launch(Dispatchers.Main) {
                                suspendAccountBalanceIdPicker(it.uid)?.let {
                                    viewModel.setBalanceUid(it)
                                }
                            }
                        }
                    }
                }
                viewModel.balance.observe(viewLifecycleOwner) { isVisible = it == null }
            }
            section {
                header = context.getString(R.string.transfer_details)
        //                addBaseTextCell {
        //                    viewModel.balance.observe(viewLifecycleOwner) {
        //                        isVisible = it != null
        //                        if (it != null) {
        //                            bindAccountBalance(it)
        //                        }
        //                    }
        //                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.transfer_asset)
                    viewModel.sender.observe(viewLifecycleOwner) {
//                            if (it != null) doOnClick { startAccountBalanceIdPicker(it.uid) { if (it != null) viewModel.setBalanceUid(it.uid) } }
                        doOnClick {
                            if (it != null) lifecycleScope.launch(Dispatchers.Main) {
                                suspendAccountBalanceIdPicker(it.uid)?.let {
                                    viewModel.setBalanceUid(it)
                                }
                            }
                        }
                    }
                    viewModel.balanceAmount.observe(viewLifecycleOwner) {
                        if (it != null) subtitle = it.asset.symbol
                    }
//                    isExpanded = false
                    isVisible = false
                    viewModel.balance.observe(viewLifecycleOwner) { isVisible = it != null }
                }
                cell {
                    updatePaddingVerticalHalf()
                    //                        title = context.getString(R.string.transfer_amount_total
                    title = "Available"
                    doOnClick { viewModel.setFullBalance() }
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.balanceAmount.observe(viewLifecycleOwner) {
                        subtextView.isClickable = it != null
                        if (it != null) subtitle = "${formatAssetBigDecimal(it).toPlainString()} ${it.asset.symbol}"
                    }
//                    isExpanded = false
                    isVisible = false
                    viewModel.balance.observe(viewLifecycleOwner) { isVisible = it != null }
                }
                cell {
                    updatePaddingVerticalHalf()
                    isVisible = false
                    //                        title = context.getString(R.string.transfer_amount_total
                    title = "Left"
                    doOnClick { viewModel.setFullBalance() }
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    viewModel.leftAmount.observe(viewLifecycleOwner) {
                        if (it != null) {
                            subtitle = "${formatAssetBigDecimal(it).toPlainString()} ${it.asset.symbol}"
                            subtitleView.textColor = if (it.amount < 0) context.getColor(R.color.component_error) else context.getColor(modulon.R.color.cell_text_secondary)
                        }
                        isVisible = it != null
                    }
//                    isExpanded = false
                    isVisible = false
                    viewModel.balance.observe(viewLifecycleOwner) { isVisible = it != null }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.transfer_amount)
                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
                    customHorizontal {
                        field {
                            inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
                            doAfterTextChanged { viewModel.changeAmountField(it.toStringOrEmpty()) }
                            viewModel.sendFieldNotice.observe(viewLifecycleOwner) { fieldtext = it }
                        }
                        text {
                            viewModel.balanceAmount.observe(viewLifecycleOwner) {
                                if (it != null) text = it.asset.symbol
                            }
                        }
                    }
                    viewModel.sender.observe(viewLifecycleOwner) {
                        doOnClick {
                            if (it != null) lifecycleScope.launch {
                                suspendAccountBalanceIdPicker(it.uid)?.let { viewModel.setBalanceUid(it) }
                            }
                        }
                    }
                    //                        viewModel.leftAmount.observe(viewLifecycleOwner) {
                    //                            subtitle = if (it == null) "" else "${it.values} ${context.getString(R.string.transfer_amount_left)}"
                    //                            isError = it != null && it.amount < 0
                    //                        }
//                    isExpanded = false
                    isVisible = false
                    viewModel.balance.observe(viewLifecycleOwner) { isVisible = it != null }
                }
                // TODO: 25/1/2022 remove expandable
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.transfer_memo)
                    field {
                        hint = context.getString(R.string.transfer_memo_hint)
                        doAfterTextChanged { viewModel.changeMemo(it.toStringOrEmpty()) }
                    }
//                    isExpanded = false
                    isVisible = false
                    viewModel.isMemoAuthorizedLive.observe(viewLifecycleOwner) { isVisible = it }
                }
                isVisible = false
                viewModel.balance.observe(viewLifecycleOwner) { isVisible = it != null }
            }
//            addSpacedSection {
//                addBaseTitleTextCell {
//                    stripVerticalPaddingHalf()
//                    title = context.getString(R.string.transfer_asset)
//                    subtitleView.transformationMethod = TABULAR_TRANSFORMATION_METHOD
//                    subtitleView.doOnClick { viewModel.setFullBalance() }
//                    viewModel.sender.observe(viewLifecycleOwner) {
//                        if (it != null) doOnClick { startAccountBalanceIdPicker(it.uid) { viewModel.setBalanceUid(it.uid) } }
//                    }
//                    viewModel.balanceAmount.observe(viewLifecycleOwner) {
//                        subtextView.isClickable = it != null
//                        if (it != null) {
//                            text = it.asset.symbol
//                            subtitle = "${formatAssetBigDecimal(it).toPlainString()} ${context.getString(R.string.transfer_amount_total)}"
//                        }
//                    }
//                }
//
//            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.transfer_send)
                    isClickable = false
                    isButtonEnabled = false
                    doOnClick { showTransferDialog() }
                    viewModel.canBroadcast.observe(viewLifecycleOwner) {
                        isClickable = it
                        isButtonEnabled = it
                    }
                }
            }
            logo()
        }
    }
}

fun Union.showTransferDialog() = showBottomDialog {
    val viewModel: TransferViewModel by activityViewModels()
    bindTransaction(viewModel.buildTransaction(), viewModel)
    subtitle = context.getString(R.string.operation_type_transfer)
    section {
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.transfer_from)
            viewModel.sender.observeNonNull(viewLifecycleOwner) {
                subtitle = createAccountSpan(it)
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.transfer_to)
            viewModel.receiver.observeNonNull(viewLifecycleOwner) {
                subtitle = createAccountSpan(it)
            }
        }
        cell {
            updatePaddingVerticalV6()
            title = context.getString(R.string.transfer_amount)
            viewModel.sendAmount.observeNonNull(viewLifecycleOwner) {
                subtitle = it.toString()
            }
        }
        cell {
            updatePaddingVerticalV6()
            isVisible = viewModel.memoField.isNotEmpty()
            title = context.getString(R.string.transfer_memo)
            subtitleView.typeface = typefaceMonoRegular
            subtitleView.isSingleLine = false
            subtitle = viewModel.memoField
        }
        feeCell(union, viewModel.transactionBuilder)
    }
}