package com.bitshares.oases.ui.transfer

import android.app.Application
import android.content.Intent
import androidx.lifecycle.*
import bitshareskit.chain.ChainConfig.EMPTY_INSTANCE
import bitshareskit.extensions.*
import bitshareskit.models.AssetAmount
import bitshareskit.models.Memo
import bitshareskit.objects.AccountObject
import bitshareskit.operations.TransferOperation
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.resolveAccountPath
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.netowrk.java_websocket.TransactionBuilder
import com.bitshares.oases.netowrk.java_websocket.buildTransaction
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.getJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.livedata.*
import java.math.BigDecimal

class TransferViewModel(application: Application) : AccountViewModel(application) {

    private val balanceUID = NonNullMutableLiveData(EMPTY_INSTANCE)
    private val receiverUID = NonNullMutableLiveData(EMPTY_INSTANCE)

    private val senderInternal = account
    private val receiverInternal = receiverUID.switchMap { AccountRepository.getAccountLive(it) }

    val balance = combineNonNull(accountBalance, balanceUID) { balances, uid ->
        balances.find { it.uid == uid }
    }

    val balanceAmount = combineNonNull(accountBalance, balanceUID) { balances, uid ->
        balances.find { it.uid == uid }?.balanceAmount
    }
    val balanceAsset = balanceAmount.map { it?.asset }

    val sender = senderInternal.distinctUntilChangedBy { it?.nameOrId }
    val receiver = receiverInternal.distinctUntilChangedBy { it?.nameOrId }

    val sendFieldDec = NonNullMediatorLiveData(BigDecimal.ZERO)
    val sendFieldNotice = MutableLiveData<String>()

    val sendAmount = combineFirstOrNull(sendFieldDec, balanceAsset) { dec, asset -> formatAssetAmount(dec, asset) }

    val leftAmount = combineFirstOrNull(balanceAmount, sendAmount) { balance, send -> balance - send }

    var memoField = modulon.extensions.charset.EMPTY_SPACE

    val isBalanceEnough = leftAmount.map { it != null && it.amount >= 0 }.withDefault { false }

    fun changeReceiver(receiver: AccountObject?) {
        if (receiver != null) setReceiverUid(receiver.uid)
    }

    fun setReceiverUid(uid: Long) {
        if (uid != sender.value?.uid) {
            receiverUID.value = uid
        }
    }

    fun setBalanceUid(uid: Long) {
        balanceUID.value = uid
    }

    fun changeAmountField(amount: String) {
        sendFieldDec.value = amount.toBigDecimalOrNull()?.stripTrailingZerosFixes() ?: BigDecimal.ZERO
    }

    fun changeMemo(memo: String) {
        memoField = memo
    }

    fun setFullBalance() {
        val balance = balanceAmount.value
        if (balance != null) {
            sendFieldNotice.value = ChainPropertyRepository.getFeeReservedAmount(balance).formattedValue.toPlainString()
        }
    }

    val transactionBuilder = MutableLiveData<TransactionBuilder>()
    val transaction = transactionBuilder.map { it.build() }
    val operation = transaction.map { it.operations.firstOrNull() as TransferOperation? }.filterNotNull()

    // TODO: 10/10/2021 normalize
    fun buildTransaction(): TransactionBuilder = buildTransaction {
        addOperation {
            val sender = senderInternal.value!!
            val receiver = receiverInternal.value!!
            val assetAmount = sendAmount.value!!
            val sendAmount = assetAmount.amount
            val sendAsset = assetAmount.asset
            val message = memoField
            val memo = if (message.isNotEmpty()) Memo(sender.options.memoKey, receiver.options.memoKey, message) else null
            memo?.encryptMessage(memoRequiredAuths.value.first())
            TransferOperation(sender, receiver, AssetAmount(sendAmount, sendAsset), memo)
        }
        transactionBuilder.value = this
        checkFees()
    }

    val canBroadcast = combineBooleanAll(
        NetworkService.isConnectedLive,
        isBalanceEnough,
        receiverUID.map { it != EMPTY_INSTANCE }
    ).distinctUntilChanged()

    override fun onActivityIntent(intent: Intent?) {
        intent ?: return
        logcat("onActivityIntent", intent.action)
        when (intent.action) {
            Intent.ACTION_MAIN -> return
            Intent.ACTION_VIEW -> {
                val uri = intent.data?.normalizeScheme()
                if (uri != null) {
                    viewModelScope.launch {
                        val receiverParam = uri.getQueryParameter(IntentParameters.Transfer.KEY_TO)
                        val receiver = resolveAccountPath(receiverParam)
                        withContext(Dispatchers.Main) { changeReceiver(receiver) }
                    }
                    val asset = uri.getQueryParameter(IntentParameters.Transfer.KEY_ASSET)
                    val amount = uri.getQueryParameter(IntentParameters.Transfer.KEY_AMOUNT)
                    val memo = uri.getQueryParameter(IntentParameters.Transfer.KEY_MEMO)
                }
            }
            null -> {
                intent.getJson(IntentParameters.Transfer.KEY_FROM, EMPTY_INSTANCE).let { setAccountUid(it) }
                intent.getJson(IntentParameters.Transfer.KEY_TO, EMPTY_INSTANCE).let { setReceiverUid(it) }
                intent.getJson(IntentParameters.Transfer.KEY_BALANCE, EMPTY_INSTANCE).let { setBalanceUid(it) }
            }
            else -> return
        }

    }

}
