package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.extensions.optGrapheneInstance
import bitshareskit.extensions.optGrapheneTime
import bitshareskit.extensions.optItem
import bitshareskit.models.SimplePrice
import org.java_json.JSONObject
import java.util.*

@Entity(tableName = LimitOrderObject.TABLE_NAME)
data class LimitOrderObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id": "1.7.446997962",
        "expiration": "2022-08-10T06:20:53",
        "seller": "1.2.1065043",
        "for_sale": 100,
        "sell_price": {
            "base": {
                "amount": 100,
                "asset_id": "1.3.0"
            },
            "quote": {
                "amount": 10,
                "asset_id": "1.3.113"
            }
        },
        "deferred_fee": 4826,
        "deferred_paid_fee": {
            "amount": 0,
            "asset_id": "1.3.0"
        }
    }*/

    companion object {
        @Ignore const val TABLE_NAME = "limit_order_object"

        @Ignore const val KEY_EXPIRATION = "expiration"
        @Ignore const val KEY_SELLER = "seller"
        @Ignore const val KEY_FOR_SALE = "for_sale"
        @Ignore const val KEY_SELL_PRICE = "sell_price"
        @Ignore const val KEY_DEFERRED_FEE = "deferred_fee"
        @Ignore const val KEY_DEFERRED_PAID_FEE = "deferred_paid_fee"
    }

    @delegate:Ignore val expiration: Date by lazy { rawJson.optGrapheneTime(KEY_EXPIRATION) }
    @delegate:Ignore val seller: AccountObject by lazy { rawJson.optGrapheneInstance(KEY_SELLER) }
    @delegate:Ignore val sales: Long by lazy { rawJson.optLong(KEY_FOR_SALE) }
    @delegate:Ignore val salePrice: SimplePrice by lazy { rawJson.optItem(KEY_SELL_PRICE) }
    @delegate:Ignore val deferredFee: Long by lazy { rawJson.optLong(KEY_DEFERRED_FEE) }
    @delegate:Ignore val deferredPaidFee: SimplePrice by lazy { rawJson.optItem(KEY_DEFERRED_PAID_FEE) }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
