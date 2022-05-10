package com.bitshares.oases.ui.raw_data

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.extensions.viewbinder.bindRawData
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.compat.setClipboardToast
import modulon.extensions.view.doOnLongClick
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section

class JsonRawDataFragment : ContainerFragment() {

    private val viewModel: JsonRawDataViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                cell {
                    viewModel.element.observe(viewLifecycleOwner) {
                        if (it != null) bindRawData(it)
                        viewModel.element.observe(viewLifecycleOwner) {
                            doOnLongClick { setClipboardToast("raw_json", it.toString()) }
                        }
                    }
                }
            }
            logo()
        }
    }

}