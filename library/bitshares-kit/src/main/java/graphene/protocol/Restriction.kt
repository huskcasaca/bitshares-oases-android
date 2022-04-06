package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias Restriction = @Serializable(with = ArgumentTypeSerializer::class) Any
typealias VariantAssertArgumentType = FlatPair<Int64, List<Restriction>>

@Serializable
internal enum class FunctionType {
    @SerialName("func_eq,") FUNC_EQ,
    @SerialName("func_ne,") FUNC_NE,
    @SerialName("func_lt,") FUNC_LT,
    @SerialName("func_le,") FUNC_LE,
    @SerialName("func_gt,") FUNC_GT,
    @SerialName("func_ge,") FUNC_GE,
    @SerialName("func_in,") FUNC_IN,
    @SerialName("func_not_in,") FUNC_NOT_IN,
    @SerialName("func_has_all,") FUNC_HAS_ALL,
    @SerialName("func_has_none,") FUNC_HAS_NONE,
    @SerialName("func_attr,") FUNC_ATTR,
    @SerialName("func_logical_or,") FUNC_LOGICAL_OR,
    @SerialName("func_variant_assert,") FUNC_VARIANT_ASSERT,
}

object ArgumentTypeSerializer : StaticVarSerializer<Any>(
    listOf(
        /*  0 */ Unit::class,
        /*  1 */ Boolean::class,
        /*  2 */ Int64::class,
        /*  3 */ String::class,
        /*  4 */ Instant::class, // TODO
        /*  5 */ PublicKeyType::class,
        /*  6 */ String::class, // Sha256::class
        /*  7 */ AccountIdType::class,
        /*  8 */ AssetIdType::class,
        /*  9 */ ForceSettlementIdType::class,
        /* 10 */ CommitteeMemberIdType::class,
        /* 11 */ WitnessIdType::class,
        /* 12 */ LimitOrderIdType::class,
        /* 13 */ CallOrderIdType::class,
        /* 14 */ CustomIdType::class,
        /* 15 */ ProposalIdType::class,
        /* 16 */ WithdrawPermissionIdType::class,
        /* 17 */ VestingBalanceIdType::class,
        /* 18 */ WorkerIdType::class,
        /* 19 */ BalanceIdType::class,
        /* 20 */ BooleanFlatSet::class,
        /* 21 */ Int64FlatSet::class,
        /* 22 */ StringFlatSet::class,
        /* 23 */ InstantFlatSet::class,
        /* 24 */ PublicKeyTypeFlatSet::class,
        /* 25 */ Ssha256FlatSet::class,
        /* 26 */ AccountIdTypeFlatSet::class,
        /* 27 */ AssetIdTypeFlatSet::class,
        /* 28 */ ForceSettlementIdTypeFlatSet::class,
        /* 29 */ CommitteeMemberIdTypeFlatSet::class,
        /* 30 */ WitnessIdTypeFlatSet::class,
        /* 31 */ LimitOrderIdTypeFlatSet::class,
        /* 32 */ CallOrderIdTypeFlatSet::class,
        /* 33 */ CustomIdTypeFlatSet::class,
        /* 34 */ ProposalIdTypeFlatSet::class,
        /* 35 */ WithdrawPermissionIdTypeFlatSet::class,
        /* 36 */ VestingBalanceIdTypeFlatSet::class,
        /* 37 */ WorkerIdTypeFlatSet::class,
        /* 38 */ BalanceIdTypeFlatSet::class,
        /* 39 */ RestrictionList::class,
        /* 40 */ RestrictionListList::class,
//        /* 41 */ VariantAssertArgumentType::class,
    )
)

internal class BooleanFlatSet
internal class Int64FlatSet
internal class StringFlatSet
internal class InstantFlatSet
internal class PublicKeyTypeFlatSet
internal class Ssha256FlatSet
internal class AccountIdTypeFlatSet
internal class AssetIdTypeFlatSet
internal class ForceSettlementIdTypeFlatSet
internal class CommitteeMemberIdTypeFlatSet
internal class WitnessIdTypeFlatSet
internal class LimitOrderIdTypeFlatSet
internal class CallOrderIdTypeFlatSet
internal class CustomIdTypeFlatSet
internal class ProposalIdTypeFlatSet
internal class WithdrawPermissionIdTypeFlatSet
internal class VestingBalanceIdTypeFlatSet
internal class WorkerIdTypeFlatSet
internal class BalanceIdTypeFlatSet
internal class RestrictionList
internal class RestrictionListList

