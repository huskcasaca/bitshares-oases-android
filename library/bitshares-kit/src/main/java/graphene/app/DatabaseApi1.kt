package graphene.app


val API_TYPE_MAP = mapOf(
    APIType.BLOCK               to LoginAPI.BLOCK,
    APIType.NETWORK_BROADCAST   to LoginAPI.NETWORK_BROADCAST,
    APIType.DATABASE            to LoginAPI.DATABASE,
    APIType.HISTORY             to LoginAPI.HISTORY,
    APIType.NETWORK_NODE        to LoginAPI.NETWORK_NODE,
    APIType.CRYPTO              to LoginAPI.CRYPTO,
    APIType.ASSET               to LoginAPI.ASSET,
    APIType.ORDERS              to LoginAPI.ORDERS,
)

enum class APIType {
    EMPTY,                  // 0x00
    LOGIN,                  // 0x01
    BLOCK,                  // 0x02
    NETWORK_BROADCAST,      // 0x03
    DATABASE,               // 0x04
    HISTORY,                // 0x05
    NETWORK_NODE,           // 0x06
    CRYPTO,                 // 0x07
    ASSET,                  // 0x08
    ORDERS;                 // 0x09

}


interface API {
    val type: APIType
    val nameString: String
}
enum class EmptyAPI: API {                  // 0x00
    ;
    override val type: APIType = APIType.EMPTY
    override val nameString: String = name.lowercase()
}

enum class LoginAPI: API {                  // 0x01
    LOGIN,
    BLOCK,
    NETWORK_BROADCAST,
    DATABASE,
    HISTORY,
    NETWORK_NODE,
    CRYPTO,
    ASSET,
    ORDERS,
    DEBUG;
    override val type: APIType = APIType.LOGIN
    override val nameString: String = name.lowercase()
}
enum class BlockAPI: API {                  // 0x02
    ;
    override val type: APIType = APIType.BLOCK
    override val nameString: String = name.lowercase()
}
enum class NetworkBroadcastAPI: API {      // 0x03
    ;
    override val type: APIType = APIType.NETWORK_BROADCAST
    override val nameString: String = name.lowercase()
}
enum class DatabaseAPI: API {               // 0x04

