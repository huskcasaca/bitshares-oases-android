package graphene.chain

import graphene.protocol.*
import graphene.protocol.uint16_t
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImmutableChainParameters(
    @SerialName("min_committee_member_count")
    val min_committee_member_count: uint16_t = GRAPHENE_DEFAULT_MIN_COMMITTEE_MEMBER_COUNT,
    @SerialName("min_witness_count")
    val min_witness_count: uint16_t = GRAPHENE_DEFAULT_MIN_WITNESS_COUNT,
    @SerialName("num_special_accounts")
    val num_special_accounts: uint32_t = 0U,
    @SerialName("num_special_assets")
    val num_special_assets: uint32_t = 0U,
) {
}