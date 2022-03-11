package com.bitshares.oases.ui.account.membership

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import bitshareskit.operations.AccountUpgradeOperation
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.ui.account.AccountViewModel
import modulon.extensions.livedata.filterNotNull

class MembershipViewModel(application: Application) : AccountViewModel(application) {

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as AccountUpgradeOperation? }.filterNotNull()

    fun buildTransaction(): TransactionBuilder {
        return buildTransaction {
            addOperation {
                val account = account.value!!
                AccountUpgradeOperation(account, true)
            }
            transactionBuilder.value = this
            checkFees()
        }
    }

}