package com.bitshares.oases.ui.wallet

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.extensions.text.StringFilter
import com.bitshares.oases.extensions.text.validateStringFilter
import com.bitshares.oases.globalWalletManager
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.security.BinaryRestore
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.dialog.DialogState
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.charset.orEmpty
import modulon.extensions.coroutine.mapParallel
import modulon.extensions.livedata.NonNullMediatorLiveData
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.distinctUntilChanged
import modulon.extensions.livedata.filterNotNull
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import graphene.extension.*

class WalletManagerViewModel(application: Application) : BaseViewModel(application) {

    companion object {
        private const val PASSWORD_LENGTH_MIN = 6
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"

        private const val BACKUP_BEGIN = "-----BEGIN WALLET TEXT BACKUP-----"
        private const val BACKUP_END = "-----END WALLET TEXT BACKUP-----"

        suspend fun decodeFile(uri: Uri, contentResolver: ContentResolver): BackupFile {
            val input: InputStream? = withContext(Dispatchers.IO) { contentResolver.openInputStream(uri) }
            if (input != null && withContext(Dispatchers.IO) { input.available() } in (32 * 2 + 2)..(1024 * 1024 * 16)) {
                val size = withContext(Dispatchers.IO) { input.available().toLong() }
                val bytes = withContext(Dispatchers.IO) { input.readBytes() }
                contentResolver.query(uri, null, null, null, null)?.apply {
                    val index = getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    moveToFirst()
                    val name = getString(index)
                    close()
                    return BackupFile(name, size, bytes)
                }
            }
            return BackupFile.INVALID
        }

        suspend fun decodeFile(text: String): BackupFile {
            val lines = text.reader().readLines()
            if (lines.size == 4 && lines[0] == BACKUP_BEGIN && lines[3] == BACKUP_END) {
                val input = ByteArrayInputStream(lines[2].decodeBase64OrEmpty())
                if (withContext(Dispatchers.IO) { input.available() } in (32 * 2 + 2)..(1024 * 1024 * 16)) {
                    val size = withContext(Dispatchers.IO) { input.available().toLong() }
                    val bytes = withContext(Dispatchers.IO) { input.readBytes() }
                    return BackupFile(lines[1], size, bytes)
                }
            }
            return BackupFile.INVALID
        }

    }

    val isPasswordFieldError = NonNullMutableLiveData(false).distinctUntilChanged()
    val isNewPasswordFieldError = NonNullMutableLiveData(false).distinctUntilChanged()
    val isRepeatPasswordFieldError = NonNullMutableLiveData(false).distinctUntilChanged()

    val passwordField = NonNullMutableLiveData(EMPTY_SPACE)
    val newPasswordField = NonNullMutableLiveData(EMPTY_SPACE)
    val repeatPasswordField = NonNullMutableLiveData(EMPTY_SPACE)

    val isWalletUnlocked = globalWalletManager.isUnlocked

    val password get() = passwordField.value
    val newPassword get() = newPasswordField.value
    val repeatPassword get() = repeatPasswordField.value

    fun unlock(): Boolean {
        return (globalWalletManager.unlock(password)).also {
            isPasswordFieldError.value = !it
        }
    }

    fun change(): Boolean {
        return (unlock() && (newPassword.length >= PASSWORD_LENGTH_MIN).also { isNewPasswordFieldError.value = !it } && (repeatPassword.length >= PASSWORD_LENGTH_MIN && newPassword == repeatPassword).also { isRepeatPasswordFieldError.value = !it } && globalWalletManager.changePassword(newPassword))
    }

    fun changePasswordField(text: String) {
        passwordField.value = text
        isPasswordFieldError.value = false
    }

    fun changeNewPasswordField(text: String) {
        newPasswordField.value = text
        isNewPasswordFieldError.value = false
        isRepeatPasswordFieldError.value = false
    }

    fun changeRepeatPasswordField(text: String) {
        repeatPasswordField.value = text
        isNewPasswordFieldError.value = false
        isRepeatPasswordFieldError.value = false
    }

    val file = MutableLiveData<BackupFile>().filterNotNull()

    data class BackupFile(
        val name: String,
        val size: Long,
        val stream: ByteArray? = null,
        val serialized: BinaryRestore? = null,
    ) {
        companion object {
            val INVALID = BackupFile(EMPTY_SPACE, 0L, null)
        }
    }

    fun setBackupFile(file: BackupFile) {
        this.file.value = file
    }


    val state = NonNullMediatorLiveData(DialogState.EMPTY)

