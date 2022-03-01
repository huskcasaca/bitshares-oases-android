package bitshareskit.ks_chain

import java.util.*

/**
 * This enum class contains all RPC methods of the blockchain, which can be used to obtain any kind of
 * data stored in the blockchain. Besides data stores in the blockchain itself (blocks, transactions,
 * etc. ..), higher level objects (such as accounts, balances, etc. …) can be retrieved through the
 * full node’s database.
 *
 * @see api     API required by RPC method
 * @see nameString      String of RPC method
 *
 * @see [Blockchain API(s)](https://dev.bitshares.works/en/master/api/blockchain_api.html)
 */

enum class CallMethod {

    /** Login API */
    LOGIN,
    BLOCK,
    NETWORK_BROADCAST,
    DATABASE,
    HISTORY,
    NETWORK_NODE,
    CRYPTO,
    ASSET,
    ORDERS,
    DEBUG,

    /** Block API */
    GET_BLOCKS,                             // Block

    /** Network Broadcast API */
    BROADCAST_TRANSACTION,                  // Transactions
    BROADCAST_TRANSACTION_WITH_CALLBACK,
    BROADCAST_TRANSACTION_SYNCHRONOUS,
    BROADCAST_BLOCK,                        // Block

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
    LIST_HTLCS,


    /** History API */
    GET_ACCOUNT_HISTORY,                    // Account History
    GET_ACCOUNT_HISTORY_BY_OPERATIONS,
    GET_ACCOUNT_HISTORY_OPERATIONS,
    GET_RELATIVE_ACCOUNT_HISTORY,
    GET_FILL_ORDER_HISTORY,                 // Market History
    GET_MARKET_HISTORY,
    GET_MARKET_HISTORY_BUCKETS,

    /** Network Node API */
    GET_INFO,                               // Obtain Network Information
    GET_CONNECTED_PEERS,
    GET_POTENTIAL_PEERS,
    GET_ADVANCED_NODE_PARAMETERS,
    ADD_NODE,                               // Change Network Settings
    SET_ADVANCED_NODE_PARAMETERS,

    /** Crypto API */
    BLIND,                                  // Blinding and Un-Blinding
    BLIND_SUM,
    RANGE_GET_INFO,                         // Rage Proofs
    RANGE_PROOF_SIGN,
    VERIFY_SUM,                             // Verification
    VERIFY_RANGE,
    VERIFY_RANGE_PROOF_REWIND,

    /** Asset API */
    GET_ASSET_HOLDERS,                      // Asset
    GET_ASSET_HOLDERS_COUNT,
    GET_ALL_ASSET_HOLDERS,

    /** Orders API */
    GET_TRACKED_GROUPS,                     // Orders
    GET_GROUPED_LIMIT_ORDERS;

    val api = when (this.ordinal) {
        in 0..9     -> BlockchainAPI.LOGIN
        in 10..10   -> BlockchainAPI.BLOCK
        in 11..14   -> BlockchainAPI.NETWORK_BROADCAST
        in 15..95   -> BlockchainAPI.DATABASE
        in 96..102  -> BlockchainAPI.HISTORY
        in 103..108 -> BlockchainAPI.NETWORK_NODE
        in 109..115 -> BlockchainAPI.CRYPTO
        in 116..118 -> BlockchainAPI.ASSET
        in 119..120 -> BlockchainAPI.ORDERS
        else        -> BlockchainAPI.EMPTY
    }

    // TODO: 2022/2/5 replace with String.lowercase() and apply to all
    val nameString = name.toLowerCase(Locale.ROOT)

}

/*
       (login)
       (block)
       (network_broadcast)
       (database)
       (history)
       (network_node)
       (crypto)
       (asset)
       (orders)
       (debug)

FC_API(graphene::app::database_api,
// Objects
(get_objects)

// Subscriptions
(set_subscribe_callback)
(set_auto_subscription)
(set_pending_transaction_callback)
(set_block_applied_callback)
(cancel_all_subscriptions)

// Blocks and transactions
(get_block_header)
(get_block_header_batch)
(get_block)
(get_transaction)
(get_recent_transaction_by_id)

// Globals
(get_chain_properties)
(get_global_properties)
(get_config)
(get_chain_id)
(get_dynamic_global_properties)

// Keys
(get_key_references)
(is_public_key_registered)

// Accounts
(get_account_id_from_string)
(get_accounts)
(get_full_accounts)
(get_account_by_name)
(get_account_references)
(lookup_account_names)
(lookup_accounts)
(get_account_count)

// Balances
(get_account_balances)
(get_named_account_balances)
(get_balance_objects)
(get_vested_balances)
(get_vesting_balances)

// Assets
(get_assets)
(list_assets)
(lookup_asset_symbols)
(get_asset_count)
(get_assets_by_issuer)
(get_asset_id_from_string)

// Markets / feeds
(get_order_book)
(get_limit_orders)
(get_account_limit_orders)
(get_call_orders)
(get_call_orders_by_account)
(get_settle_orders)
(get_settle_orders_by_account)
(get_margin_positions)
(get_collateral_bids)
(subscribe_to_market)
(unsubscribe_from_market)
(get_ticker)
(get_24_volume)
(get_top_markets)
(get_trade_history)
(get_trade_history_by_sequence)

// Witnesses
(get_witnesses)
(get_witness_by_account)
(lookup_witness_accounts)
(get_witness_count)

// Committee members
(get_committee_members)
(get_committee_member_by_account)
(lookup_committee_member_accounts)
(get_committee_count)

// workers
(get_all_workers)
(get_workers_by_account)
(get_worker_count)

// Votes
(lookup_vote_ids)

// Authority / validation
(get_transaction_hex)
(get_transaction_hex_without_sig)
(get_required_signatures)
(get_potential_signatures)
(get_potential_address_signatures)
(verify_authority)
(verify_account_authority)
(validate_transaction)
(get_required_fees)

// Proposed transactions
(get_proposed_transactions)

// Blinded balances
(get_blinded_balances)

// Withdrawals
(get_withdraw_permissions_by_giver)
(get_withdraw_permissions_by_recipient)

// HTLC
(get_htlc)
(get_htlc_by_from)
(get_htlc_by_to)
(list_htlcs)
*/