package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.java_json.JSONObject

@Entity(tableName = ChainPropertyObject.TABLE_NAME)
data class ChainPropertyObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {

        @Ignore const val TABLE_NAME = "chain_property_object"
        @Ignore const val KEY_CHAIN_ID = "chain_id"
        @Ignore const val KEY_IMMUTABLE_PARAMETERS = "immutable_parameters"
        @Ignore const val KEY_MIN_COMMITTEE_MEMBER_COUNT = "min_committee_member_count"
        @Ignore const val KEY_MIN_WITNESS_COUNT = "min_witness_count"
        @Ignore const val KEY_NUM_SPECIAL_ACCOUNTS = "num_special_accounts"
        @Ignore const val KEY_NUM_SPECIAL_ASSETS = "num_special_assets"

    }

    @Ignore val chainId: String = rawJson.optString(KEY_CHAIN_ID)
    @Ignore val minCommitteeMemberCount: Int
    @Ignore val minWitnessCount: Int
    @Ignore val specialAccountsNum: Int
    @Ignore val specialAssetsNum: Int

    init {
        rawJson.optJSONObject(KEY_IMMUTABLE_PARAMETERS).apply {
            minCommitteeMemberCount = optInt(KEY_MIN_COMMITTEE_MEMBER_COUNT)
            minWitnessCount = optInt(KEY_MIN_WITNESS_COUNT)
            specialAccountsNum = optInt(KEY_NUM_SPECIAL_ACCOUNTS)
            specialAssetsNum = optInt(KEY_NUM_SPECIAL_ASSETS)
        }

    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
