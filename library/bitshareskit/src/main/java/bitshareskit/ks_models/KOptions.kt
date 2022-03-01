package bitshareskit.ks_models

import bitshareskit.ks_object_base.K102AccountId
import bitshareskit.ks_object_base.KGrapheneIdSerializer
import bitshareskit.ks_object_base.UInt16
import bitshareskit.ks_object_base.emptyIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KOptions(
    @SerialName(KEY_MEMO_KEY) val memoKey: KPublicKey = KPublicKey.EMPTY,
    @SerialName(KEY_VOTING_ACCOUNT) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") val votingAccount: K102AccountId = emptyIdType(),
    @SerialName(KEY_NUM_WITNESS) val witnessNumber: UInt16 = 0U,
    @SerialName(KEY_NUM_COMMITTEE) val committeeNumber: UInt16 = 0U,
    @SerialName(KEY_VOTES) val vote: Set<KVote> = emptySet(),
    @SerialName(KEY_EXTENSIONS) val extensions: Set<KExtensions> = emptySet(),
) : GrapheneComponent{

    companion object {
        const val KEY_MEMO_KEY = "memo_key"
        const val KEY_VOTING_ACCOUNT = "voting_account"
        const val KEY_NUM_WITNESS = "num_witness"
        const val KEY_NUM_COMMITTEE = "num_committee"
        const val KEY_VOTES = "votes"
        const val KEY_EXTENSIONS = "extensions"

        val EMPTY = KOptions()

    }

}