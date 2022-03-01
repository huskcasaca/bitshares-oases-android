package bitshareskit.models

import bitshareskit.extensions.*
import bitshareskit.objects.AccountObject
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.serializer.writeGrapheneSet
import bitshareskit.serializer.writeGrapheneUShort
import bitshareskit.serializer.writeSerializable
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONObject

data class AccountOptions(
    val memoKey: PublicKey,
    val votingAccount: AccountObject,
    val witnessNumber: UShort,
    val committeeNumber: UShort,
    val vote: Set<Vote> = emptySet(),
    val extensions: Set<Extensions> = emptySet(),
) : GrapheneSerializable {

    companion object {
        const val KEY_MEMO_KEY = "memo_key"
        const val KEY_VOTING_ACCOUNT = "voting_account"
        const val KEY_NUM_WITNESS = "num_witness"
        const val KEY_NUM_COMMITTEE = "num_committee"
        const val KEY_VOTES = "votes"
        const val KEY_EXTENSIONS = "extensions"

        fun fromJson(rawJson: JSONObject): AccountOptions {
            return AccountOptions(
                rawJson.optItem(KEY_MEMO_KEY),
                rawJson.optGrapheneInstance(KEY_VOTING_ACCOUNT),
                rawJson.optUShort(KEY_NUM_WITNESS),
                rawJson.optUShort(KEY_NUM_COMMITTEE),
                rawJson.optIterable<String>(KEY_VOTES).mapNotNull { Vote.fromStringId(it).takeIf { it != Vote.EMPTY } }.toSet()
            )
        }
    }

    override fun toByteArray(): ByteArray = buildPacket {
        writeSerializable(memoKey)
        writeSerializable(votingAccount)
        writeGrapheneUShort(witnessNumber)
        writeGrapheneUShort(committeeNumber)
        writeGrapheneSet(vote)
        writeGrapheneSet(extensions)
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
        putSerializable(KEY_MEMO_KEY, memoKey)
        putSerializable(KEY_VOTING_ACCOUNT, votingAccount)
        putNumber(KEY_NUM_WITNESS, witnessNumber)
        putNumber(KEY_NUM_COMMITTEE, committeeNumber)
        putArraySerializable(KEY_VOTES, vote)
        putArray(KEY_EXTENSIONS, extensions)
    }

}