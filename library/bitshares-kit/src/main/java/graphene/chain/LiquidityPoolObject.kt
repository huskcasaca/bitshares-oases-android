package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K119_LiquidityPoolObject(
    @SerialName("id")
    override val id: LiquidityPoolId,
    @SerialName("asset_a")
    val firstAsset: AssetIdType, //< Type of the first asset in the pool
    @SerialName("asset_b")
    val secondAsset: AssetIdType, //< Type of the second asset in the pool
    @SerialName("balance_a")
    val firstBalance: ShareType, //< The balance of the first asset in the pool
    @SerialName("balance_b")
    val secondBalance: ShareType, //< The balance of the second asset in the pool
    @SerialName("share_asset")
    val shareAsset: AssetIdType, //< Type of the share asset aka the LP token
    @SerialName("taker_fee_percent")
    val takerFeePercent: UInt16, // = 0 //< Taker fee percent
    @SerialName("withdrawal_fee_percent")
    val withdrawalFeePercent: UInt16, // = 0 ///< Withdrawal fee percent
    @SerialName("virtual_value")
    val virtualValue: UInt128, // = 0 ///< Virtual value of the pool
) : AbstractObject(), LiquidityPoolIdType {

//    void update_virtual_value()
//    {
//        virtual_value = fc::uint128_t( balance_a.value ) * balance_b.value;
//    }

}


