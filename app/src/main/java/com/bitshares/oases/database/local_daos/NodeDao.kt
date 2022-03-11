package com.bitshares.oases.database.local_daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitshares.oases.database.entities.Node

@Dao
interface NodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNode(nodes: Node): Long

    @Query("SELECT * FROM nodes")
    fun getLiveList(): LiveData<List<Node>>

    @Query("SELECT * FROM nodes WHERE id = :id")
    fun getLiveNode(id: Int): LiveData<Node>

    @Query("SELECT * FROM nodes ORDER BY name ASC")
    fun getNameSortedList(): LiveData<List<Node>>

    @Query("SELECT * FROM nodes ORDER BY latency ASC")
    fun getLatencySortedList(): LiveData<List<Node>>

    @Query("SELECT * FROM nodes WHERE id = :id")
    suspend fun getNode(id: Int): Node?

    @Query("SELECT * FROM nodes")
    suspend fun getList(): List<Node>

    @Query("UPDATE nodes SET name = :name, url = :url, username = :username, password = :password WHERE id = :id")
    suspend fun update(id: Int, name: String, url: String, username: String, password: String)

    @Query("UPDATE nodes SET latency = :latency, last_update = :lastUpdate WHERE id = :id")
    suspend fun updateLatency(id: Int, latency: Long, lastUpdate: Long)

    @Query("UPDATE nodes SET supported_apis = :apis WHERE id = :id")
    suspend fun updateApis(id: Int, apis: String)

    @Query("UPDATE nodes SET chain_id = :chainId, core_symbol = :symbol WHERE id = :id")
    suspend fun updateChainInfo(id: Int, chainId: String, symbol: String)

    @Delete
    suspend fun remove(node: Node)

    @Query("DELETE FROM nodes")
    suspend fun removeAll()

}