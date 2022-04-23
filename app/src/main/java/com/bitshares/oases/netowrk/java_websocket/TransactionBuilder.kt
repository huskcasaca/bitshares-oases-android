package com.bitshares.oases.netowrk.java_websocket

import bitshareskit.errors.ErrorCode
import bitshareskit.errors.TransactionBroadcastException
import bitshareskit.models.*
import bitshareskit.objects.AssetObject
import bitshareskit.objects.DynamicGlobalPropertyObject
import bitshareskit.operations.Operation
import com.bitshares.oases.chain.blockchainNetworkScope
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.chain_repo.TransactionRepository
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.NonNullMediatorLiveData
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*

@Deprecated("")
class TransactionBuilder {

    enum class FeeState { EMPTY, CHECKING, COMPLETE, INSUFFICIENT }

    val feeState = NonNullMediatorLiveData(FeeState.EMPTY)
    val fee = NonNullMediatorLiveData(AssetAmount.EMPTY)

    private val transaction = Transaction()

    private val privateKeys = mutableSetOf<PrivateKey>()
//    private val ownerKeys = mutableSetOf<PrivateKey>()
//    private val active = mutableSetOf<PrivateKey>()

    var isSuccess = false
        private set

    var isFeeCalculated = false
        private set

    var chainId
        get() = transaction.chainId
        private set(value) {
            transaction.chainId = value
        }

    var isBroadcast = false
        private set

    val operationTypes
        get() = transaction.operations.map { it.operationType }.toSet()

    var feeAsset: AssetObject = Graphene.KEY_CORE_ASSET.value
        set(value) {
            isFeeCalculated = false
            feeState.postValue(FeeState.EMPTY)
            fee.postValue(AssetAmount.EMPTY)
            field = value
        }

    val requiredAuthority get() = transaction.operations.map { it.authority }.toSet()

    fun addOperation(op: Operation) {
        transaction.operations.add(op)
        isFeeCalculated = false
        feeState.value = FeeState.EMPTY
        fee.value = AssetAmount.EMPTY
    }

    fun addKey(key: PrivateKey) {
        if (key.isValid) {
            privateKeys.add(key)
        }
    }

    fun addKeys(keys: Collection<PrivateKey>) {
        privateKeys.addAll(keys.filter { it.isValid })
    }

    fun replaceKeys(keys: Collection<PrivateKey>) {
        privateKeys.clear()
        privateKeys.addAll(keys.filter { it.isValid })
    }

    fun clearOperations() {
        transaction.operations.clear()
        isFeeCalculated = false
        feeState.postValue(FeeState.EMPTY)
        fee.postValue(AssetAmount.EMPTY)
    }

    fun clearKeys() {
        privateKeys.clear()
    }

    fun build() = transaction

    fun checkFees() {
        blockchainNetworkScope.launch {
            calculateFees()
        }
    }

    suspend fun calculateFees(feeAsset: AssetObject = this.feeAsset): List<AssetAmount> {
        withContext(Dispatchers.Main) { feeState.value = FeeState.CHECKING }
        val operations = transaction.operations
        val formatted = operations.map { listOf(it.operationType.ordinal, it.toJsonElement()) }
        val feeList = NetworkService.sendOrNull(CallMethod.GET_REQUIRED_FEES, listOf(formatted, feeAsset.id)) {
            runCatching { (it as JSONArray).map { AssetAmount.fromJson(it as JSONObject) } }.getOrNull()
        }.orEmpty()
        if (feeList.isNotEmpty()) {
            if (!isFeeCalculated && feeAsset == this.feeAsset) {
                transaction.operations.forEachIndexed { index, operation ->
                    transaction.operations[index] = operation.apply { fee = feeList[index] }
                }

            }
            isFeeCalculated = true
            withContext(Dispatchers.Main) {
                feeState.value = FeeState.COMPLETE
                fee.value = AssetAmount(feeList.sumOf { it.amount }, feeList.first().asset)
            }
        } else {
            withContext(Dispatchers.Main) {
                feeState.value = FeeState.EMPTY
                fee.value = AssetAmount.EMPTY
            }
        }
        return feeList
    }

    fun broadcast(test: Boolean = false) {
        startCallbacks.forEach { it.invoke() }
//        startTimeoutTimer()
        blockchainNetworkScope.launch {
            try {
                validate()
                chainId = ChainPropertyRepository.chainId
                transaction.sign(privateKeys)
                if (test) return@launch
                TransactionRepository.broadcastTransactionWithCallback(transaction).collect { result ->
                    withContext(Dispatchers.Main) {
                        when (result) {
                            null -> confirmCallbacks.forEach { it.invoke() }
                            is TransactionBlock -> {
                                successCallbacks.forEach { it.invoke(result) }; isSuccess = true
                            }
                            is ErrorStack -> failureCallbacks.forEach { it.invoke(result.exception) }
                        }
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                withContext(Dispatchers.Main) { failureCallbacks.forEach { it.invoke(e) } }
            }
        }
    }

    private var block
        get() = transaction.block
        set(value) { transaction.block = value }

    fun DynamicGlobalPropertyObject.toReferenceBlock(timeExpSec: Long = Graphene.KEY_MAXIMUM_TIME_UNTIL_EXPIRATION.value.toLong()) = ReferenceBlock(headBlockNumber, headBlockId, Date.from(time.toInstant().plusSeconds(timeExpSec)))


    private suspend fun validate() {
        if (isBroadcast) throw TransactionBroadcastException(ErrorCode.ALREADY_BROADCAST)
        isBroadcast = true
        block = ChainPropertyRepository.getLastDynamicGlobalPropertyCF()?.toReferenceBlock() ?: throw TransactionBroadcastException(ErrorCode.MISSING_HEADER_BLOCK)
        when {
            !NetworkService.isConnected -> throw TransactionBroadcastException(ErrorCode.NO_CONNECTION)
            transaction.operations.isEmpty() -> throw TransactionBroadcastException(ErrorCode.MISSING_OPERATION)
            block == null -> throw TransactionBroadcastException(ErrorCode.MISSING_HEADER_BLOCK)
//            privateKeys.isEmpty() -> throw TransactionBroadcastException(ErrorCode.MISSING_OTHER_AUTH)
            !isFeeCalculated && calculateFees(feeAsset).isEmpty() -> throw TransactionBroadcastException(ErrorCode.FEE_NOT_CALCULATED)
        }
    }

    private val startCallbacks: MutableList<() -> Unit> = mutableListOf()
    private val confirmCallbacks: MutableList<() -> Unit> = mutableListOf()
    private val successCallbacks: MutableList<(TransactionBlock) -> Unit> = mutableListOf()
    private val failureCallbacks: MutableList<(Throwable) -> Unit> = mutableListOf()

    fun TransactionBuilder.onStart(block: () -> Unit) {
        startCallbacks.add(block)
    }

    fun TransactionBuilder.onConfirm(block: () -> Unit) {
        confirmCallbacks.add(block)
    }

    fun TransactionBuilder.onSuccess(block: (TransactionBlock) -> Unit) {
        successCallbacks.add(block)
    }

    fun TransactionBuilder.onFailure(block: (Throwable) -> Unit) {
        failureCallbacks.add(block)
    }

    fun TransactionBuilder.addOperation(block: () -> Operation) {
        runCatching { addOperation(block.invoke()) }.onFailure { it.printStackTrace() }
    }

}

fun buildTransaction(block: TransactionBuilder.() -> Unit): TransactionBuilder {
    return TransactionBuilder().apply(block)
}



