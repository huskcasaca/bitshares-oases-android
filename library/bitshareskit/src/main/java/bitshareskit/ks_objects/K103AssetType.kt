package bitshareskit.ks_objects

import bitshareskit.ks_object_base.*
import kotlinx.serialization.SerialName

interface K103AssetType: K000AbstractType {
    override val id: K103AssetId            get() = this as K103AssetId
    val symbol: String                      get() = emptyString()
    val issuerId: K102AccountType           get() = emptyIdType()
    val precision: UByte                    get() = 0U
    val options: K103AssetObject.Options    get() = emptyComponent()
    val dynamicDataId: K203AssetDynamicType get() = emptyIdType()
//    @SerialName(KEY_BITASSET_DATA_ID) var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}