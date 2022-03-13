package bitshareskit.ks_object_type

import bitshareskit.ks_object_base.*
import bitshareskit.ks_objects.K103AssetObject

interface K103AssetType: K000AbstractType {
    override val id: K103AssetId            get() = this as K103AssetId
    val symbol: String                      get() = emptyString()
    val issuerId: K102AccountType           get() = emptyIdType()
    val precision: UByte                    get() = 0U
    val options: K103AssetObject.Options    get() = emptyComponent()
    val dynamicDataId: K203AssetDynamicType get() = emptyIdType()
//    var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}