package com.bitshares.oases.provider.local_repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import bitshareskit.extensions.logcat
import bitshareskit.models.PrivateKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.toBackupUser
import com.bitshares.oases.database.LocalDatabase
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.toUser
import com.bitshares.oases.database.entities.userInstanceComparator
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.security.BinaryRestore
import com.bitshares.oases.security.WalletManager
import bitshareskit.chain.Authority
import kotlinx.coroutines.launch
import modulon.extensions.livedata.combineLatest
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.distinctUntilChangedBy

object LocalUserRepository {

    private val userDao = LocalDatabase.INSTANCE.userDao()

    fun decryptedList(manager: WalletManager) = userDao.getListLive().map { it.map { decrypt(manager, it) }.toSortedSet(userInstanceComparator) }

    suspend fun add(manager: WalletManager, user: User) = userDao.add(appendFromDatabase(manager, user))
    suspend fun add(manager: WalletManager, account: AccountObject) = add(manager, account.toUser())
    suspend fun add(manager: WalletManager, users: List<User>) = userDao.add(users.map { appendFromDatabase(manager, it) })


    suspend fun addForObserve(user: User) = userDao.addForObserve(user)
    suspend fun addForObserve(account: AccountObject) = addForObserve(account.toUser())

    suspend fun get(uid: Long, chainId: String) = userDao.get(uid, chainId)
    suspend fun getList() = userDao.getList()
    suspend fun getList(chainId: String) = userDao.getList(chainId)

    fun getListLive() = userDao.getListLive()
    fun getListLive(chainId: String) = userDao.getListLive(chainId)

    private suspend fun appendFromDatabase(manager: WalletManager, user: User): User {
        val owner = mutableSetOf<PrivateKey>()
        val active = mutableSetOf<PrivateKey>()
        val memo = mutableSetOf<PrivateKey>()
        getDecryptedUser(manager, user)?.apply {
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
        return encrypt(manager, user)
    }

    // TODO: 2022/3/11 move to extension
    fun encrypt(manager: WalletManager, user: User): User {
        if (!manager.isUnlocked.value) return user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())
        return user.copy(
            activeKeys = user.activeKeys.map { encryptPrivateKey(manager, it) }.toSet(),
            ownerKeys = user.ownerKeys.map { encryptPrivateKey(manager, it) }.toSet(),
            memoKeys = user.memoKeys.map { encryptPrivateKey(manager, it) }.toSet(),
        )
    }

    // TODO: 2022/3/11 move to extension
    fun decrypt(manager: WalletManager, user: User): User {
        if (!manager.isUnlocked.value) return user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())
        return user.copy(
            activeKeys = user.activeKeys.map { decryptPrivateKey(manager, it) }.toSet(),
            ownerKeys = user.ownerKeys.map { decryptPrivateKey(manager, it) }.toSet(),
            memoKeys = user.memoKeys.map { decryptPrivateKey(manager, it) }.toSet(),
        )
    }

    private fun decryptPrivateKey(manager: WalletManager, key: PrivateKey): PrivateKey {
        return if (key.isEncrypted) PrivateKey(manager.decrypt(key.keyBytes), key.type, key.prefix).apply { isEncrypted = false } else key
    }

    private fun encryptPrivateKey(manager: WalletManager, key: PrivateKey): PrivateKey {
        return PrivateKey(manager.encrypt(key.keyBytes), key.type, key.prefix).apply { isEncrypted = true }
    }

    private suspend fun getDecryptedUser(manager: WalletManager, user: User): User? {
        val result = get(user.uid, user.chainId)
        if (result != null) return decrypt(manager, result)
        return result
    }

    private suspend fun getDecryptedUserList(manager: WalletManager): List<User> = userDao.getList().map { decrypt(manager, it) }

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

    suspend fun removeKey(manager: WalletManager, user: User, key: PrivateKey, type: Authority) {
        if (!manager.isUnlocked.value) return
        getDecryptedUser(manager, user)?.let { decrypted ->
            when (type) {
                Authority.OWNER -> decrypted.ownerKeys = decrypted.ownerKeys.filter { it != key }.toSet()
                Authority.ACTIVE -> decrypted.activeKeys = decrypted.activeKeys.filter { it != key }.toSet()
                Authority.MEMO -> decrypted.memoKeys = decrypted.memoKeys.filter { it != key }.toSet()
            }
            userDao.add(encrypt(manager, decrypted))
        }
    }

    fun getUserLive(manager: WalletManager, uid: Long, chainId: String) = getUserLive(manager, User.generateUUID(uid, chainId))

    fun getUserLive(manager: WalletManager, uuid: String): LiveData<User?> = combineLatest(userDao.getLive(uuid), manager.isUnlocked) { user, unlocked ->
        when {
            user == null -> null
            unlocked == true -> decrypt(manager, user)
            else -> decrypt(manager, user)
        }
    }


    val currentUserAccount: LiveData<AccountObject?> = Settings.KEY_CURRENT_ACCOUNT_ID.switchMap { AccountRepository.getAccountLive(it) }.distinctUntilChanged()

    // TODO: 2022/3/11 remove
    fun decryptCurrentUser(manager: WalletManager): LiveData<User?> = combineNonNull(Settings.KEY_CURRENT_ACCOUNT_ID, Graphene.KEY_CHAIN_ID).switchMap { (uid, chainId) -> getUserLive(manager, uid, chainId) }
    // TODO: 2022/3/11 remove
    fun decryptCurrentUserOnly(manager: WalletManager): LiveData<User?> = decryptCurrentUser(manager).map { if (it != null) removeAllKeys(it) else null }.distinctUntilChangedBy { it?.uid }


    private fun removeAllKeys(user: User): User = user.copy(activeKeys = emptySet(), ownerKeys = emptySet(), memoKeys = emptySet())

    suspend fun createBackup(manager: WalletManager): BinaryRestore {
        val users = getDecryptedUserList(manager)
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