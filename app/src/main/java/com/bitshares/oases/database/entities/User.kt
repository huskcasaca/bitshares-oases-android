package com.bitshares.oases.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import bitshareskit.extensions.EMPTY_UID
import bitshareskit.extensions.createAccountObject
import bitshareskit.models.PrivateKey
import bitshareskit.objects.AccountObject
import com.bitshares.oases.provider.chain_repo.ChainPropertyRepository
import java.util.*

@Entity(tableName = "users")
data class User(
    @ColumnInfo(name = "uid") val uid: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "chain_id") var chainId: String,
    @ColumnInfo(name = "owner_keys") var ownerKeys: Set<PrivateKey> = emptySet(),
    @ColumnInfo(name = "active_keys") var activeKeys: Set<PrivateKey> = emptySet(),
    @ColumnInfo(name = "memo_keys") var memoKeys: Set<PrivateKey> = emptySet()
) {

    companion object {

        fun generateUUID(uid: Long, chainId: String) = UUID(chainId.toSigBits(), uid).toString()

        private fun String.toSigBits() : Long {
            if (this.length != 64) return 0L
            return substring(0..31).hashCode().toLong().shl(32) + substring(32..63).hashCode()
        }
    }

    @PrimaryKey(autoGenerate = false) @ColumnInfo(name = "uuid") var uuid: String = generateUUID(uid, chainId)

    override fun toString(): String {
        return "User(uid=$uid, name=$name, owners=${ownerKeys.map { it.wif }}, active=${activeKeys.map { it.wif }}, memo=${memoKeys.map { it.wif }}"
    }

    fun hasKeys() = ownerKeys.isNotEmpty() || activeKeys.isNotEmpty() || memoKeys.isNotEmpty()

}

fun AccountObject.toUser(chainId: String = ChainPropertyRepository.chainId) = User(uid, name, chainId)

fun User.toAccount() = createAccountObject(uid, name)

val userInstanceComparator = Comparator { o1: User, o2: User ->
    o1.uuid.compareTo(o2.uuid)
}

val User?.uidOrEmpty get() = this?.uid ?: EMPTY_UID
