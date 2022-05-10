package com.bitshares.oases.ui.block

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.operations.Operation
import com.bitshares.oases.extensions.compat.showOperationBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindOperation
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.cell.ComponentCell
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.lazy.*

class BlockBrowserFragment_Transactions : ContainerFragment() {

    private val viewModel: BlockViewModel by activityViewModels()

    override fun onCreateView() {

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
                    viewModel.ops.observe(viewLifecycleOwner) { submitList(it) }
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
