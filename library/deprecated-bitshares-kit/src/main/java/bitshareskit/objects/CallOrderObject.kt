package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optItem
import bitshareskit.extensions.optUShort
import bitshareskit.models.SimplePrice
import org.java_json.JSONObject

@Entity(tableName = CallOrderObject.TABLE_NAME)
data class CallOrderObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id": "1.8.130268",
        "borrower": "1.2.1790308",
        "collateral": 4000000,
        "debt": 31234,
        "call_price": {
            "base": {
                "amount": 1,
                "asset_id": "1.3.0"
            },
            "quote": {
                "amount": 1,
                "asset_id": "1.3.113"
            }
        },
        "target_collateral_ratio": 1600
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "call_order_object"

        @Ignore const val KEY_BORROWER = "borrower"
        @Ignore const val KEY_COLLATERAL = "collateral"
        @Ignore const val KEY_DEBT = "debt"
        @Ignore const val KEY_CALL_PRICE = "call_price"
        @Ignore const val KEY_TARGET_COLLATERAL_RATIO = "target_collateral_ratio"
    }

    @delegate:Ignore val borrower: AccountObject by lazy { rawJson.optGrapheneInstance(KEY_BORROWER) }
    @delegate:Ignore val collateral: Long by lazy { rawJson.optLong(KEY_COLLATERAL) }
    @delegate:Ignore val debt: Long by lazy { rawJson.optLong(KEY_DEBT) }
    @delegate:Ignore val callPrice: SimplePrice by lazy { rawJson.optItem(KEY_CALL_PRICE) }
    @delegate:Ignore val tcr: UShort by lazy { rawJson.optUShort(KEY_TARGET_COLLATERAL_RATIO) }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