    /** Database API +1 */
    GET_OBJECTS,                            // Objects
    SET_SUBSCRIBE_CALLBACK,                 // Subscriptions
    SET_AUTO_SUBSCRIPTION,
    SET_PENDING_TRANSACTION_CALLBACK,
    SET_BLOCK_APPLIED_CALLBACK,
    CANCEL_ALL_SUBSCRIPTIONS,
    GET_BLOCK_HEADER,                       // Blocks and transactions
    GET_BLOCK_HEADER_BATCH,
    GET_BLOCK,
    GET_TRANSACTION,
    GET_RECENT_TRANSACTION_BY_ID,
    GET_CHAIN_PROPERTIES,                   // Globals
    GET_GLOBAL_PROPERTIES,
    GET_CONFIG,
    GET_CHAIN_ID,
    GET_DYNAMIC_GLOBAL_PROPERTIES,
    GET_KEY_REFERENCES,                     // Keys
    IS_PUBLIC_KEY_REGISTERED,
    GET_ACCOUNT_ID_FROM_STRING,             // Accounts
    GET_ACCOUNTS,
    GET_FULL_ACCOUNTS,
    GET_ACCOUNT_BY_NAME,
    GET_ACCOUNT_REFERENCES,
    LOOKUP_ACCOUNT_NAMES,
    LOOKUP_ACCOUNTS,
    GET_ACCOUNT_COUNT,
    GET_ACCOUNT_BALANCES,                   // Balances
    GET_NAMED_ACCOUNT_BALANCES,
    GET_BALANCE_OBJECTS,
    GET_VESTED_BALANCES,
    GET_VESTING_BALANCES,
    GET_ASSETS,                             // Assets
    LIST_ASSETS,
    LOOKUP_ASSET_SYMBOLS,
    GET_ASSET_COUNT,
    GET_ASSETS_BY_ISSUER,
    GET_ASSET_ID_FROM_STRING,
    GET_ORDER_BOOK,                         // Markets / feeds
    GET_LIMIT_ORDERS,
    GET_ACCOUNT_LIMIT_ORDERS,
    GET_CALL_ORDERS,
    GET_CALL_ORDERS_BY_ACCOUNT,
    GET_SETTLE_ORDERS,
    GET_SETTLE_ORDERS_BY_ACCOUNT,
    GET_MARGIN_POSITIONS,
    GET_COLLATERAL_BIDS,
    SUBSCRIBE_TO_MARKET,
    UNSUBSCRIBE_FROM_MARKET,
    GET_TICKER,
    GET_24_VOLUME,
    GET_TOP_MARKETS,
    GET_TRADE_HISTORY,
    GET_TRADE_HISTORY_BY_SEQUENCE,
    GET_WITNESSES,                          // Witnesses
    GET_WITNESS_BY_ACCOUNT,
    LOOKUP_WITNESS_ACCOUNTS,
    GET_WITNESS_COUNT,
    GET_COMMITTEE_MEMBERS,                  // Committee members
    GET_COMMITTEE_MEMBER_BY_ACCOUNT,
    LOOKUP_COMMITTEE_MEMBER_ACCOUNTS,
    GET_COMMITTEE_COUNT,
    GET_ALL_WORKERS,                        // Workers
    GET_WORKERS_BY_ACCOUNT,
    GET_WORKER_COUNT,
    LOOKUP_VOTE_IDS,                        // Votes
    GET_TRANSACTION_HEX,                    // Authority / Validation
    GET_REQUIRED_SIGNATURES,
    GET_POTENTIAL_SIGNATURES,
    GET_POTENTIAL_ADDRESS_SIGNATURES,
    VERIFY_AUTHORITY,
    VERIFY_ACCOUNT_AUTHORITY,
    VALIDATE_TRANSACTION,
    GET_REQUIRED_FEES,
    GET_PROPOSED_TRANSACTIONS,              // Proposed Transactions
    GET_BLINDED_BALANCES,                   // Blinded balances
    GET_WITHDRAW_PERMISSIONS_BY_GIVER,      // Withdrawals
    GET_WITHDRAW_PERMISSIONS_BY_RECIPIENT,
    GET_HTLC,                               // HTLC
    GET_HTLC_BY_FROM,
    GET_HTLC_BY_TO,
    LIST_HTLCS;
    override val type: APIType = APIType.DATABASE
    override val nameString: String = name.lowercase()
}
enum class HistoryAPI: API {                // 0x05
    ;
    override val type: APIType = APIType.HISTORY
    override val nameString: String = name.lowercase()

}
enum class NetworkNodeAPI: API {           // 0x06
    ;
    override val type: APIType = APIType.NETWORK_NODE
    override val nameString: String = name.lowercase()
}
enum class CryptoAPI: API {                 // 0x07
    ;
    override val type: APIType = APIType.CRYPTO
    override val nameString: String = name.lowercase()
}
enum class AssetAPI: API {                  // 0x08
    ;
    override val type: APIType = APIType.ASSET
    override val nameString: String = name.lowercase()
}
enum class OrdersAPI: API {                 // 0x09
    ;
    override val type: APIType = APIType.ORDERS
    override val nameString: String = name.lowercase()
}



//
//FC_API(graphene::app::history_api,
//(get_account_history)
//(get_account_history_by_operations)
//(get_account_history_operations)
//(get_relative_account_history)
//(get_fill_order_history)
//(get_market_history)
//(get_market_history_buckets)
//(get_liquidity_pool_history)
//(get_liquidity_pool_history_by_sequence)
//)
//FC_API(graphene::app::block_api,
//(get_blocks)
//)
//FC_API(graphene::app::network_broadcast_api,
//(broadcast_transaction)
//(broadcast_transaction_with_callback)
//(broadcast_transaction_synchronous)
//(broadcast_block)
//)
//FC_API(graphene::app::network_node_api,
//(get_info)
//(add_node)
//(get_connected_peers)
//(get_potential_peers)
//(get_advanced_node_parameters)
//(set_advanced_node_parameters)
//)
//FC_API(graphene::app::crypto_api,
//(blind)
//(blind_sum)
//(verify_sum)
//(verify_range)
//(range_proof_sign)
//(verify_range_proof_rewind)
//(range_get_info)
//)
//FC_API(graphene::app::asset_api,
//(get_asset_holders)
//(get_asset_holders_count)
//(get_all_asset_holders)
//)
//FC_API(graphene::app::orders_api,
//(get_tracked_groups)
//(get_grouped_limit_orders)
//)
//FC_API(graphene::app::custom_operations_api,
//(get_storage_info)
//)
//FC_API(graphene::app::login_api,
//(login)
//(block)
//(network_broadcast)
//(database)
//(history)
//(network_node)
//(crypto)
//(asset)
//(orders)
//(debug)
//(custom_operations)
//)
