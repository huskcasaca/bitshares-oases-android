package com.bitshares.oases.chain

object IntentParameters {

    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_ID = "user_id"

    const val KEY_ACCOUNT_NAME = "account_name"
    const val KEY_ACCOUNT_UID = "account_uid"
    const val KEY_PASSWORD = "password"
    const val KEY_PRIVATE_KEY = "private_key"
    const val KEY_BRAIN_KEY = "brain_key"

    const val KEY_ASSET_NAME = "asset_name"
    const val KEY_ASSET_UID = "asset_uid"

    const val KEY_IS_DASHBOARD = "is_dashboard"

    const val KEY_WORKER_NAME = "worker_name"

    const val KEY_ACCOUNT_BALANCE_UID = "account_balance_uid"

    const val KEY_IS_PICKER = "is_picker"

    const val KEY_ACTIVITY_TYPE = "activity_type"
    const val KEY_TAB_TYPE = "tab_type"

    const val KEY_FRAGMENT = "fragment"

    const val KEY_PARAM_1 = "param_1"
    const val KEY_PARAM_2 = "param_2"
    const val KEY_PARAM_3 = "param_3"
    const val KEY_PARAM_4 = "param_4"
    const val KEY_PARAM_5 = "param_5"

    object Transfer {
        const val KEY_FROM = "from"
        const val KEY_TO = "to"
        const val KEY_ASSET = "asset"
        const val KEY_BALANCE = "balance"
        const val KEY_AMOUNT = "buyAmount"
        const val KEY_MEMO = "memo"
    }

    object Account {
        const val KEY_ID = "id"
        const val KEY_UID = "uid"

        // local
        const val KEY_ACCOUNT = "account"
        const val KEY_BALANCE = "balance"
    }

    object AccountBalance {
        const val KEY_ID = "id"
        const val KEY_UID = "uid"
    }

    object CallOrder {
        const val KEY_ID = "id"
        const val KEY_UID = "uid"

        // local
        const val KEY_CALL_ORDER = "call_order"
    }

    object Asset {
        const val KEY_ID = "id"
        const val KEY_UID = "uid"

        // local
        const val KEY_ASSET = "asset"
    }

    object Block {
        const val KEY_HEIGHT = "height"
    }

    object Chain {
        const val KEY_CHAIN_ID = "chain_id"
    }

    object Operation {
        // local
        const val KEY_OPERATION = "operation"
    }

    object MarketTrade {
        // local
        const val KEY_MARKET = "marketInternal"
    }

}