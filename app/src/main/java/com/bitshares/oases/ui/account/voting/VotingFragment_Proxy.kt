package com.bitshares.oases.ui.account.voting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAccountPicker
import modulon.component.IconSize
import modulon.component.buttonStyle
import modulon.component.toggleEnd
import modulon.extensions.view.doOnClick
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.layout.recycler.section

class VotingFragment_Proxy : ContainerFragment() {

    private val viewModel: VotingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                header = context.getString(R.string.voting_proxy_title)
                cell {
                    toggleEnd {
                        viewModel.proxyEnabled.observe(viewLifecycleOwner) {
                            // TODO: 23/8/2021 catch
                            setChecked(it, false)
                            subtextView.isVisible = it
                        }
                    }
                    text = context.getString(R.string.voting_use_proxy)
                    viewModel.proxyToDetailed.observe(viewLifecycleOwner) {
                        subtextView.text = context.getString(R.string.voting_proxy_current_proxy, it.name)
                    }
                    doOnClick {
                        if (viewModel.proxyEnabled.value == false) startAccountPicker {
                            if (it != null) viewModel.changeProxy(it)
                        } else viewModel.changeProxy(AccountObject.PROXY_TO_SELF)
                    }
                }
                cell {
                    isVisible = false
                    viewModel.proxyEnabled.observe(viewLifecycleOwner) {
                        isVisible = it
                    }
                    viewModel.proxyToDetailed.observe(viewLifecycleOwner) {
                        if (it != null) {
                            bindAccountV3(it, false, IconSize.COMPONENT_0)
                        }
                        doOnClick { startAccountBrowser(it.uid) }
                    }
                }
                cell {
                    buttonStyle()
                    title = context.getString(R.string.voting_change_proxy)
                    isVisible = false
                    doOnClick {
                        startAccountPicker { if (it != null) viewModel.changeProxy(it) }
                    }
                    viewModel.proxyEnabled.observe(viewLifecycleOwner) { isVisible = it }
                }
            }
            hint {
                text = context.getString(R.string.voting_proxy_tip)
            }
        }
    }
}
