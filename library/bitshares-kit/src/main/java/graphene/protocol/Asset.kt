package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Asset(
    @SerialName("amount")
    val amount: ShareType,
    @SerialName("asset_id")
    val asset: AssetIdType
) {
    companion object {

        val INVALID = Asset(
            ShareType.MAX_VALUE,
            emptyIdType(),
        )

    }

//    asset& operator += ( const asset& o )
//    {
//        FC_ASSERT( asset_id == o.asset_id );
//        amount += o.amount;
//        return *this;
//    }
//    asset& operator -= ( const asset& o )
//    {
//        FC_ASSERT( asset_id == o.asset_id );
//        amount -= o.amount;
//        return *this;
//    }
//    asset operator -()const { return asset( -amount, asset_id ); }
//
//    friend bool operator == ( const asset& a, const asset& b )
//    {
//        return std::tie( a.asset_id, a.amount ) == std::tie( b.asset_id, b.amount );
//    }
//    friend bool operator < ( const asset& a, const asset& b )
//    {
//        FC_ASSERT( a.asset_id == b.asset_id );
//        return a.amount < b.amount;
//    }
//    friend inline bool operator <= ( const asset& a, const asset& b )
//    {
//        return !(b < a);
//    }
//
//    friend inline bool operator != ( const asset& a, const asset& b )
//    {
//        return !(a == b);
//    }
//    friend inline bool operator > ( const asset& a, const asset& b )
//    {
//        return (b < a);
//    }
//    friend inline bool operator >= ( const asset& a, const asset& b )
//    {
//        return !(a < b);
//    }
//
//    friend asset operator - ( const asset& a, const asset& b )
//    {
//        FC_ASSERT( a.asset_id == b.asset_id );
//        return asset( a.amount - b.amount, a.asset_id );
//    }
//    friend asset operator + ( const asset& a, const asset& b )
//    {
//        FC_ASSERT( a.asset_id == b.asset_id );
//        return asset( a.amount + b.amount, a.asset_id );
//    }
//
//    static share_type scaled_precision( uint8_t precision );
//
//    asset multiply_and_round_up( const price& p )const; //< Multiply and round up

}




/**
 * @brief The price struct stores asset prices in the BitShares system.
 *
 * A price is defined as a ratio between two assets, and represents a possible exchange rate between those two
 * assets. prices are generally not stored in any simplified form, i.e. a price of (1000 CORE)/(20 USD) is perfectly
 * normal.
 *
 * The assets within a price are labeled base and quote. Throughout the BitShares code base, the convention used is
 * that the base asset is the asset being sold, and the quote asset is the asset being purchased, where the price is
 * represented as base/quote, so in the example price above the seller is looking to sell CORE asset and get USD in
 * return.
 */
@Serializable
data class PriceType(
    @SerialName("base")
    val base: Asset,
    @SerialName("quote")
    val quote: Asset,
) {
    companion object {

        val INVALID = PriceType(
            Asset.INVALID,
            Asset.INVALID,
        )

    }
//    static price max(asset_id_type base, asset_id_type quote );
//    static price min(asset_id_type base, asset_id_type quote );
//
//    static price call_price(const asset& debt, const asset& collateral, uint16_t collateral_ratio);
//
//    // The unit price for an asset type A is defined to be a price such that for any asset m, m*A=m
//    static price unit_price(asset_id_type a = asset_id_type()) { return price(asset(1, a), asset(1, a)); }
//
//    price max()const { return price::max( base.asset_id, quote.asset_id ); }
//    price min()const { return price::min( base.asset_id, quote.asset_id ); }
//
//    double to_real()const { return double(base.amount.value)/double(quote.amount.value); }
//
//    bool is_null()const;
//    // @brief Check if the object is valid
//    // @param check_upper_bound Whether to check if the amounts in the price are too large
//    void validate( bool check_upper_bound = false )const;
}
//price operator / ( const asset& base, const asset& quote );
//inline price operator~( const price& p ) { return price{p.quote,p.base}; }
//
//bool  operator <  ( const price& a, const price& b );
//bool  operator == ( const price& a, const price& b );
//
//inline bool  operator >  ( const price& a, const price& b ) { return (b < a); }
//inline bool  operator <= ( const price& a, const price& b ) { return !(b < a); }
//inline bool  operator >= ( const price& a, const price& b ) { return !(a < b); }
//inline bool  operator != ( const price& a, const price& b ) { return !(a == b); }
//
//asset operator *  ( const asset& a, const price& b ); //< Multiply and round down
//
//price operator *  ( const price& p, const ratio_type& r );
//price operator /  ( const price& p, const ratio_type& r );
//
//inline price& operator *=  ( price& p, const ratio_type& r )
//{ p = p * r; return p; }
//inline price& operator /=  ( price& p, const ratio_type& r )
//{ p = p / r; return p; }


