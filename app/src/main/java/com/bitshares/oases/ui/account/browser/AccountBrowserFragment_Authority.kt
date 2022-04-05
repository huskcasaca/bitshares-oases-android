package com.bitshares.oases.ui.account.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.Clipboard.LABEL_PRIVATE_KEY
import com.bitshares.oases.extensions.compat.showAccountBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindAccountAuth
import com.bitshares.oases.extensions.viewbinder.bindPublicKey
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import bitshareskit.chain.Authority
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.extensions.compat.setClipboardToast
import modulon.extensions.livedata.emptyLiveData
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.construct
import modulon.layout.recycler.data
import modulon.layout.recycler.list
import modulon.layout.recycler.section

class AccountBrowserFragment_Authority : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler {
            Authority.values().forEach { permission ->
                val source = viewModel.account.map {
                    when (permission) {
                        Authority.OWNER -> it?.ownerKeyAuths?.isNotEmpty() == true || it?.ownerAccountAuths?.isNotEmpty() == true
                        Authority.ACTIVE -> it?.activeKeyAuths?.isNotEmpty() == true || it?.activeAccountAuths?.isNotEmpty() == true
                        Authority.MEMO -> it?.memoKeyAuths?.isNotEmpty() == true
                    }
                }
                section {
                    header = when (permission) {
                        Authority.OWNER -> context.getString(R.string.permission_settings_owner_keys)
                        Authority.ACTIVE -> context.getString(R.string.permission_settings_active_keys)
                        Authority.MEMO -> context.getString(R.string.permission_settings_memo_keys)
                    }
                    cell {
                        updatePaddingVerticalHalf()
                        title = context.getString(R.string.permission_settings_threshold)
//                        subtext = when (permission) {
//                            Authority.OWNER -> context.getString(R.string.permission_settings_owner_threshold_hint)
//                            Authority.ACTIVE -> context.getString(R.string.permission_settings_active_threshold_hint)
//                            Authority.MEMO -> context.getString(R.string.permission_settings_memo_threshold_hint)
//                        }
                        viewModel.account.observe(viewLifecycleOwner) {
                            subtitle = when (permission) {
                                Authority.OWNER -> it?.ownerMinThreshold
                                Authority.ACTIVE -> it?.activeMinThreshold
                                Authority.MEMO -> it?.memoMinThreshold
                            }.toString()
                        }
                        source.observe(viewLifecycleOwner) { isVisible = it }
                    }
                    list<ComponentCell, Pair<PublicKey, UShort>> {
                        construct {
                            updatePaddingVerticalHalf()
                        }
                        data { (key, threshold) ->
                            bindPublicKey(key, threshold)
                            doOnLongClick {
                                setClipboardToast(LABEL_PRIVATE_KEY, key.address)
                            }
                        }
                        viewModel.account.observe(viewLifecycleOwner) {
                            val list = when (permission) {
                                Authority.OWNER -> it?.ownerKeyAuths
                                Authority.ACTIVE -> it?.activeKeyAuths
                                Authority.MEMO -> it?.memoKeyAuths
                            }.orEmpty().toList()
                            adapter.submitList(list)
                        }
                    }
                    list<ComponentCell, Pair<AccountObject, UShort>> {
                        construct {
                            iconSize = IconSize.NORMAL
                            updatePaddingVerticalHalf()
                        }

                        data { (account, threshold) ->
                            bindAccountAuth(account, threshold)
                            doOnLongClick {
                                showAccountBrowserDialog(account)
                            }
                        }
                        when (permission) {
                            Authority.OWNER -> viewModel.ownerAccountAuths
                            Authority.ACTIVE -> viewModel.activeAccountAuths
                            Authority.MEMO -> emptyLiveData()
                        }.observe(viewLifecycleOwner)  { adapter.submitList(it.toList()) }
                    }
                    source.observe(viewLifecycleOwner) { isVisible = it }
                }
            }
            logo()
        }
    }
}