package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.Serializable

@Serializable(with = PredicateSerializer::class)
sealed class Predicate

/**
 *  Used to verify that account_id->name is equal to the given string literal.
 */
@Serializable
data class AccountNameEqLitPredicate(
    val account_id: AccountIdType,
    val name: String,
) : Predicate()

/**
 *  Used to verify that asset_id->symbol is equal to the given string literal.
 */
@Serializable
data class AssetSymbolEqLitPredicate(
    val asset_id: AssetIdType,
    val symbol: String,
) : Predicate()
/**
 * Used to verify that a specific block is part of the
 * blockchain history.  This helps protect some high-value
 * transactions to newly created IDs
 *
 * The block ID must be within the last 2^16 blocks.
 */
@Serializable
data class BlockIdPredicate(
    val id: BlockIdType,
) : Predicate()

object PredicateSerializer : StaticVarSerializer<Predicate>(
    listOf(
        AccountNameEqLitPredicate::class,
        AssetSymbolEqLitPredicate::class,
        BlockIdPredicate::class,
    )
)
