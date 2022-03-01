package com.bitshares.android.ui.wallet

import android.app.Application
import com.bitshares.android.preference.old.Settings
import com.bitshares.android.provider.local_repo.LocalUserRepository
import com.bitshares.android.ui.base.BaseViewModel

class WalletViewModel(application: Application) : BaseViewModel(application) {


    val account = LocalUserRepository.currentUserAccount
    val userList = LocalUserRepository.list
    val currentUser = LocalUserRepository.currentUser

    val usePassword = Settings.KEY_USE_PASSWORD
    val useFingerprint = Settings.KEY_USE_FINGERPRINT


}

