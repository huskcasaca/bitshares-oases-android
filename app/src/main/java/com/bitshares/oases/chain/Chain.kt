package com.bitshares.oases.chain

import bitshareskit.entities.Block
import bitshareskit.extensions.formatAssetBalance
import bitshareskit.extensions.formatInstance
import bitshareskit.extensions.isGrapheneInstanceValid
import bitshareskit.extensions.isIdentifierValid
import bitshareskit.models.AssetAmount
import bitshareskit.models.PrivateKey
import bitshareskit.objects.AccountObject
import bitshareskit.objects.AssetObject
import com.bitshares.oases.MainApplication
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.preference.old.Graphene
import com.bitshares.oases.provider.chain_repo.AccountRepository
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import com.bitshares.oases.security.BinaryRestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import modulon.extensions.text.PatternInputFilter
import modulon.extensions.text.toStringOrEmpty

// TODO: 2022/2/19 remove
val blockchainDatabaseScope get() = CoroutineScope(Dispatchers.IO + MainApplication.applicationJob)
val blockchainNetworkScope get() = CoroutineScope(Dispatchers.IO + MainApplication.applicationJob)

val accountNameFilter = AccountNameFilter
val assetSymbolFilter = AssetSymbolFilter

val walletPasswordFilter = PatternInputFilter(Regex("[0-9]+"))

fun formatCoreAssetAmount(amount: Long): AssetAmount = AssetAmount(amount, Graphene.KEY_CORE_ASSET.value)
fun formatCoreAssetBalance(amount: Long): String = formatAssetBalance(formatCoreAssetAmount(amount))


fun formatCoreAssetAmount(amount: ULong): AssetAmount = AssetAmount(amount.toLong(), Graphene.KEY_CORE_ASSET.value)
fun formatCoreAssetBalance(amount: ULong): String = formatAssetBalance(formatCoreAssetAmount(amount))


fun getCanonicalAccountName(name: String): String {
    return accountNameFilter.filter(name, 0, name.length, null, 0, 0).toStringOrEmpty()
}

fun getCanonicalAssetName(symbol: String): String {
    return assetSymbolFilter.filter(symbol, 0, symbol.length, null, 0, 0).toStringOrEmpty()
}

suspend fun resolveAccountPath(nameOrId: String?): AccountObject {
    return when {
        nameOrId == null -> null
        isGrapheneInstanceValid(nameOrId.toLongOrNull() ?: -1L) -> AccountRepository.getAccountObject(nameOrId.toLong())
        isIdentifierValid<AccountObject>(nameOrId) -> AccountRepository.getAccountObject(formatInstance(nameOrId))
        else -> AccountRepository.getAccountOrNull(getCanonicalAccountName(nameOrId))
    } ?: AccountObject.EMPTY
}

suspend fun resolveAssetPath(symbolOrId: String?): AssetObject {
    return when {
        symbolOrId == null -> null
        isGrapheneInstanceValid(symbolOrId.toLongOrNull() ?: -1L) -> AssetRepository.getAssetObject(symbolOrId.toLong())
        isIdentifierValid<AccountObject>(symbolOrId) -> AssetRepository.getAssetObject(formatInstance(symbolOrId))
        else -> AssetRepository.getAssetOrNull(getCanonicalAssetName(symbolOrId))
    } ?: AssetObject.EMPTY
}

suspend fun resolveBlock(heightPath: String?): Block {
    return when {
        heightPath == null -> null
        heightPath.toLongOrNull() != null -> ChainPropertyRepository.getBlock(heightPath.toLong())
        else -> null
    } ?: Block.EMPTY
}

val Set<PrivateKey>.publicKeysSet get() = map { it.publicKey }.toSet()

fun BinaryRestore.BackupUser.toUser(): User = User(
    uid,
    name,
    chainId
)

fun User.toBackupUser(): BinaryRestore.BackupUser = BinaryRestore.BackupUser(
    uid,
    name,
    chainId,
    ownerKeys.publicKeysSet,
    activeKeys.publicKeysSet,
    memoKeys.publicKeysSet,
)

