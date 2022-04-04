package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K205_AccountBalanceObject(
    @SerialName("id")
    override val id: AccountBalanceId,
    @SerialName("owner")
    override val owner: AccountIdType,
    @SerialName("asset_type")
    override val asset: AssetIdType,
    @SerialName("balance")
    override val balance: ShareType,
    @SerialName("maintenance_flag")
    override val maintenanceFlag: Boolean = false,  // Whether need to process this balance object in maintenance interval
) : AbstractObject(), AccountBalanceIdType {

//    public:
//    static constexpr uint8_t space_id = implementation_ids;
//    static constexpr uint8_t type_id  = impl_account_balance_object_type;
//

//
//    asset get_balance()const { return asset(balance, asset_type); }
//    void  adjust_balance(const asset& delta);

}