//abstract class BasePriceFeed {
//
//    abstract val settlementPrice: PriceType
//    abstract val coreExchangeRate: PriceType
//    abstract val maintenanceCollateralRatio: UInt16
//    abstract val maximumShortSqueezeRatio: UInt16
//
//}

/**
 *  @class price_feed
 *  @brief defines market parameters for margin positions
 */

@Serializable
data class PriceFeed(
    /** Forced settlements will evaluate using this price, defined as BITASSET / COLLATERAL */
    @SerialName("settlement_price")
    val settlementPrice: PriceType,
    /** Price at which automatically exchanging this asset for CORE from fee pool occurs (used for paying fees) */
    @SerialName("core_exchange_rate")
    val core_exchange_rate: PriceType,
    /** Fixed point between 1.000 and 10.000, implied fixed point denominator is GRAPHENE_COLLATERAL_RATIO_DENOM */
    @SerialName("maintenance_collateral_ratio")
    val maintenance_collateral_ratio: UInt16 = GRAPHENE_DEFAULT_MAINTENANCE_COLLATERAL_RATIO.toUInt16(),
    /** Fixed point between 1.000 and 10.000, implied fixed point denominator is GRAPHENE_COLLATERAL_RATIO_DENOM */
    @SerialName("maximum_short_squeeze_ratio")
    val maximum_short_squeeze_ratio: UInt16 = GRAPHENE_DEFAULT_MAX_SHORT_SQUEEZE_RATIO.toUInt16(),
) {

//    /**
//     *  Required maintenance collateral is defined
//     *  as a fixed point number with a maximum value of 10.000
//     *  and a minimum value of 1.000.  (denominated in GRAPHENE_COLLATERAL_RATIO_DENOM)
//     *
//     *  A black swan event occurs when value_of_collateral equals
//     *  value_of_debt * MSSR.  To avoid a black swan a margin call is
//     *  executed when value_of_debt * required_maintenance_collateral
//     *  equals value_of_collateral using rate.
//     *
//     *  Default requirement is $1.75 of collateral per $1 of debt
//     *
//     *  BlackSwan ---> SQR ---> MCR ----> SP
//     */
//
//    /**
//     * This is the price at which a call order will relinquish COLLATERAL when margin called. It is
//     * also the price that establishes the minimum amount of collateral per debt that call orders must
//     * maintain to avoid possibility of black swan.  A call order maintaining less collateral per debt
//     * than this price is unable to meet the combined obligation to sell collateral at the Margin Call
//     * Offer Price (MCOP) *AND* to pay the margin call fee. The MSSP is related to the MCOP, but the
//     * MSSP accounts for the need to reserve extra collateral to pay the margin call fee, whereas the
//     * MCOP only accounts for the collateral to be traded to the call buyer.  Prior to the
//     * introduction of the Margin Call Fee Ratio (MCFR) with BSIP-74, the two prices (MSSP and MCOP)
//     * were identical, and MSSP could be thought of as "the price at which you are forced to sell
//     * collateral if margin called," but this latter concept is now embodied by the MCOP.
//     *
//     * The Maximum Short Squeeze Price is computed as follows, in units of DEBT per COLLATERAL:
//     *
//     *   MSSP = settlement_price / MSSR
//     *
//     * @return The MSSP in units of DEBT per COLLATERAL.
//     */
//    price max_short_squeeze_price()const;
//    /**
//     * Older implementation of max_short_squeeze_price() due to hardfork changes. It came with
//     * the following commentary:
//     *
//     * When selling collateral to pay off debt, the least amount of debt to receive should be
//     *  min_usd = max_short_squeeze_price() * collateral
//     *
//     *  This is provided to ensure that a black swan cannot be trigged due to poor liquidity alone, it
//     *  must be confirmed by having the max_short_squeeze_price() move below the black swan price.
//     * @returns the Maximum Short Squeeze price for this asset
//     */
//    price max_short_squeeze_price_before_hf_1270()const;
//
//    /**
//     * Compute price at which margin calls offer to sell collateral.
//     *
//     * Margin calls offer a greater amount of COLLATERAL asset to the market to buy back DEBT
//     * asset than would otherwise be required in a fair exchange at the settlement_price.
//     * (I.e. they sell collateral "cheaper" than its price feed value.) This is done to attract a
//     * quick buyer of the call in order to preserve healthy collateralization of the DEBT asset
//     * overall.  The price at which the call is offered, in comparison to the settlement price, is
//     * determined by the Maximum Short Squeeze Ratio (MSSR) and the Margin Call Fee Ratio (MCFR)
//     * as follows, in units of DEBT per COLLATERAL:
//     *
//     *   MCOP = settlement_price / (MSSR - MCFR)
//     *
//     * Compare with Maximum Short Squeeze Price (MSSP), which is computed as follows:
//     *
//     *   MSSP = settlement_price / MSSR
//     *
//     * Since BSIP-74, we distinguish between Maximum Short Squeeze Price (MSSP) and Margin Call
//     * Order Price (MCOP). Margin calls previously offered collateral at the MSSP, but now they
//     * offer slightly less collateral per debt if Margin Call Fee Ratio (MCFR) is set, because
//     * the call order must reserve some collateral to pay the fee.  We must still retain the
//     * concept of MSSP, as it communicates the minimum collateralization before black swan may be
//     * triggered, but we add this new method to calculate MCOP.
//     *
//     * Note that when we calculate the MCOP, we enact a price floor to ensure the margin call never
//     * offers LESS collateral than the DEBT is worth. As such, it's important to calculate the
//     * realized fee, when trading at the offer price, as a delta between total relinquished collateral
//     * (DEBT*MSSP) and collateral sold to the buyer (DEBT*MCOP).  If you instead try to calculate the
//     * fee by direct multiplication of MCFR, you will get the wrong answer if the price was
//     * floored. (Fee is truncated when price is floored.)
//     *
//     * @param margin_call_fee_ratio MCFR value currently in effect. If zero or unset, returns
//     *    same result as @ref max_short_squeeze_price().
//     *
//     * @return The MCOP in units of DEBT per COLLATERAL.
//     */
//    price margin_call_order_price(const fc::optional<uint16_t>& margin_call_fee_ratio)const;
//
//    // Compute the MCOR, the ratio between margin_call_order_price and feed price
//    // @return MSSR - MCFR
//    ratio_type margin_call_order_ratio( const fc::optional<uint16_t>& margin_call_fee_ratio )const;
//
//    /**
//     * Ratio between max_short_squeeze_price and margin_call_order_price.
//     *
//     * This ratio, if it multiplied margin_call_order_price (expressed in DEBT/COLLATERAL), would
//     * yield the max_short_squeeze_price, apart perhaps for truncation (rounding) error.
//     *
//     * When a margin call is taker, matching an existing order on the books, it is possible the call
//     * gets a better realized price than the order price that it offered at.  In this case, the margin
//     * call fee is proportionaly reduced. This ratio is used to calculate the price at which the call
//     * relinquishes collateral (to meet both trade and fee obligations) based on actual realized match
//     * price.
//     *
//     * This function enacts the same flooring as margin_call_order_price() (MSSR - MCFR is floored at
//     * 1.00).  This ensures we apply the same fee truncation in the taker case as the maker case.
//     *
//     * @return (MSSR - MCFR) / MSSR
//     */
//    ratio_type margin_call_pays_ratio(const fc::optional<uint16_t>& margin_call_fee_ratio)const;
//
//    // Call orders with collateralization (aka collateral/debt) not greater than this value are in margin call
//    // territory.
//    // Calculation: ~settlement_price * maintenance_collateral_ratio / GRAPHENE_COLLATERAL_RATIO_DENOM
//    price maintenance_collateralization()const;
//
//    // Whether the parameters that affect margin calls in this price feed object are the same as the parameters
//    // in the passed-in object
//    bool margin_call_params_equal( const price_feed& b ) const
//            {
//                if( this == &b )
//                    return true;
//                return std::tie(   settlement_price,   maintenance_collateral_ratio,   maximum_short_squeeze_ratio ) ==
//                        std::tie( b.settlement_price, b.maintenance_collateral_ratio, b.maximum_short_squeeze_ratio );
//            }
//    //@}
//
//    void validate() const;
//    bool is_for( asset_id_type asset_id ) const;
//    private:
//    // Helper function for other functions e.g. @ref margin_call_order_price
//    uint16_t get_margin_call_price_numerator(const fc::optional<uint16_t>& margin_call_fee_ratio)const;
}

