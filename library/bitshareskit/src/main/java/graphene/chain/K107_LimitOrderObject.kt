package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K107_LimitOrderObject(
    @SerialName("id")
    override val id: LimitOrderId,
    @SerialName("expiration") @Serializable(TimePointSecSerializer::class)
    val expiration: Instant,
    @SerialName("seller")
    val seller: AccountIdType,
    @SerialName("for_sale")
    val forSale: ShareType, // asset id is sell_price.base.asset_id
    @SerialName("sell_price")
    val sellPrice: PriceType,
    @SerialName("deferred_fee")
    val deferredFee: ShareType, // fee converted to CORE
    @SerialName("deferred_paid_fee")
    val deferredPaidFee: Asset, // originally paid fee
    @SerialName("is_settled_debt")
    val isSettledDebt: Boolean = false, // Whether this order is an individual settlement fund
) : AbstractObject(), LimitOrderIdType {

//    pair<asset_id_type,asset_id_type> get_market()const
//    {
//        auto tmp = std::make_pair( sell_price.base.asset_id, sell_price.quote.asset_id );
//        if( tmp.first > tmp.second ) std::swap( tmp.first, tmp.second );
//        return tmp;
//    }
//
//    asset amount_for_sale()const   { return asset( for_sale, sell_price.base.asset_id ); }
//    asset amount_to_receive()const { return amount_for_sale() * sell_price; }
//    asset_id_type sell_asset_id()const    { return sell_price.base.asset_id;  }
//    asset_id_type receive_asset_id()const { return sell_price.quote.asset_id; }

}



