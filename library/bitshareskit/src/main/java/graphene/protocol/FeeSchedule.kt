package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FeeSchedule(
    /**
     *  @note must be sorted by fee_parameters.which() and have no duplicates
     */
    @SerialName("parameters") val parameters: StatSet<FeeParameter>, // type_lt
    @SerialName("scale")      val scale: UInt32, //< fee * scale / GRAPHENE_100_PERCENT
) {
    ///**
// *  @brief contains all of the parameters necessary to calculate the fee for any operation
// */
//struct fee_schedule
//{
//    static const fee_schedule& get_default();
//
//    /**
//     *  Finds the appropriate fee parameter struct for the operation
//     *  and then calculates the appropriate fee in CORE asset.
//     */
//    asset calculate_fee( const operation& op )const;
//    /**
//     *  Finds the appropriate fee parameter struct for the operation
//     *  and then calculates the appropriate fee in an asset specified
//     *  implicitly by core_exchange_rate.
//     */
//    asset calculate_fee( const operation& op, const price& core_exchange_rate )const;
//    /**
//     *  Updates the operation with appropriate fee and returns the fee.
//     */
//    asset set_fee( operation& op, const price& core_exchange_rate = price::unit_price() )const;
//
//    void zero_all_fees();
//
//    /**
//     *  Validates all of the parameters are present and accounted for.
//     */
//    void validate()const {}
//
//    template<typename Operation>
//    const typename Operation::fee_parameters_type& get()const
//    {
//        return fee_helper<Operation>().cget(parameters);
//    }
//    template<typename Operation>
//    typename Operation::fee_parameters_type& get()
//    {
//        return fee_helper<Operation>().get(parameters);
//    }
//    template<typename Operation>
//    bool exists()const
//            {
//                auto itr = parameters.find(typename Operation::fee_parameters_type());
//                return itr != parameters.end();
//            }
//

//    private:
//    static fee_schedule get_default_impl();
//};
}


@Serializable(with = FeeParameterSerializer::class)
sealed class FeeParameter

