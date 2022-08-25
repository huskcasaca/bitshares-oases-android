package bitshareskit.objects

import androidx.room.ColumnInfo
import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.extensions.parseUrl
import bitshareskit.models.PublicKey
import bitshareskit.models.Vote
import org.java_json.JSONObject

@Entity(tableName = WitnessObject.TABLE_NAME)
data class WitnessObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"1.6.110",
        "witness_account":"1.2.768841",
        "last_aslot":48866810,
        "signing_key":"BTS8WqgFkupvXx7haanZDFMDGdMsPcZkxR3h4YL8m4z63pUG1oREw",
        "pay_vb":"1.13.13442",
        "vote_id":"1:328",
        "total_votes":"94534233046430",
        "url":"https://www.magicwallet.io",
        "total_missed":1303,
        "last_confirmed_block_num":48671976
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "witness_object"

        @Ignore const val KEY_WITNESS_ACCOUNT = "witness_account"
        @Ignore const val KEY_LAST_ASLOT = "last_aslot"
        @Ignore const val KEY_SIGNING_KEY = "signing_key"
        @Ignore const val KEY_PAY_VB = "pay_vb"
        @Ignore const val KEY_VOTE_ID = "vote_id"
        @Ignore const val KEY_TOTAL_VOTES = "total_votes"
        @Ignore const val KEY_URL = "url"
        @Ignore const val KEY_TOTAL_MISSED = "total_missed"
        @Ignore const val KEY_LAST_CONFIRMED_BLOCK_NUM = "last_confirmed_block_num"
    }

    @Ignore var witnessAccount: AccountObject
    @Ignore val lastAslot: Int
    @Ignore val signingKey: PublicKey
    @Ignore val payVestingBalance: VestingBalanceObject
    @Ignore val vote: Vote
    @Ignore val totalVotes: Long
    @Ignore val url: Uri
    @Ignore val totalMissed: Int
    @Ignore val lastConfirmedBlockNum: Int
    @ColumnInfo(name = COLUMN_OWNER_UID) var ownerUid: Long

    init {
        witnessAccount = rawJson.optGrapheneInstance(KEY_WITNESS_ACCOUNT)
        ownerUid = witnessAccount.uid
        lastAslot = rawJson.optInt(KEY_LAST_ASLOT)
        signingKey = rawJson.optItem(KEY_SIGNING_KEY)
        payVestingBalance = rawJson.optGrapheneInstance(KEY_PAY_VB)
        vote = rawJson.optItem(KEY_VOTE_ID)
        totalVotes = rawJson.optLong(KEY_TOTAL_VOTES)
        url = parseUrl(rawJson.optString(KEY_URL))
        totalMissed = rawJson.optInt(KEY_TOTAL_MISSED)
        lastConfirmedBlockNum = rawJson.optInt(KEY_LAST_CONFIRMED_BLOCK_NUM)
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
