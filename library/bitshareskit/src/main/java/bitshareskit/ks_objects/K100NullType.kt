package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K100NullId
import bitshareskit.ks_object_base.K103AssetId
import bitshareskit.ks_object_base.ObjectSpace
import bitshareskit.ks_object_base.ObjectType

interface K100NullType: K000AbstractType {
    override val id: K100NullId     get() = this as K100NullId
}