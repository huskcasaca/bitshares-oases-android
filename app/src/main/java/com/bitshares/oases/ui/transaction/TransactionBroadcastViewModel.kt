package com.bitshares.oases.ui.transaction

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import bitshareskit.extensions.isValid
import bitshareskit.objects.AccountObject
import bitshareskit.operations.LimitOrderCancelOperation
import bitshareskit.operations.Operation
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.ui.account.AccountViewModel
import modulon.extensions.livedata.filterNotNull
import modulon.extensions.livedata.map
import modulon.extensions.livedata.withDefault

open class TransactionBroadcastViewModel(application: Application) : AccountViewModel(application) {

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operations = transaction.map { it.operations }
    val operationCreator = operations.map { it.firstOrNull() }.filterNotNull().map(viewModelScope) { AccountRepository.getAccountDetail(getOperationCreator(it)) }
    val isOperationValid = operations.map { it.all { it.isValid() } }.withDefault { false }

    fun buildTransaction(operation: Operation) = buildTransaction(listOf(operation))
    fun buildTransaction(operations: List<Operation>) = buildTransaction {
        if (operations.isNotEmpty()) setAccountUid(getOperationCreator(operations.first()).uid)
        operations.forEach(::addOperation)
        transactionBuilder.value = this
        checkFees()
    }

    fun getOperationCreator(operation: Operation): AccountObject {
        return when (operation) {
            is LimitOrderCancelOperation -> operation.account
            else -> throw IllegalArgumentException("${operation::class} creator is not defined")
        }
    }


}