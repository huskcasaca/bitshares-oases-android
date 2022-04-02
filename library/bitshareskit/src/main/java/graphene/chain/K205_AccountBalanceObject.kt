package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K205_AccountBalanceObject(
    @SerialName("id")
    override val id: K205_AccountBalanceIdType,
    @SerialName("owner")
    val owner: K102_AccountType,
    @SerialName("asset_type")
    val assetType: K103_AssetType,
    @SerialName("balance")
    val balance: share_type,
    @SerialName("maintenance_flag")
    val maintenanceFlag: Boolean = false,  // Whether need to process this balance object in maintenance interval
) : AbstractObject(), K205_AccountBalanceType {

//    public:
//    static constexpr uint8_t space_id = implementation_ids;
//    static constexpr uint8_t type_id  = impl_account_balance_object_type;
//

//
//    asset get_balance()const { return asset(balance, asset_type); }
//    void  adjust_balance(const asset& delta);

}

