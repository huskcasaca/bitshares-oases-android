package com.bitshares.oases.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Entity(tableName = "nodes")
data class Node(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "username") var username: String = "",
    @ColumnInfo(name = "password") var password: String = "",
    @ColumnInfo(name = "chain_id") var chainId: String = "",
    @ColumnInfo(name = "core_symbol") var coreSymbol: String = "",
    @ColumnInfo(name = "latency") var latency: Long = Long.MAX_VALUE,
    @ColumnInfo(name = "supported_apis") var apis: List<Boolean> = List(4) { false },
    @ColumnInfo(name = "last_update") var lastUpdate: Long = 0L
) {
    companion object {
        val EMPTY get() = Node(url = "wss://")

        const val LATENCY_TIMEOUT = Long.MAX_VALUE
        const val LATENCY_CONNECTING = -1L
    }
}

@Serializable
@Entity(tableName = "bitshares_nodes")
data class BitsharesNode(
    @SerialName("id") @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @SerialName("name") @ColumnInfo(name = "name")
    val name: String = "",
    @SerialName("url") @ColumnInfo(name = "url")
    val url: String = "wss://",
    @SerialName("username") @ColumnInfo(name = "username")
    val username: String = "",
    @SerialName("password") @ColumnInfo(name = "password")
    val password: String = "",
    @SerialName("chain_id") @ColumnInfo(name = "chain_id")
    val chainId: String = "",
    @SerialName("core_symbol") @ColumnInfo(name = "core_symbol")
    val coreSymbol: String = "",
    @SerialName("latency") @ColumnInfo(name = "latency")
    val latency: Long = Long.MAX_VALUE,
//    @ColumnInfo(name = "supported_apis")
//    val apis: List<Boolean> = List(4) { false },
    @SerialName("last_update") @ColumnInfo(name = "last_update")
    val lastUpdate: Long = Long.MAX_VALUE,
) {

    companion object {
        const val LATENCY_TIMEOUT = Long.MAX_VALUE
        const val LATENCY_CONNECTING = -1L
        const val LATENCY_UNRESOLVED = -2L
        const val LATENCY_UNKNOWN = -3L

    }
}

val nodeConfigAreEquivalent: (BitsharesNode, BitsharesNode) -> Boolean = { old, new ->
    old.id == new.id && old.url == new.url && old.username == new.username && old.password == new.password
}