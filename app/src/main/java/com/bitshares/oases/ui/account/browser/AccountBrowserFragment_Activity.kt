package com.bitshares.oases.ui.account.browser

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.operations.Operation
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showOperationBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindOperation
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.cell.ComponentCell
import modulon.extensions.content.optBoolean
import modulon.extensions.stdlib.logcat
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.lazy.*

class AccountBrowserFragment_Activity : ContainerFragment() {

    private val isPicker by lazy { arguments.optBoolean(IntentParameters.KEY_IS_PICKER, false) }

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {
        viewModel.checkAccountHistory()

        setupRecycler {
            section {
                list<ComponentCell, Operation> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data {
                        bindOperation(it)
                        doOnLongClick { showOperationBrowserDialog(it) }
                    }
                    distinctItemsBy { it }
                    viewModel.activities.observe(viewLifecycleOwner) {
                        "submitList(it)".logcat()
                        submitList(it)
                    }
                }
                viewModel.activities.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }

    }
}