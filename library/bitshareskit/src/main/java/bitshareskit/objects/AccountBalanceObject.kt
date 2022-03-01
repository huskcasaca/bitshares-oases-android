package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.models.AssetAmount
import org.java_json.JSONObject

@Entity(tableName = AccountBalanceObject.TABLE_NAME)
data class AccountBalanceObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "account_balance_object"
        @Ignore const val KEY_OWNER = "owner"
        @Ignore const val KEY_ASSET_TYPE = "asset_type"
        @Ignore const val KEY_BALANCE = "balance"
        @Ignore const val KEY_MAINTENANCE_FLAG = "maintenance_flag"
    }

    @Ignore val owner: AccountObject
    @Ignore var asset: AssetObject
    @Ignore val balance: Long
    @Ignore val maintenance: Boolean
    @ColumnInfo(name = COLUMN_OWNER_UID) var ownerUid: Long
    @ColumnInfo(name = "asset_uid") var assetUid: Long

    val amount get() = AssetAmount(balance, asset)

    init {
        owner = rawJson.optGrapheneInstance(KEY_OWNER)
        ownerUid = owner.uid
        asset = rawJson.optGrapheneInstance(KEY_ASSET_TYPE)
        assetUid = asset.uid
        balance = rawJson.optLong(KEY_BALANCE)
        maintenance = rawJson.optBoolean(KEY_BALANCE)
    }

    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}


