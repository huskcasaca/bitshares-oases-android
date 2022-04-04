package graphene.protocol

import graphene.serializers.FeeParameterSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FeeSchedule(
    /**
     *  @note must be sorted by fee_parameters.which() and have no duplicates
     */
    @SerialName("parameters") val parameters: FeeParameters, // type_lt
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

@Serializable
object EmptyFeeParameter : FeeParameter()
