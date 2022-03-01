package com.bitshares.android.provider.local_repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import bitshareskit.extensions.logcat
import bitshareskit.ks_chain.Authority
import bitshareskit.models.PrivateKey
import bitshareskit.objects.AccountObject
import com.bitshares.android.chain.blockchainDatabaseScope
import com.bitshares.android.chain.toBackupUser
import com.bitshares.android.database.LocalDatabase
import com.bitshares.android.database.entities.User
import com.bitshares.android.database.entities.toUser
import com.bitshares.android.database.entities.userInstanceComparator
import com.bitshares.android.preference.old.Graphene
import com.bitshares.android.preference.old.Settings
import com.bitshares.android.provider.chain_repo.AccountRepository
import com.bitshares.android.provider.chain_repo.ChainPropertyRepository
import com.bitshares.android.security.BinaryRestore
import com.bitshares.android.security.SecurityService
import kotlinx.coroutines.launch
import modulon.extensions.livedata.combineLatest
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.distinctUntilChangedBy

object LocalUserRepository {

    private val userDao = LocalDatabase.INSTANCE.userDao()

    val list = userDao.getListLive().map { it.map { decrypt(it) }.toSortedSet(userInstanceComparator) }

    suspend fun add(user: User) = userDao.add(appendFromDatabase(user))
    suspend fun add(account: AccountObject) = add(account.toUser())
    suspend fun add(users: List<User>) = userDao.add(users.map { appendFromDatabase(it) })

    suspend fun addForObserve(user: User) = userDao.addForObserve(user)
    suspend fun addForObserve(account: AccountObject) = addForObserve(account.toUser())

    suspend fun get(uid: Long, chainId: String) = userDao.get(uid, chainId)
    suspend fun getList() = userDao.getList()
    suspend fun getList(chainId: String) = userDao.getList(chainId)

    fun getListLive() = userDao.getListLive()
    fun getListLive(chainId: String) = userDao.getListLive(chainId)

    private suspend fun appendFromDatabase(user: User): User {
        val owner = mutableSetOf<PrivateKey>()
        val active = mutableSetOf<PrivateKey>()
        val memo = mutableSetOf<PrivateKey>()
        getDecryptedUser(user)?.apply {
            ownerKeys.let { keys -> owner.addAll(keys) }
            activeKeys.let { keys -> active.addAll(keys) }
            memoKeys.let { keys -> memo.addAll(keys) }
        }
        user.apply {
            ownerKeys.let { keys -> owner.addAll(keys) }
            activeKeys.let { keys -> active.addAll(keys) }
            memoKeys.let { keys -> memo.addAll(keys) }
            ownerKeys = owner.toSet()
            activeKeys = active.toSet()
            memoKeys = memo.toSet()
        }
        return encrypt(user)
    }

    private fun encrypt(user: User): User {
        if (!SecurityService.isUnlocked.value) return user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())
        return user.copy(
            activeKeys = user.activeKeys.map { encryptPrivateKey(it) }.toSet(),
            ownerKeys = user.ownerKeys.map { encryptPrivateKey(it) }.toSet(),
            memoKeys = user.memoKeys.map { encryptPrivateKey(it) }.toSet(),
        )
    }

    private fun decrypt(user: User): User {
        if (!SecurityService.isUnlocked.value) return user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())
        return user.copy(
            activeKeys = user.activeKeys.map { decryptPrivateKey(it) }.toSet(),
            ownerKeys = user.ownerKeys.map { decryptPrivateKey(it) }.toSet(),
            memoKeys = user.memoKeys.map { decryptPrivateKey(it) }.toSet(),
        )
    }

    private fun decryptPrivateKey(key: PrivateKey): PrivateKey {
        return if (key.isEncrypted) PrivateKey(SecurityService.decrypt(key.keyBytes), key.type, key.prefix).apply { isEncrypted = false } else key
    }

    private fun encryptPrivateKey(key: PrivateKey): PrivateKey {
        return PrivateKey(SecurityService.encrypt(key.keyBytes), key.type, key.prefix).apply { isEncrypted = true }
    }

    private suspend fun getDecryptedUser(user: User): User? {
        val result = get(user.uid, user.chainId)
        if (result != null) return decrypt(result)
        return result
    }

    private suspend fun getDecryptedUserList(): List<User> = userDao.getList().map { decrypt(it) }

    fun switch(user: User) {
        if (user.chainId == ChainPropertyRepository.chainId) {
            Settings.KEY_CURRENT_ACCOUNT_ID.value = user.uid
        }
    }

    private suspend fun removeInternal(user: User) {
        userDao.remove(user)
        if (Settings.KEY_CURRENT_ACCOUNT_ID.value == user.uid) {
            val users = getList(ChainPropertyRepository.chainId)
            if (users.isNotEmpty()) Settings.KEY_CURRENT_ACCOUNT_ID.value = users.first().uid else Settings.KEY_CURRENT_ACCOUNT_ID.reset()
        }
    }

    fun remove(user: User) {
        blockchainDatabaseScope.launch {
            removeInternal(user)
        }
    }

    suspend fun removeKey(user: User, key: PrivateKey, type: Authority) {
        if (!SecurityService.isUnlocked.value) return
        getDecryptedUser(user)?.let { decrypted ->
            when (type) {
                Authority.OWNER -> decrypted.ownerKeys = decrypted.ownerKeys.filter { it != key }.toSet()
                Authority.ACTIVE -> decrypted.activeKeys = decrypted.activeKeys.filter { it != key }.toSet()
                Authority.MEMO -> decrypted.memoKeys = decrypted.memoKeys.filter { it != key }.toSet()
            }
            userDao.add(encrypt(decrypted))
        }
    }

    fun getUserLive(uid: Long, chainId: String) = getUserLive(User.generateUUID(uid, chainId))

    fun getUserLive(uuid: String): LiveData<User?> = combineLatest(userDao.getLive(uuid), SecurityService.isUnlocked) { user, unlocked ->
        when {
            user == null -> null
            unlocked == true -> decrypt(user)
            else -> decrypt(user)
        }
    }

    val currentUser: LiveData<User?> = combineNonNull(Settings.KEY_CURRENT_ACCOUNT_ID, Graphene.KEY_CHAIN_ID).switchMap { (uid, chainId) -> getUserLive(uid, chainId) }
    val currentUserAccount: LiveData<AccountObject?> = Settings.KEY_CURRENT_ACCOUNT_ID.switchMap { AccountRepository.getAccountLive(it) }.distinctUntilChanged()
    val currentUserOnly: LiveData<User?> = currentUser.map { if (it != null) removeAllKeys(it) else null }.distinctUntilChangedBy { it?.uid }

    private fun removeAllKeys(user: User): User = user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())

    suspend fun createBackup(): BinaryRestore {
        val users = getDecryptedUserList()
        val keys = users.flatMap { it.ownerKeys + it.activeKeys + it.memoKeys }.toSet()
        return BinaryRestore(users.map { it.toBackupUser() }, keys, true)
    }

    suspend fun switchChain(chainId: String) {
        logcat("switchChain $chainId")
        val currentUser = get(Settings.KEY_CURRENT_ACCOUNT_ID.value, ChainPropertyRepository.chainId)
        if (currentUser == null || currentUser.chainId != chainId) {
            val users = getList(chainId)
            if (users.isNotEmpty()) Settings.KEY_CURRENT_ACCOUNT_ID.value = users.first().uid else Settings.KEY_CURRENT_ACCOUNT_ID.reset()
        }
    }


}