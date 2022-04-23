package com.bitshares.oases.database.local_daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.database.entities.Node
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

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

@Dao
interface BitsharesNodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(nodes: BitsharesNode): Long

    @Query("SELECT * FROM bitshares_nodes WHERE id = :id")
    suspend fun get(id: Long): BitsharesNode?
    @Query("SELECT * FROM bitshares_nodes WHERE id = :id")
    fun getLive(id: Long): LiveData<BitsharesNode>
    @Query("SELECT * FROM bitshares_nodes WHERE id = :id")
    fun getAsync(id: Long): Flow<BitsharesNode>

    @Query("SELECT * FROM bitshares_nodes")
    suspend fun getList(): List<BitsharesNode>
    @Query("SELECT * FROM bitshares_nodes")
    fun getListLive(): LiveData<List<BitsharesNode>>
    @Query("SELECT * FROM bitshares_nodes")
    fun getListAsync(): Flow<List<BitsharesNode>>
    @Query("SELECT * FROM bitshares_nodes ORDER BY name ASC")
    fun getListSortedByNameLive(): LiveData<List<BitsharesNode>>
    @Query("SELECT * FROM bitshares_nodes ORDER BY latency ASC")
    fun getListSortedByLatencyLive(): LiveData<List<BitsharesNode>>


    @Query("UPDATE bitshares_nodes SET name = :name, url = :url, username = :username, password = :password WHERE id = :id")
    suspend fun update(id: Long, name: String, url: String, username: String, password: String)
    @Query("UPDATE bitshares_nodes SET latency = :latency, last_update = :lastUpdate WHERE id = :id")
    suspend fun updateLatency(id: Long, latency: Long, lastUpdate: Long = Clock.System.now().toEpochMilliseconds())
    @Query("UPDATE bitshares_nodes SET chain_id = :chainId, core_symbol = :symbol WHERE id = :id")
    suspend fun updateChainInfo(id: Long, chainId: String, symbol: String)

    @Delete
    suspend fun remove(node: BitsharesNode)
    @Query("DELETE FROM bitshares_nodes")
    suspend fun clear()

}