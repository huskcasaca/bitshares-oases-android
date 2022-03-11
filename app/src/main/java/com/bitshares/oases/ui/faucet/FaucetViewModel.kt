package com.bitshares.oases.ui.faucet

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import bitshareskit.models.PrivateKey
import com.bitshares.oases.extensions.text.StringFilter
import com.bitshares.oases.extensions.text.validateStringFilter
import com.bitshares.oases.netowrk.faucets.Faucet
import com.bitshares.oases.netowrk.faucets.FaucetRegister
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.ui.base.BaseViewModel
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.debounce
import modulon.extensions.livedata.map
import modulon.extensions.text.toStringOrEmpty

class FaucetViewModel(application: Application) : BaseViewModel(application) {

    val faucet = NonNullMutableLiveData(Faucet.BITSHARES_EUROPE)

    val accountNameField = NonNullMutableLiveData(EMPTY_SPACE)
    val accountPasswordField = NonNullMutableLiveData(EMPTY_SPACE)
    val accountPasswordRepeatField = NonNullMutableLiveData(EMPTY_SPACE)


    val accountExist = accountNameField.debounce(viewModelScope).map(viewModelScope) {
        AccountRepository.getAccountOrNull(it.toStringOrEmpty()) != null
    }

    val isAccountNameFieldError = NonNullMutableLiveData(false)
    val isAccountPasswordFieldError = NonNullMutableLiveData(false)
    val isAccountPasswordRepeatFieldError = NonNullMutableLiveData(false)

    val accountPasswordFieldNoticed = NonNullMutableLiveData(EMPTY_SPACE)
    val accountPasswordRepeatFieldNoticed = NonNullMutableLiveData(EMPTY_SPACE)

    fun generateRandom() {
        val randomPassword = "P${PrivateKey.random(ChainPropertyRepository.chainSymbol).wif}"
        accountPasswordFieldNoticed.value = randomPassword
        accountPasswordRepeatFieldNoticed.value = randomPassword
    }

    fun changeNameField(text: String) {
        accountNameField.value = text
        isAccountNameFieldError.value = text.isNotEmpty() && !validateStringFilter(text, StringFilter.FILTER_CHEAP_ACCOUNT_NAME)
    }

    fun changePasswordField(text: String) {
        accountPasswordField.value = text
        isAccountPasswordFieldError.value = text.isNotEmpty() && !validateStringFilter(text, StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER)
    }

    fun changePasswordRepeatField(text: String) {
        accountPasswordRepeatField.value = text
        isAccountPasswordRepeatFieldError.value = text.isNotEmpty() && !validateStringFilter(accountPasswordField.value, text, StringFilter.FILTER_REQUIRE_EQUALS)
    }

    fun validate(): Boolean {
        return  validateStringFilter(accountNameField.value, StringFilter.FILTER_CHEAP_ACCOUNT_NAME).also { isAccountNameFieldError.value = !it } &&
                validateStringFilter(accountPasswordField.value, StringFilter.FILTER_PASSWORD_STRENGTH_REGISTER).also { isAccountPasswordFieldError.value = !it } &&
                validateStringFilter(accountPasswordField.value, accountPasswordRepeatField.value, StringFilter.FILTER_REQUIRE_EQUALS).also { isAccountPasswordRepeatFieldError.value = !it }
    }

    val registerInfo = MutableLiveData<FaucetRegister>()

    fun generateRegisterInfo(): FaucetRegister = FaucetRegister(faucet.value, accountNameField.value, accountPasswordField.value)

    fun clear() {
        faucet.value = Faucet.BITSHARES_EUROPE
        accountNameField.value = EMPTY_SPACE
        accountPasswordField.value = EMPTY_SPACE
        accountPasswordRepeatField.value = EMPTY_SPACE
        isAccountNameFieldError.value = false
        isAccountPasswordFieldError.value = false
        isAccountPasswordRepeatFieldError.value = false
        accountPasswordFieldNoticed.value = EMPTY_SPACE
        accountPasswordRepeatFieldNoticed.value = EMPTY_SPACE
    }

}