@Serializable
data class PriceFeedWithIcr(
    @SerialName("settlement_price")
    val settlementPrice: PriceType,
    @SerialName("core_exchange_rate")
    val coreExchangeRate: PriceType,
    @SerialName("maintenance_collateral_ratio")
    val maintenanceCollateralRatio: UInt16 = GRAPHENE_DEFAULT_MAINTENANCE_COLLATERAL_RATIO,
    @SerialName("maximum_short_squeeze_ratio")
    val maximumShortSqueezeRatio: UInt16 = GRAPHENE_DEFAULT_MAX_SHORT_SQUEEZE_RATIO,
    // After BSIP77, when creating a new debt position or updating an existing position,
    // the position will be checked against this parameter.
    // Fixed point between 1.000 and 10.000, implied fixed point denominator is GRAPHENE_COLLATERAL_RATIO_DENOM
    @SerialName("initial_collateral_ratio")
    val initialCollateralRatio: UInt16 = GRAPHENE_DEFAULT_MAINTENANCE_COLLATERAL_RATIO,
)  {
//
//    price_feed_with_icr()
//    : price_feed(), initial_collateral_ratio( maintenance_collateral_ratio )
//    {}
//
//    price_feed_with_icr( const price_feed& pf, const optional<uint16_t>& icr = {} )
//    : price_feed( pf ), initial_collateral_ratio( icr.valid() ? *icr : pf.maintenance_collateral_ratio )
//    {}
//
//    /// The result will be used to check new debt positions and position updates.
//    /// Calculation: ~settlement_price * initial_collateral_ratio / GRAPHENE_COLLATERAL_RATIO_DENOM
//    price get_initial_collateralization()const;
}

