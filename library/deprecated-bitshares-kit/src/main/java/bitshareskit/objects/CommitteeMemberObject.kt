package bitshareskit.objects

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.createGrapheneEmptyInstance
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.extensions.parseUrl
import bitshareskit.models.Vote
import org.java_json.JSONObject

@Entity(tableName = CommitteeMemberObject.TABLE_NAME)
data class CommitteeMemberObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"1.5.52",
        "committee_member_account":"1.2.1692677",
        "vote_id":"0:682",
        "total_votes":"48600896831840",
        "url":"https://bitsharestalk.org/index.php?topic=29889.msg336869#msg336869"
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "committee_member_object"

        @Ignore const val KEY_COMMITTEE_MEMBER_ACCOUNT = "committee_member_account"
        @Ignore const val KEY_VOTE_ID = "vote_id"
        @Ignore const val KEY_TOTAL_VOTES = "total_votes"
        @Ignore const val KEY_URL = "url"

        val EMPTY: CommitteeMemberObject = createGrapheneEmptyInstance()
    }

    @Ignore val committeeMemberAccount: AccountObject
    @Ignore val vote: Vote
    @Ignore val totalVotes: Long
    @Ignore val url: Uri
    @ColumnInfo(name = COLUMN_OWNER_UID) var ownerUid: Long

    init {
        committeeMemberAccount = rawJson.optGrapheneInstance(KEY_COMMITTEE_MEMBER_ACCOUNT)
        ownerUid = committeeMemberAccount.uid
        vote = rawJson.optItem(KEY_VOTE_ID)
        totalVotes = rawJson.optLong(KEY_TOTAL_VOTES)
        url = parseUrl(rawJson.optString(KEY_URL))
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
