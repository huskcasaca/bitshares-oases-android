package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K201_DynamicGlobalPropertyObject(
    @SerialName("id")
    override val id: DynamicGlobalPropertyIdType,
    @SerialName("head_block_number")
    override val headBlockNumber: uint32_t,
    @SerialName("head_block_id")
    override val headBlockId: BlockIdType,
    @SerialName("time")
    override val time: ChainTimePoint,
    @SerialName("current_witness")
    override val currentWitness: WitnessType,
    @SerialName("next_maintenance_time")
    override val nextMaintenanceTime: ChainTimePoint,
    @SerialName("last_vote_tally_time")
    override val lastVoteTallyTime: ChainTimePoint,
    @SerialName("last_budget_time")
    override val lastBudgetTime: ChainTimePoint,
    @SerialName("witness_budget")
    override val witnessBudget: share_type,
    @SerialName("total_pob")
    override val totalPob: share_type,
    @SerialName("total_inactive")
    override val totalInactive: share_type,
    @SerialName("accounts_registered_this_interval")
    override val accountsRegisteredThisInterval: uint32_t, //  = 0
    /**
     *  Every time a block is missed this increases by
     *  RECENTLY_MISSED_COUNT_INCREMENT,
     *  every time a block is found it decreases by
     *  RECENTLY_MISSED_COUNT_DECREMENT.  It is
     *  never less than 0.
     */
    @SerialName("recently_missed_count")
    override val recentlyMissedCount: uint32_t, // = 0
    /**
     * The current absolute slot number.  Equal to the total
     * number of slots since genesis.  Also equal to the total
     * number of missed slots plus head_block_number.
     */
    @SerialName("current_aslot")
    override val currentAslot: uint64_t, // = 0
    /**
     * used to compute witness participation.
     */
    @SerialName("recent_slots_filled")
    override val recentSlotsFilled: UInt128, // fc::uint128_t TODO
    /**
     * dynamic_flags specifies chain state properties that can be
     * expressed in one bit.
     */
    @SerialName("dynamic_flags")
    override val dynamicFlags: uint32_t, // = 0
    @SerialName("last_irreversible_block_num")
    override val lastIrreversibleBlockNum: uint32_t, // = 0


) : AbstractObject(), DynamicGlobalPropertyType {

//    public:
//    static constexpr uint8_t space_id = implementation_ids;
//    static constexpr uint8_t type_id  = impl_dynamic_global_property_object_type;

    enum class DynamicFlagBits {
        /**
         * If maintenance_flag is set, then the head block is a
         * maintenance block.  This means
         * get_time_slot(1) - head_block_time() will have a gap
         * due to maintenance duration.
         *
         * This flag answers the question, "Was maintenance
         * performed in the last call to apply_block()?"
         */
        MAINTENANCE_FLAG //  = 0x01
    }

}