    val users = NonNullMediatorLiveData(emptyList<User>())

    fun importBackup() {
        blockchainDatabaseScope.launch {
            LocalUserRepository.add(globalWalletManager, users.value)
        }
    }

    fun unlockBackup() {
        state.value = DialogState.PENDING
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    delay(500)
                    BinaryRestore.fromFile(file.value!!.stream!!, password)
                }
            }.onSuccess { bin ->
                users.value = withContext(Dispatchers.IO) {
                    if (bin.isCanonical) {
//                        val currentChainAccounts = bin.accounts.filter { it.chainId == ChainPropertyRepository.chainId }
//                        val otherChainAccounts = bin.accounts.filter { it.chainId != ChainPropertyRepository.chainId }
                        bin.accounts.map { backupUser ->
                            val keys = bin.privateKeys.map { it.rePrefix(ChainPropertyRepository.resolveSymbol(backupUser.chainId)) }
                            User(
                                backupUser.uid,
                                backupUser.name,
                                backupUser.chainId,
                                keys.filter { backupUser.owner.contains(it.publicKey) }.toSet(),
                                keys.filter { backupUser.active.contains(it.publicKey) }.toSet(),
                                keys.filter { backupUser.memo.contains(it.publicKey) }.toSet(),
                            )
                        }
                    } else {
                        val keys = bin.privateKeys.map { it.rePrefix(ChainPropertyRepository.resolveSymbol(ChainPropertyRepository.chainId)) }
//                        val keys = bin.privateKeys
                        bin.accounts.filter { it.chainId == ChainPropertyRepository.chainId }.mapParallel { AccountRepository.getAccountOrNull(it.name) }.filterNotNull().map { account ->
                            User(
                                account.uid,
                                account.name,
                                ChainPropertyRepository.chainId,
                                keys.filter { account.ownerKeyAuths.keys.contains(it.publicKey) }.toSet(),
                                keys.filter { account.activeKeyAuths.keys.contains(it.publicKey) }.toSet(),
                                keys.filter { account.memoKeyAuths.keys.contains(it.publicKey) }.toSet(),
                            )
                        }
                    }
                }
                state.value = DialogState.SUCCESS
            }.onFailure {
                if (it is BinaryRestore.IncorrectPasswordException) {
                    isPasswordFieldError.value = true
                }
                state.value = DialogState.FAILURE
            }
        }
    }

    private val filePrefix: String get() = "backup_${SimpleDateFormat(DATE_FORMAT, Locale.ROOT).format(Date())}"
    private val fileSuffix: String get() = ".bin"

    fun buildBackup() {
        viewModelScope.launch(Dispatchers.IO) {
            val block = LocalUserRepository.createBackup(globalWalletManager)
            val name = "$filePrefix$fileSuffix"
            withContext(Dispatchers.Main) {
                file.value = BackupFile(name, 0L, null, block)
            }
        }
    }

    val isPasswordValid
        get() = (validateStringFilter(newPassword, StringFilter.FILTER_PASSWORD_STRENGTH_NORMAL)).also { isNewPasswordFieldError.value = !it } && (newPassword == repeatPassword).also { isRepeatPasswordFieldError.value = !it }

    fun createBackup() {
        val backup = file.value
        if (isPasswordValid && backup?.serialized != null) {
            try {
                backup.serialized.encodeToBinary(newPassword)
                state.value = DialogState.PENDING
            } catch (e: Exception) {
                state.value = DialogState.FAILURE
            }
        }
    }

//    fun shareBackup(context: Context): Uri {
//        val uri = File.createTempFile(filePrefix, fileSuffix).toUri()
//        saveBackup(uri, context.contentResolver)
//        return uri
//    }

    fun sharePlainBackup(): String {
        val bin = file.value?.serialized?.encoded.orEmpty()
        return "$BACKUP_BEGIN\n" +
                "$filePrefix$fileSuffix\n" +
                "${bin.encodeBase64OrEmpty()}\n" +
                "$BACKUP_END"

    }

    fun saveBackup(uri: Uri, contentResolver: ContentResolver) {
        viewModelScope.launch {
            val bin = file.value?.serialized?.encoded
            if (bin != null) {
                try {
                    withContext(Dispatchers.IO) {
                        contentResolver.openOutputStream(uri)!!.write(bin)
                        withContext(Dispatchers.Main) { state.value = DialogState.SUCCESS }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) { state.value = DialogState.FAILURE }
                }
            } else {
                withContext(Dispatchers.Main) { state.value = DialogState.FAILURE }
            }
        }
    }

}