object FeeParameterSerializer : StaticVarSerializer<FeeParameter>(
    listOf(
        /*  0 */ TransferOperationFeeParameter::class,
        /*  1 */ LimitOrderCreateOperationFeeParameter::class,
        /*  2 */ LimitOrderCancelOperationFeeParameter::class,
        /*  3 */ CallOrderUpdateOperationFeeParameter::class,
        /*  4 */ FillOrderOperationFeeParameter::class,           // Virtual
        /*  5 */ AccountCreateOperationFeeParameter::class,
        /*  6 */ AccountUpdateOperationFeeParameter::class,
        /*  7 */ AccountWhitelistOperationFeeParameter::class,
        /*  8 */ AccountUpgradeOperationFeeParameter::class,
        /*  9 */ AccountTransferOperationFeeParameter::class,
        /* 10 */ AssetCreateOperationFeeParameter::class,
        /* 11 */ AssetUpdateOperationFeeParameter::class,
        /* 12 */ AssetUpdateBitassetOperationFeeParameter::class,
        /* 13 */ AssetUpdateFeedProducersOperationFeeParameter::class,
        /* 14 */ AssetIssueOperationFeeParameter::class,
        /* 15 */ AssetReserveOperationFeeParameter::class,
        /* 16 */ AssetFundFeePoolOperationFeeParameter::class,
        /* 17 */ AssetSettleOperationFeeParameter::class,
        /* 18 */ AssetGlobalSettleOperationFeeParameter::class,
        /* 19 */ AssetPublishFeedOperationFeeParameter::class,
        /* 20 */ WitnessCreateOperationFeeParameter::class,
        /* 21 */ WitnessUpdateOperationFeeParameter::class,
        /* 22 */ ProposalCreateOperationFeeParameter::class,
        /* 23 */ ProposalUpdateOperationFeeParameter::class,
        /* 24 */ ProposalDeleteOperationFeeParameter::class,
        /* 25 */ WithdrawPermissionCreateOperationFeeParameter::class,
        /* 26 */ WithdrawPermissionUpdateOperationFeeParameter::class,
        /* 27 */ WithdrawPermissionClaimOperationFeeParameter::class,
        /* 28 */ WithdrawPermissionDeleteOperationFeeParameter::class,
        /* 29 */ CommitteeMemberCreateOperationFeeParameter::class,
        /* 30 */ CommitteeMemberUpdateOperationFeeParameter::class,
        /* 31 */ CommitteeMemberUpdateGlobalParametersOperationFeeParameter::class,
        /* 32 */ VestingBalanceCreateOperationFeeParameter::class,
        /* 33 */ VestingBalanceWithdrawOperationFeeParameter::class,
        /* 34 */ WorkerCreateOperationFeeParameter::class,
        /* 35 */ CustomOperationFeeParameter::class,
        /* 36 */ AssertOperationFeeParameter::class,
        /* 37 */ BalanceClaimOperationFeeParameter::class,
        /* 38 */ OverrideTransferOperationFeeParameter::class,
        /* 39 */ TransferToBlindOperationFeeParameter::class,
        /* 40 */ BlindTransferOperationFeeParameter::class,
        /* 41 */ TransferFromBlindOperationFeeParameter::class,
        /* 42 */ AssetSettleCancelOperationFeeParameter::class,  // Virtual
        /* 43 */ AssetClaimFeesOperationFeeParameter::class,
        /* 44 */ FbaDistributeOperationFeeParameter::class,       // Virtual
        /* 45 */ BidCollateralOperationFeeParameter::class,
        /* 46 */ ExecuteBidOperationFeeParameter::class,          // Virtual
        /* 47 */ AssetClaimPoolOperationFeeParameter::class,
        /* 48 */ AssetUpdateIssuerOperationFeeParameter::class,
        /* 49 */ HtlcCreateOperationFeeParameter::class,
        /* 50 */ HtlcRedeemOperationFeeParameter::class,
        /* 51 */ HtlcRedeemedOperationFeeParameter::class,         // Virtual
        /* 52 */ HtlcExtendOperationFeeParameter::class,
        /* 53 */ HtlcRefundOperationFeeParameter::class,           // Virtual
        /* 54 */ CustomAuthorityCreateOperationFeeParameter::class,
        /* 55 */ CustomAuthorityUpdateOperationFeeParameter::class,
        /* 56 */ CustomAuthorityDeleteOperationFeeParameter::class,
        /* 57 */ TicketCreateOperationFeeParameter::class,
        /* 58 */ TicketUpdateOperationFeeParameter::class,
        /* 59 */ LiquidityPoolCreateOperationFeeParameter::class,
        /* 60 */ LiquidityPoolDeleteOperationFeeParameter::class,
        /* 61 */ LiquidityPoolDepositOperationFeeParameter::class,
        /* 62 */ LiquidityPoolWithdrawOperationFeeParameter::class,
        /* 63 */ LiquidityPoolExchangeOperationFeeParameter::class,
        /* 64 */ SametFundCreateOperationFeeParameter::class,
        /* 65 */ SametFundDeleteOperationFeeParameter::class,
        /* 66 */ SametFundUpdateOperationFeeParameter::class,
        /* 67 */ SametFundBorrowOperationFeeParameter::class,
        /* 68 */ SametFundRepayOperationFeeParameter::class,
        /* 69 */ CreditOfferCreateOperationFeeParameter::class,
        /* 70 */ CreditOfferDeleteOperationFeeParameter::class,
        /* 71 */ CreditOfferUpdateOperationFeeParameter::class,
        /* 72 */ CreditOfferAcceptOperationFeeParameter::class,
        /* 73 */ CreditDealRepayOperationFeeParameter::class,
        /* 74 */ CreditDealExpiredOperationFeeParameter::class,    // Virtual
    )
)


