package com.bitshares.oases.ui.wallet

import android.app.Application
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel

class WalletViewModel(application: Application) : BaseViewModel(application) {


    val account = LocalUserRepository.currentUserAccount
    val userList = LocalUserRepository.decryptedList(globalWalletManager)
    val currentUser = LocalUserRepository.decryptCurrentUser(globalWalletManager)

    val usePassword = globalPreferenceManager.USE_PASSWORD
    val useBio = globalPreferenceManager.USE_BIO


}

