package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import org.java_json.JSONObject

@Entity(tableName = AssetDynamicData.TABLE_NAME)
data class AssetDynamicData(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    /*{
        "id":"2.3.985",
        "current_supply":35925155,
        "confidential_supply":0,
        "accumulated_fees":31456,
        "accumulated_collateral_fees":0,
        "fee_pool":975
    }*/

    companion object {
        const val TABLE_NAME = "asset_dynamic_data"

        const val KEY_CURRENT_SUPPLY = "current_supply"
        const val KEY_CONFIDENTIAL_SUPPLY = "confidential_supply"
        const val KEY_ACCUMULATED_FEES = "accumulated_fees"
        const val KEY_ACCUMULATED_COLLATERAL_FEES = "accumulated_collateral_fees"
        const val KEY_FEE_POOL = "fee_pool"
    }

    @Ignore val currentSupply: Long
    @Ignore val confidentialSupply: Long
    @Ignore val accumulatedFees: Long
    @Ignore val accumulatedCollateralFees: Long
    @Ignore val feePool: Long

    init {
        currentSupply = rawJson.optLong(KEY_CURRENT_SUPPLY)
        confidentialSupply = rawJson.optLong(KEY_CONFIDENTIAL_SUPPLY)
        accumulatedFees = rawJson.optLong(KEY_ACCUMULATED_FEES)
        accumulatedCollateralFees = rawJson.optLong(KEY_ACCUMULATED_COLLATERAL_FEES)
        feePool = rawJson.optLong(KEY_FEE_POOL)
    }


    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}
