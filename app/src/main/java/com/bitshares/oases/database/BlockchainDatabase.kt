package com.bitshares.oases.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import bitshareskit.entities.Block
import bitshareskit.objects.*
import com.bitshares.oases.database.blockchain_daos.*
import com.bitshares.oases.database.converters.JsonConverters
import com.bitshares.oases.database.entities.TickerEntity
import com.bitshares.oases.database.local_daos.TickerDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AccountBalanceObject::class,
        AccountObject::class,
        AccountStatisticsObject::class,
        AccountTransactionHistoryObject::class,
        AssetDynamicData::class,
        AssetBitassetData::class,
        AssetObject::class,
        BalanceObject::class,
        BaseObject::class,
        BlindedBalanceObject::class,
        BlockSummaryObject::class,
        BucketObject::class,
        BudgetRecordObject::class,
        BuybackObject::class,
        CallOrderObject::class,
        ChainPropertyObject::class,
        CollateralBidObject::class,
        CommitteeMemberObject::class,
        CustomAuthorityObject::class,
        CustomObject::class,
        DynamicGlobalPropertyObject::class,
        FbaAccumulatorObject::class,
        ForceSettlementObject::class,
        GlobalPropertyObject::class,
        HtlcObject::class,
        LimitOrderObject::class,
        LiquidityPoolObject::class,
        NullObject::class,
        OperationHistoryObject::class,
        OrderHistoryObject::class,
        ProposalObject::class,
        SpecialAuthorityObject::class,
        TicketObject::class,
        TransactionObject::class,
        VestingBalanceObject::class,
        WithdrawPermissionObject::class,
        WitnessObject::class,
        WitnessScheduleObject::class,
        WorkerObject::class,

        Block::class,
        TickerEntity::class
    ],
    version = 7,
    exportSchema = false
)

@TypeConverters(JsonConverters::class)
abstract class BlockchainDatabase : RoomDatabase() {

    abstract fun accountBalanceDao(): AccountBalanceDao
    abstract fun accountDao(): AccountDao
    abstract fun accountStatisticsDao(): AccountStatisticsDao
    abstract fun accountTransactionHistoryDao(): AccountTransactionHistoryDao
    abstract fun assetBitassetDataDao(): AssetBitassetDataDao
    abstract fun assetDynamicDataDao(): AssetDynamicDataDao
    abstract fun assetDao(): AssetDao
    abstract fun balanceDao(): BalanceDao
    abstract fun baseDao(): BaseDao
    abstract fun blindedBalanceDao(): BlindedBalanceDao
    abstract fun blockSummaryDao(): BlockSummaryDao
    abstract fun bucketDao(): BucketDao
    abstract fun budgetRecordDao(): BudgetRecordDao
    abstract fun buybackDao(): BuybackDao
    abstract fun callOrderDao(): CallOrderDao
    abstract fun chainPropertyDao(): ChainPropertyDao
    abstract fun collateralBidDao(): CollateralBidDao
    abstract fun committeeMemberDao(): CommitteeMemberDao
    abstract fun customAuthorityDao(): CustomAuthorityDao
    abstract fun customDao(): CustomDao
    abstract fun dynamicGlobalPropertyDao(): DynamicGlobalPropertyDao
    abstract fun fbaAccumulatorDao(): FbaAccumulatorDao
    abstract fun forceSettlementDao(): ForceSettlementDao
    abstract fun globalPropertyDao(): GlobalPropertyDao
    abstract fun htlcDao(): HtlcDao
    abstract fun limitOrderDao(): LimitOrderDao
    abstract fun liquidityPoolDao(): LiquidityPoolDao
    abstract fun nullDao(): NullDao
    abstract fun operationHistoryDao(): OperationHistoryDao
    abstract fun orderHistoryDao(): OrderHistoryDao
    abstract fun proposalDao(): ProposalDao
    abstract fun specialAuthorityDao(): SpecialAuthorityDao
    abstract fun ticketDao(): TicketDao
    abstract fun transactionDao(): TransactionDao
    abstract fun vestingBalanceDao(): VestingBalanceDao
    abstract fun withdrawPermissionDao(): WithdrawPermissionDao
    abstract fun witnessDao(): WitnessDao
    abstract fun witnessScheduleDao(): WitnessScheduleDao
    abstract fun workerDao(): WorkerDao

    abstract fun blockDao(): BlockDao
    abstract fun tickerDao(): TickerDao

    companion object {
        const val DB_NAME = "bitshares_blockchain_data.db"

        // To make sure there is always only one instance of the database open
        @Volatile
        lateinit var INSTANCE: BlockchainDatabase

        internal fun initialize(context: Context): BlockchainDatabase? {
            if (::INSTANCE.isInitialized) return INSTANCE
            synchronized(BlockchainDatabase::class.java) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, BlockchainDatabase::class.java, DB_NAME).addCallback(databaseCallback).build()
            }
            return INSTANCE
        }


        private var databaseCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE.accountDao().clear()

//                    instance!!.nodeDao().apply {
//                        addNode(Node(1,"Roelandp", "wss://btsws.roelandp.nl/ws"))
//                        addNode(Node(2,"Witness abit", "wss://api.bts.mobi/ws"))
//                        addNode(Node(3,"Witness yao", "wss://kimziv.com/ws"))
////                        addNode(Node(4,"Witness hiblockchain", "wss://api.bts.ai"))
//                        addNode(Node(5,"delegate-zhaomu", "wss://blockzms.xyz/ws"))
//                        addNode(Node(6,"xn-delegate", "wss://api.btsgo.net/ws"))
////                        addNode(Node(7,"crazybit ", "wss://crazybit.online"))
//                    }
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