@Serializable
data class AdditionalAssetOptions(
    @SerialName("reward_percent")
    val rewardPercent: UInt16 = 0U, // optional
    @SerialName("whitelist_market_fee_sharing")
    val whitelistMarketFeeSharing: FlatSet<AccountIdType> = sortedSetOf(), // optional
    @SerialName("taker_fee_percent")
    val takerFeePercent: UInt16 = 0U, // optional
) : Extension<AdditionalAssetOptions> {

    companion object {

        val INVALID = AdditionalAssetOptions(
            UInt16.MAX_VALUE,
            sortedSetOf(),
            UInt16.MAX_VALUE,
        )

    }
}

@Serializable
data class AssetOptions(
    // The maximum supply of this asset which may exist at any given time. This can be as large as
    // GRAPHENE_MAX_SHARE_SUPPLY
    @SerialName("max_supply")
    val maxSupply: ShareType,
    // When this asset is traded on the markets, this percentage of the total traded will be exacted and paid
    // to the issuer. This is a fixed point value, representing hundredths of a percent, i.e. a value of 100
    // in this field means a 1% fee is charged on market trades of this asset.
    // BSIP81: Asset owners may specify different market fee rate for maker orders and taker orders
    // After BSIP81 activation, market_fee_percent is the maker fee
    @SerialName("market_fee_percent")
    val marketFeePercent: UInt16,
    // Market fees calculated as @ref market_fee_percent of the traded volume are capped to this value
    @SerialName("max_market_fee")
    val maxMarketFee: ShareType,
    // The flags which the issuer has permission to update. See @ref asset_issuer_permission_flags
    @SerialName("issuer_permissions")
    val issuerPermissions: UInt16,
    // The currently active flags on this permission. See @ref asset_issuer_permission_flags
    @SerialName("flags")
    val flags: UInt16,
    // When a non-core asset is used to pay a fee, the blockchain must convert that asset to core asset in
    // order to accept the fee. If this asset's fee pool is funded, the chain will automatically deposite fees
    // in this asset to its accumulated fees, and withdraw from the fee pool the same amount as converted at
    // the core exchange rate.
    @SerialName("core_exchange_rate")
    val coreExchangeRate: PriceType, // = price(asset(), asset(0, asset_id_type(1)));
    // A set of accounts which maintain whitelists to consult for this asset. If whitelist_authorities
    // is non-empty, then only accounts in whitelist_authorities are allowed to hold, use, or transfer the asset.
    @SerialName("whitelist_authorities")
    val whitelistAuthorities: FlatSet<AccountIdType>,
    // A set of accounts which maintain blacklists to consult for this asset. If flags & white_list is set,
    // an account may only send, receive, trade, etc. in this asset if none of these accounts appears in
    // its account_object::blacklisting_accounts field. If the account is blacklisted, it may not transact in
    // this asset even if it is also whitelisted.
    @SerialName("blacklist_authorities")
    val blacklistAuthorities: FlatSet<AccountIdType>,
    /** defines the assets that this asset may be traded against in the market */
    @SerialName("whitelist_markets")
    val whitelistMarkets: FlatSet<AssetIdType>,
    /** defines the assets that this asset may not be traded against in the market, must not overlap whitelist */
    @SerialName("blacklist_markets")
    val blacklistMarkets: FlatSet<AssetIdType>,
    /**
     * data that describes the meaning/purpose of this asset, fee will be charged proportional to
     * size of description.
     */
    @SerialName("description")
    val description: String,
    @SerialName("extensions")
    val extensions: AdditionalAssetOptions
) : GrapheneComponent {

    companion object {
        val INVALID = AssetOptions(
            ShareType.MAX_VALUE, // val maxSupply: ShareType,
            UInt16.MAX_VALUE, // val marketFeePercent: UInt16,
            ShareType.MAX_VALUE, // val maxMarketFee: ShareType,
            UInt16.MAX_VALUE, // val issuerPermissions: UInt16,
            UInt16.MAX_VALUE, // val flags: UInt16,
            PriceType.INVALID, // val coreExchangeRate: PriceType, // = price(asset(), asset(0, asset_id_type(1)));
            sortedSetOf(), // val whitelistAuthorities: FlatSet<K102_AccountType>,
            sortedSetOf(), // val blacklistAuthorities: FlatSet<K102_AccountType>,
            sortedSetOf(), // val whitelistMarkets: FlatSet<K103_AssetType>,
            sortedSetOf(), // val blacklistMarkets: FlatSet<K103_AssetType>,
            emptyString(), // val description: String,
            AdditionalAssetOptions.INVALID, // val extensions: AdditionalAssetOptions
        )
    }

    // @return the bits in @ref flags which are allowed to be updated according to data in @ref issuer_permissions
//    uint16_t get_enabled_issuer_permissions_mask() const;

    // Perform internal consistency checks.
    // @throws fc::exception if any check fails
//    void validate()const;

    // Perform checks about @ref flags.
    // @throws fc::exception if any check fails
//    void validate_flags( bool is_market_issued, bool allow_disable_collateral_bid = true )const;
}


