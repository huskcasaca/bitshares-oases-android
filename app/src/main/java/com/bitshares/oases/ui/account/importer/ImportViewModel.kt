package com.bitshares.oases.ui.account.importer

import android.app.Application
import android.text.Editable
import androidx.lifecycle.viewModelScope
import bitshareskit.chain.ChainConfig
import bitshareskit.errors.GrapheneException
import bitshareskit.errors.WebSocketClosedException
import bitshareskit.chain.Authority.*
import bitshareskit.models.PrivateKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.chain.KeyCreator
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.toUser
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel
import bitshareskit.chain.Authority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.coroutine.debounce
import modulon.extensions.livedata.NonNullMediatorLiveData
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineFirst
import modulon.extensions.text.toStringOrEmpty
import org.java_json.JSONException

class ImportViewModel(application: Application) : BaseViewModel(application) {

    enum class State {
        EMPTY, CHECKING, COMPLETE, NETWORK_ERROR, INVALID_NAME, INVALID_SECRET
    }

    private val users = mutableSetOf<User>()
    private val privateKeys = NonNullMutableLiveData(emptySet<PrivateKey>())

    val checkingState = NonNullMediatorLiveData(State.EMPTY)
    val accountList = NonNullMutableLiveData(emptySet<AccountObject>())

