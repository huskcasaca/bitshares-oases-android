package com.bitshares.oases.ui.account.picker

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountBalanceObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountBalance
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import modulon.component.cell.ComponentCell
import modulon.extensions.compat.finishActivity
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.component.appbar.subtitle
import modulon.layout.lazy.*
import java.util.*

class AccountBalancePickerFragment : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {
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
                                finishActivity { putJson(IntentParameters.AccountBalance.KEY_UID, it.uid) }
                            } else startAssetBrowser(it.assetUid)
                        }
                        doOnLongClick { showAccountBalanceBrowserDialog(it) }
                    }
                    distinctItemsBy { it.asset.uid }
                    distinctContentBy { it.balance }
                    viewModel.accountBalance.observe(viewLifecycleOwner) { submitList(it) }
                }
                viewModel.accountBalance.observe(viewLifecycleOwner) {
                    isVisible = it.isNotEmpty()
                }
            }
        }

    }

}