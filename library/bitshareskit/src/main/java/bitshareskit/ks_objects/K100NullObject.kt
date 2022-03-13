package bitshareskit.ks_objects

import bitshareskit.ks_object_base.*
import bitshareskit.ks_object_type.K100NullType
import bitshareskit.ks_object_type.K101BaseType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KNullObject(
    @SerialName(KEY_ID) override val id: K100NullId = emptyIdType(),
) : K000AbstractObject(), K100NullType {

}