    // priv key import
    private val onPrivateKeyChange = debounce<String>(viewModelScope) {
        val keys = setOf(PrivateKey.fromWif(it, ChainPropertyRepository.chainSymbol)).filter { it.isValid }.toSet()
        privateKeys.value = keys
        if (keys.isEmpty()) return@debounce
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { AccountRepository.getKeyReferencesOrThrow(keys.map { it.address }) }.onSuccess {
                if (privateKeys.value == keys) {
                    checkingState.postValue(if (it.isEmpty()) State.COMPLETE else State.COMPLETE)
                    accountList.postValue(it.toSet())
                }
            }.onFailure { error ->
                when {
                    privateKeys.value != keys -> Unit
                    error is WebSocketClosedException || error is GrapheneException -> checkingState.postValue(State.NETWORK_ERROR)
                    error is JSONException -> checkingState.postValue(State.INVALID_NAME)
                }
            }
        }

    }

    // mnemonic import
    private val onBrainKeyChange = debounce<String>(viewModelScope) { mnemonic ->
        val keys = KeyCreator.createFromMnemonic(mnemonic)
        privateKeys.value = keys
        if (keys.isEmpty()) return@debounce
        checkingState.value = State.CHECKING
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { AccountRepository.getKeyReferencesOrThrow(keys.map { it.address!! }) }.onSuccess {
                if (privateKeys.value == keys) {
                    checkingState.postValue(if (it.isEmpty()) State.COMPLETE else State.COMPLETE)
                    accountList.postValue(it.toSet())
                }
            }.onFailure { error ->
                when {
                    privateKeys.value != keys -> Unit
                    error is WebSocketClosedException || error is GrapheneException -> checkingState.postValue(State.NETWORK_ERROR)
                    error is JSONException -> checkingState.postValue(State.INVALID_NAME)
                }
            }
        }
    }

    val privateKeyPermissions = combineFirst(accountList, privateKeys) { accounts, keys ->
        when {
            accounts.isNullOrEmpty() || keys.isNullOrEmpty() -> {
                users.clear()
                checkingState.value = State.EMPTY
                emptyList()
            }
            else -> {
                val permissions = getUserPermission(accounts, keys)
                checkingState.value = if (permissions.isEmpty()) State.INVALID_SECRET else State.COMPLETE
                permissions
            }
        }
    }

    // account import
    val isObserveModeEnabled = NonNullMediatorLiveData(false)
    private var lastAccountNameOrId = ChainConfig.EMPTY_STRING_ID
    private val passwordText = NonNullMutableLiveData(EMPTY_SPACE)

    private val onAccountChange = debounce<String>(viewModelScope) { nameOrId ->
        if (lastAccountNameOrId.isBlank()) return@debounce
        checkingState.value = State.CHECKING
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { AccountRepository.getAccountOrThrow(nameOrId) }.onSuccess {
                if (it != null && (lastAccountNameOrId == it.name || lastAccountNameOrId == it.id)) {
                    AccountRepository.addAccountObject(it)
                    checkingState.postValue(State.COMPLETE)
                    accountList.postValue(setOf(it))
                }
            }.onFailure { error ->
                when {
                    lastAccountNameOrId != nameOrId -> Unit
                    error is WebSocketClosedException || error is GrapheneException -> checkingState.postValue(State.NETWORK_ERROR)
                    error is JSONException -> checkingState.postValue(State.INVALID_NAME)
                }
            }
        }
    }
    private val onPasswordChange = debounce<String>(viewModelScope) { passwordText.value = it }

    val cloudPermissions = combineFirst(accountList, passwordText, isObserveModeEnabled) { accounts, password, observe ->
        when {
            accounts.isNullOrEmpty() || (password.isNullOrEmpty() && observe != true) -> {
                users.clear()
                checkingState.value = State.EMPTY
                emptyList()
            }
            observe == true -> {
                users.addAll(accounts.map { it.toUser() })
                checkingState.value = State.COMPLETE
                emptyList()
            }
            else -> {
                val permissions = getUserPermission(accounts, accounts.flatMap { KeyCreator.createFromSeed(it.name, password!!) }.toSet())
                checkingState.value = if (permissions.isEmpty()) State.INVALID_SECRET else State.COMPLETE
                permissions
            }
        }
    }

    @Synchronized
    fun changeAccountText(text: Editable?) {
        resetUserList()
        lastAccountNameOrId = text.toStringOrEmpty()
        if (lastAccountNameOrId.isNotBlank()) onAccountChange.invoke(lastAccountNameOrId)
    }

    fun changePasswordText(text: Editable?) {
        passwordText.value = text.toStringOrEmpty()
    }

    fun changeBrainText(text: Editable?) {
        resetUserList()
        onBrainKeyChange.invoke(text.toStringOrEmpty())
    }

    fun changePrivateText(text: Editable?) {
        resetUserList()
        onPrivateKeyChange.invoke(text.toStringOrEmpty())
    }

    fun switchObserveMode(enabled: Boolean) {
        isObserveModeEnabled.value = enabled
    }

    fun checkForImport(): Boolean {
        if (users.isEmpty() && accountList.value.isNotEmpty()) checkingState.value = State.INVALID_SECRET
        return users.isNotEmpty()
    }

    fun import(): Boolean {
        if (users.isNotEmpty()) blockchainDatabaseScope.launch {
            LocalUserRepository.add(globalWalletManager, users.toList())
            withContext(Dispatchers.Main) { LocalUserRepository.switch(users.random()) }
        }
        return users.isNotEmpty()
    }

    private fun resetUserList() {
        checkingState.value = State.EMPTY
        accountList.value = emptySet()
        users.clear()
    }

    private fun getUserPermission(accounts: Set<AccountObject>, keys: Set<PrivateKey>): List<Authority> {
        return accounts.flatMap { account ->
            val user = account.toUser().apply {
                ownerKeys = keys.filter { account.ownerKeyAuths.containsKey(it.publicKey) }.toSet()
                activeKeys = keys.filter { account.activeKeyAuths.containsKey(it.publicKey) }.toSet()
                memoKeys = keys.filter { account.memoKeyAuths.containsKey(it.publicKey) }.toSet()
            }
            if (user.hasKeys()) users.add(user)
            values().toList().filter { permission ->
                when (permission) {
                    OWNER -> user.ownerKeys.isNotEmpty()
                    ACTIVE -> user.activeKeys.isNotEmpty()
                    MEMO -> user.memoKeys.isNotEmpty()
                }
            }
        }.distinct()
    }

}

