package com.bitshares.oases.database.local_daos


import androidx.lifecycle.LiveData
import androidx.room.*
import com.bitshares.oases.database.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(user: User)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addForObserve(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(users: List<User>)

    @Query("SELECT * FROM users")
    suspend fun getList(): List<User>

    @Query("SELECT * FROM users WHERE chain_id = :chainId")
    suspend fun getList(chainId: String): List<User>

    @Query("SELECT * FROM users WHERE uid = :id AND chain_id = :chainId")
    suspend fun get(id: Long, chainId: String): User?

    @Query("SELECT * FROM users WHERE name = :name AND chain_id = :chainId")
    suspend fun getByName(name: String, chainId: String): User

    @Query("SELECT * FROM users")
    fun getListLive(): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE chain_id = :chainId")
    fun getListLive(chainId: String): LiveData<List<User>>

    @Query("SELECT * FROM users WHERE uuid = :uuid")
    fun getLive(uuid: String): LiveData<User?>

    @Query("SELECT * FROM users WHERE uid = :id AND chain_id = :chainId")
    fun getLive(id: Long, chainId: String): LiveData<User?>

//    @Query("UPDATE users SET owner_keys = :ownerKeys, active_keys = :activeKeys, memo_keys = :memoKeys WHERE name = :name")
//    fun updateKeys(name: String, ownerKeys: List<ByteArray>?, activeKeys: List<ByteArray>?, memoKeys: List<ByteArray>?)

    @Delete
    suspend fun remove(user: User): Int?

    @Query("DELETE FROM users")
    suspend fun clear()


}