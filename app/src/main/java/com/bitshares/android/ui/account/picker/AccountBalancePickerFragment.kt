package com.bitshares.android.ui.account.picker

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountBalanceObject
import com.bitshares.android.R
import com.bitshares.android.chain.IntentParameters
import com.bitshares.android.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.android.extensions.compat.startAssetBrowser
import com.bitshares.android.extensions.viewbinder.bindAccountBalance
import com.bitshares.android.ui.account.AccountViewModel
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.putJson
import modulon.component.ComponentCell
import modulon.extensions.compat.finish
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.layout.actionbar.subtitle
import modulon.layout.recycler.*
import java.util.*

class AccountBalancePickerFragment : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState(context.getString(R.string.account_balance_title))
            viewModel.isPicker = true
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupRecycler {
            section {
                list<ComponentCell, AccountBalanceObject> {
                    data {
                        bindAccountBalance(it)
                        doOnClick {
                            if (viewModel.isPicker) {
                                finish { putJson(IntentParameters.AccountBalance.KEY_UID, it.uid) }
                            } else startAssetBrowser(it.assetUid)
                        }
                        doOnLongClick { showAccountBalanceBrowserDialog(it) }
                    }
                    distinctItemsBy { it.asset.uid }
                    distinctContentBy { it.balance }
                    viewModel.accountBalance.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                viewModel.accountBalance.observe(viewLifecycleOwner) {
                    isVisible = it.isNotEmpty()
                }
            }
        }

    }

}