package bitshareskit.ks_objects

import bitshareskit.ks_object_base.*
import bitshareskit.ks_object_type.K101BaseType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K101BaseObject(
    @SerialName(KEY_ID) override val id: K101BaseId = emptyIdType(),
) : K000AbstractObject(), K101BaseType {

}

