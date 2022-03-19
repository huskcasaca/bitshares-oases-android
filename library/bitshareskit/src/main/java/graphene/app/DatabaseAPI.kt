package graphene.app

import java.util.*


private enum class DatabaseAPI1 {


    /** Block API */
    GET_BLOCKS,                             // Block

    /** Network Broadcast API */
    BROADCAST_TRANSACTION,                  // Transactions
    BROADCAST_TRANSACTION_WITH_CALLBACK,
    BROADCAST_TRANSACTION_SYNCHRONOUS,
    BROADCAST_BLOCK,                        // Block


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