package com.bitshares.android.ui.account.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.operations.Operation
import com.bitshares.android.chain.IntentParameters
import com.bitshares.android.extensions.compat.showOperationBrowserDialog
import com.bitshares.android.extensions.viewbinder.bindOperation
import com.bitshares.android.extensions.viewbinder.logo
import com.bitshares.android.ui.account.AccountViewModel
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import modulon.component.ComponentCell
import modulon.extensions.content.optBoolean
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class AccountBrowserFragment_Activity : ContainerFragment() {

    private val isPicker by lazy { arguments.optBoolean(IntentParameters.KEY_IS_PICKER, false) }

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    viewModel.activities.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                viewModel.activities.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }

    }
}