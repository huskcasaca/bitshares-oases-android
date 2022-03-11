package com.bitshares.oases.ui.block

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.operations.Operation
import com.bitshares.oases.extensions.compat.showOperationBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindOperation
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.ComponentCell
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class BlockBrowserFragment_Transactions : ContainerFragment() {

    private val viewModel: BlockViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    viewModel.ops.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                // TODO: 24/10/2021 bindTransaction()
//                addRecyclerGroup<BaseLinkedCell, Transaction> {
//                    initView {
//                        stripVerticalPaddingHalf()
//                    }
//                    bindData {
//                        bindTransaction(it)
//                    }
//                    distinctItemsBy { it }
//                    viewModel.tx.observe(viewLifecycleOwner) {
//                        dispatchUpdates(it)
//                    }
//                }
                isVisible = false
                viewModel.tx.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }

}
