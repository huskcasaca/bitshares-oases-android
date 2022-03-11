package com.bitshares.oases.security

import bitshareskit.models.PrivateKey
import bitshareskit.models.PublicKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineFirst

// TODO: 3/10/2021 replace with AuthorityRepository
// TODO: 15/1/2022 init by application
object AuthorityService {

    val isAuthorized = NonNullMutableLiveData(false)

    val isOwnerAuthorized = NonNullMutableLiveData(false)
    val isActiveAuthorized = NonNullMutableLiveData(false)
    val isMemoAuthorized = NonNullMutableLiveData(false)

    val ownerRequiredAuths = mutableSetOf<PrivateKey>()
    val activeRequiredAuths = mutableSetOf<PrivateKey>()
    val memoRequiredAuths = mutableSetOf<PrivateKey>()

    // TODO: 9/10/2021 add account
    @Synchronized
    private fun check(user: User?, account: AccountObject?) {
        ownerRequiredAuths.clear()
        activeRequiredAuths.clear()
        memoRequiredAuths.clear()
        if (user == null || account == null || user.uid != account.uid) {
            isActiveAuthorized.value = false
            isMemoAuthorized.value = false
            isAuthorized.value = false
        } else {
            isOwnerAuthorized.value = appendKeys(account.ownerKeyAuths, user.ownerKeys, ownerRequiredAuths, account.ownerMinThreshold)
            isActiveAuthorized.value = appendKeys(account.activeKeyAuths, user.activeKeys, activeRequiredAuths, account.activeMinThreshold)
            isMemoAuthorized.value = appendKeys(account.memoKeyAuths, user.memoKeys, memoRequiredAuths, 1U)
        }
        isAuthorized.value = isOwnerAuthorized.value && isActiveAuthorized.value && isMemoAuthorized.value
    }

    private fun appendKeys(keyMap: Map<PublicKey, UShort>, from: Set<PrivateKey>, to: MutableSet<PrivateKey>, minimal: UInt): Boolean {
        var accumulated = 0U
        val toLocal = mutableSetOf<PrivateKey>()
        keyMap.toList().sortedByDescending { it.second }.forEach { (key, threshold) ->
            if (accumulated >= minimal) return@forEach
            from.find { it.publicKey == key }?.let {
                toLocal.add(it)
                accumulated += threshold
            }
        }
        to.addAll(toLocal)
        return accumulated >= minimal
    }

//    fun start() {
//        combineFirst(LocalUserRepository.currentUser, LocalUserRepository.currentUserAccount).observeForever { (user, account) ->
//            check(user, account)
//        }
//
//    }


}