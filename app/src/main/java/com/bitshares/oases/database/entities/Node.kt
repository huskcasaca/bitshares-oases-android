package com.bitshares.oases.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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