@Serializable
data class BitassetOptions(
    @SerialName("feed_lifetime_sec")
    val feedLifetimeSec: UInt32 = GRAPHENE_DEFAULT_PRICE_FEED_LIFETIME, // uint32_t
    @SerialName("minimum_feeds")
    val minimumFeeds: UInt8 = 1U, // uint32_t
    @SerialName("force_settlement_delay_sec")
    val forceSettlementDelaySec: UInt32 = GRAPHENE_DEFAULT_FORCE_SETTLEMENT_DELAY,
    @SerialName("force_settlement_offset_percent")
    val forceSettlementOffsetPercent: UInt16 = GRAPHENE_DEFAULT_FORCE_SETTLEMENT_OFFSET,
    @SerialName("maximum_force_settlement_volume")
    val maximumForceSettlementVolume: UInt16 = GRAPHENE_DEFAULT_FORCE_SETTLEMENT_MAX_VOLUME,
    @SerialName("short_backing_asset")
    val shortBackingAsset: AssetId,
    @SerialName("extensions")
    val extension: BitassetOptionsExtension,
)

@Serializable
enum class BlackSwanResponseType {
    @SerialName("global_settlement") GLOBAL_SETTLEMENT,
    @SerialName("no_settlement") NO_SETTLEMENT,
    @SerialName("individual_settlement_to_fund") INDIVIDUAL_SETTLEMENT_TO_FUND,
    @SerialName("individual_settlement_to_order") INDIVIDUAL_SETTLEMENT_TO_ORDER,
}

@Serializable
data class BitassetOptionsExtension(
    // After BSIP77, when creating a new debt position or updating an existing position,
    // the position will be checked against this parameter.
    // Unused for prediction markets, although we allow it to be set for simpler implementation
    @SerialName("initial_collateral_ratio")
    val initialCollateralRatio: Optional<UInt16> = optional(),  // BSIP-77
    @SerialName("maintenance_collateral_ratio") // After BSIP75, the asset owner can update MCR directly
    val maintenanceCollateralRatio: Optional<UInt16> = optional(),  // BSIP-75
    @SerialName("maximum_short_squeeze_ratio") // After BSIP75, the asset owner can update MSSR directly
    val maximumShortSqueezeRatio: Optional<UInt16> = optional(),  // BSIP-75
    @SerialName("margin_call_fee_ratio")
    val marginCallFeeRatio: Optional<UInt16> = optional(), // BSIP 74
    @SerialName("force_settle_fee_percent")
    val forceSettleFeePercent: Optional<UInt16> = optional(),  // BSIP-87
    @SerialName("black_swan_response_method") // https://github.com/bitshares/bitshares-core/issues/2467
    val blackSwanResponseMethod: Optional<UInt8> = optional(),
) : Extension<BitassetOptionsExtension>

