package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K103AssetObject(
    @SerialName(KEY_ID) override val id: K103_AssetIdType = emptyIdType(),
    @SerialName(KEY_SYMBOL) override val symbol: String = emptyString(),
    @SerialName(KEY_ISSUER) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val issuerId: K102_AccountType = emptyIdType(),
    @SerialName(KEY_PRECISION) override val precision: UByte = 0U,
    @SerialName(KEY_OPTIONS) override val options: AssetOptions = emptyComponent(),
    @SerialName(KEY_DYNAMIC_ASSET_DATA_ID) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val dynamicDataId: K203_AssetDynamicType = emptyIdType(),
//    @SerialName(KEY_BITASSET_DATA_ID) var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
) : K000AbstractObject(), K103_AssetType {


    companion object {
        const val TABLE_NAME = "asset_object"

        const val KEY_SYMBOL = "symbol"
        const val KEY_PRECISION = "precision"
        const val KEY_ISSUER = "issuer"
        const val KEY_OPTIONS = "options"
        const val KEY_REWARD_PERCENT = "reward_percent"

        const val KEY_DYNAMIC_ASSET_DATA_ID = "dynamic_asset_data_id"
        const val KEY_BITASSET_DATA_ID = "bitasset_data_id"

    }



}
