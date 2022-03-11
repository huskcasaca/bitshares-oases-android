package com.bitshares.oases.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.converters.BooleanListConverters
import com.bitshares.oases.database.converters.KeyListConverters
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.local_daos.NodeDao
import com.bitshares.oases.database.local_daos.UserDao
import com.bitshares.oases.preference.old.Settings
import kotlinx.coroutines.launch

@Database(
    entities = [
        Node::class,
        User::class
    ],
    version = 7,
    exportSchema = false
)
@TypeConverters(
    KeyListConverters::class, BooleanListConverters::class
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
    abstract fun userDao(): UserDao

    companion object {
        const val DB_NAME = "bitshares_local_data.db"

        @Volatile
        lateinit var INSTANCE: LocalDatabase

        internal fun initialize(context: Context): LocalDatabase? {
            if (::INSTANCE.isInitialized) return INSTANCE
            synchronized(LocalDatabase::class.java) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, LocalDatabase::class.java, DB_NAME).addCallback(databaseCallback).build()
            }
            return INSTANCE
        }

        private var databaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                blockchainDatabaseScope.launch {
                    INSTANCE.nodeDao().apply {
//                        addNode(Node(1, "xbts testnet", "wss://testnet.xbts.io/ws"))
                        addNode(Node(2, "gdex", "wss://ws.gdex.top/"))
                        addNode(Node(3, "Roelandp", "wss://btsws.roelandp.nl/ws"))
                        addNode(Node(4, "Witness abit", "wss://api.bts.mobi/ws"))
                        addNode(Node(5, "Witness yao", "wss://kimziv.com/ws"))
//                        addNode(Node(4,"Witness hiblockchain", "wss://api.bts.ai"))
                        addNode(Node(6, "delegate-zhaomu", "wss://blockzms.xyz/ws"))
                        addNode(Node(7, "xn-delegate", "wss://api.btsgo.net/ws"))
//                        addNode(Node(7,"crazybit ", "wss://crazybit.online"))
                    }
                    Settings.KEY_CURRENT_NODE_ID.value = 1
                }
            }

            // PP private nodes
//            "wss://nl.palmpay.io/ws",
//
//            // Other public nodes
//            "wss://btsws.roelandp.nl/ws",
//            "wss://api.bts.mobi/ws",
//            "wss://kimziv.com/ws",
//            "wss://api.bts.ai")
            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

    }


}
