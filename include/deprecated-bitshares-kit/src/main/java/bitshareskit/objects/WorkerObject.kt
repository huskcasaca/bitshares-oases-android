package bitshareskit.objects

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optGrapheneTime
import bitshareskit.extensions.optItem
import bitshareskit.extensions.parseUrl
import bitshareskit.models.Vote
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = WorkerObject.TABLE_NAME)
data class WorkerObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {
    /*{
        "id":"1.14.243",
        "worker_account":"1.2.364315",
        "work_begin_date":"2020-01-15T00:00:00",
        "work_end_date":"2020-12-31T23:59:59",
        "daily_pay":1472300000,
        "worker":[
            1,
            {
                "balance":"1.13.27081"
            }],
        "vote_for":"2:700",
        "vote_against":"2:701",
        "total_votes_for":"15784243936793",
        "total_votes_against":0,
        "name":"202001-bitshares-legal-representative",
        "url":"https://www.bitshares.foundation/workers/2020-01-bitshares-legal-representative"
    }*/
    companion object {
        @Ignore const val TABLE_NAME = "worker_object"

        @Ignore const val KEY_WORKER_ACCOUNT = "worker_account"
        @Ignore const val KEY_WORK_BEGIN_DATE = "work_begin_date"
        @Ignore const val KEY_WORK_END_DATE = "work_end_date"
        @Ignore const val KEY_DAILY_PAY = "daily_pay"
        @Ignore const val KEY_WORKER = "worker"
        @Ignore const val KEY_BALANCE = "balance"
        @Ignore const val TOTAL_BURNED = "total_burned"
        @Ignore const val KEY_VOTE_FOR = "vote_for"
        @Ignore const val KEY_VOTE_AGAINST = "vote_against"
        @Ignore const val KEY_TOTAL_VOTES_FOR = "total_votes_for"
        @Ignore const val KEY_TOTAL_VOTES_AGAINST = "total_votes_against"
        @Ignore const val KEY_NAME = "name"
        @Ignore const val KEY_URL = "url"

        @Ignore const val WORKER_TYPE_REFUND = 0
        @Ignore const val WORKER_TYPE_VESTING = 1
        @Ignore const val WORKER_TYPE_BURN = 2
        @Ignore const val WORKER_TYPE_EMPTY = 3

    }

    @Ignore var workerAccount: AccountObject
    @Ignore val workBeginDate: Date
    @Ignore val workEndDate: Date
    @Ignore val dailyPay: Long
    @Ignore val workerType: Int
    @Ignore val voteFor: Vote
    @Ignore val voteAgainst: Vote
    @Ignore val totalVotesFor: Long
    @Ignore val totalVotesAgainst: Long
    @Ignore val name: String
    @Ignore val url: Uri
    @ColumnInfo(name = COLUMN_OWNER_UID) var ownerUid: Long

    init {
        workerAccount = rawJson.optGrapheneInstance(KEY_WORKER_ACCOUNT)
        ownerUid = workerAccount.uid
        workBeginDate = rawJson.optGrapheneTime(KEY_WORK_BEGIN_DATE)
        workEndDate = rawJson.optGrapheneTime(KEY_WORK_END_DATE)
        dailyPay = rawJson.optLong(KEY_DAILY_PAY)
        workerType = rawJson.optJSONArray(KEY_WORKER).optInt(0, WORKER_TYPE_EMPTY)
        voteFor = rawJson.optItem(KEY_VOTE_FOR)
        voteAgainst = Vote.fromStringId(rawJson.optString(KEY_VOTE_AGAINST))
        totalVotesFor = rawJson.optLong(KEY_TOTAL_VOTES_FOR)
        totalVotesAgainst = rawJson.optLong(KEY_TOTAL_VOTES_AGAINST)
        name = rawJson.optString(KEY_NAME)
        url = parseUrl(rawJson.optString(KEY_URL))
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
