package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K108_CallOrderObject(
    @SerialName("id")
    override val id: CallOrderId,
    @SerialName("borrower")
    val borrower: AccountIdType,
    @SerialName("collateral")
    val collateral: ShareType, // call_price.base.asset_id, access via get_collateral
    @SerialName("debt")
    val debt: ShareType, // call_price.quote.asset_id, access via get_debt
    @SerialName("call_price")
    val callPrice: PriceType, // Collateral / Debt
    @SerialName("target_collateral_ratio")
    val targetCollateralRatio: Optional<UInt16> = optional(), //< maximum CR to maintain when selling collateral on margin call
) : AbstractObject(), CallOrderIdType {
//
//    asset get_collateral()const { return asset( collateral, call_price.base.asset_id ); }
//    asset get_debt()const { return asset( debt, debt_type() ); }
//    asset amount_to_receive()const { return get_debt(); }
//    asset_id_type debt_type()const { return call_price.quote.asset_id; }
//    asset_id_type collateral_type()const { return call_price.base.asset_id; }
//    price collateralization()const { return get_collateral() / get_debt(); }
//
//    pair<asset_id_type,asset_id_type> get_market()const
//    {
//        auto tmp = std::make_pair( call_price.base.asset_id, call_price.quote.asset_id );
//        if( tmp.first > tmp.second ) std::swap( tmp.first, tmp.second );
//        return tmp;
//    }
//
//    /**
//     *  Calculate maximum quantity of debt to cover to satisfy @ref target_collateral_ratio.
//     *
//     *  @param match_price the matching price if this call order is margin called
//     *  @param feed_price median settlement price of debt asset
//     *  @param maintenance_collateral_ratio median maintenance collateral ratio of debt asset
//     *  @param maintenance_collateralization maintenance collateralization of debt asset,
//     *                                       should only be valid after core-1270 hard fork
//     *  @return maximum amount of debt that can be called
//     */
//    share_type get_max_debt_to_cover( price match_price,
//    price feed_price,
//    const uint16_t maintenance_collateral_ratio,
//    const optional<price>& maintenance_collateralization = optional<price>()
//    )const;

}



