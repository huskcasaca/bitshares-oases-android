package bitshareskit.ks_objects

import bitshareskit.ks_object_base.K100NullId
import bitshareskit.ks_object_base.K103AssetId

interface K100NullType: K000AbstractType {
    override val id: K100NullId get() = this as K100NullId
}