package com.bitshares.oases.ui.wallet

import android.app.Application
import com.bitshares.oases.applicationWalletSecurityManager
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.ui.base.BaseViewModel

class WalletViewModel(application: Application) : BaseViewModel(application) {


    val account = LocalUserRepository.currentUserAccount
    val userList = LocalUserRepository.decryptedList(applicationWalletSecurityManager)
    val currentUser = LocalUserRepository.decryptCurrentUser(applicationWalletSecurityManager)

    val usePassword = Settings.KEY_USE_PASSWORD
    val useFingerprint = Settings.KEY_USE_FINGERPRINT


}

