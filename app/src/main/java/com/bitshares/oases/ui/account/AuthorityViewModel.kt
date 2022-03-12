package com.bitshares.oases.ui.account

import android.app.Application
import com.bitshares.oases.globalWalletManager
import modulon.extensions.livedata.combineFirst
import modulon.extensions.livedata.combineFirstOrNull
import modulon.extensions.livedata.combineNonNull

open class AuthorityViewModel(application: Application) : AccountViewModel(application) {

    val isOwnerSufficientLocal = combineFirstOrNull(ownerKeyAuths, ownerKeyAuthsLocal, ownerMinThreshold, globalWalletManager.isUnlocked) { keyAuths, keyAuthsLocal, threshold, unlocked -> if (unlocked) checkSufficient(keyAuths, keyAuthsLocal, threshold) else null }
    val isActiveSufficientLocal = combineFirstOrNull(activeKeyAuths, activeKeyAuthsLocal, activeMinThreshold, globalWalletManager.isUnlocked) { keyAuths, keyAuthsLocal, threshold, unlocked -> if (unlocked) checkSufficient(keyAuths, keyAuthsLocal, threshold) else null }
    val isMemoSufficientLocal = combineFirstOrNull(memoKeyAuths, memoKeyAuthsLocal, memoMinThreshold, globalWalletManager.isUnlocked) { keyAuths, keyAuthsLocal, threshold, unlocked -> if (unlocked) checkSufficient(keyAuths, keyAuthsLocal, 1U) else null }

    val ownerThreshold = ownerMinThreshold
    val activeThreshold = activeMinThreshold
    val memoThreshold = memoMinThreshold

    val ownerKeyAuthsLocalWithWeight = combineNonNull(ownerKeyAuths, ownerKeyAuthsLocal) { keyAuths, keyAuthsLocal -> keyAuthsLocal.map { it to (keyAuths[it.publicKey] ?: 0u) } }
    val activeKeyAuthsLocalWithWeight = combineNonNull(activeKeyAuths, activeKeyAuthsLocal) { keyAuths, keyAuthsLocal -> keyAuthsLocal.map { it to (keyAuths[it.publicKey] ?: 0u) } }
    val memoKeyAuthsLocalWithWeight = combineNonNull(memoKeyAuths, memoKeyAuthsLocal) { keyAuths, keyAuthsLocal -> keyAuthsLocal.map { it to (keyAuths[it.publicKey] ?: 0u) } }

    val ownerKeyAuthsLocalWithWeightOrEmpty = combineFirst(ownerKeyAuthsLocalWithWeight, ownerKeyAuthsLocal){ keysWithWeight, keysOnly -> keysWithWeight ?: keysOnly.orEmpty().map { it to null } }
    val activeKeyAuthsLocalWithWeightOrEmpty = combineFirst(activeKeyAuthsLocalWithWeight, activeKeyAuthsLocal){ keysWithWeight, keysOnly -> keysWithWeight ?: keysOnly.orEmpty().map { it to null } }
    val memoKeyAuthsLocalWithWeightOrEmpty = combineFirst(memoKeyAuthsLocalWithWeight, memoKeyAuthsLocal){ keysWithWeight, keysOnly -> keysWithWeight ?: keysOnly.orEmpty().map { it to null } }

}

