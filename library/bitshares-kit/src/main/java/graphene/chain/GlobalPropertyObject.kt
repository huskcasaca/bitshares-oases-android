package graphene.chain

import graphene.protocol.*
import graphene.protocol.Optional
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K200_GlobalPropertyObject(
    @SerialName("id")
    override val id: GlobalPropertyId,
    @SerialName("parameters")
    override val parameters: ChainParameters,
    @SerialName("pending_parameters")
    override val pendingParameters: Optional<ChainParameters> = optional(),
    @SerialName("next_available_vote_id")
    override val nextAvailableVoteId: UInt32, // = 0
    @SerialName("active_committee_members")
    override val activeCommitteeMembers: List<CommitteeMemberIdType>, // updated once per maintenance interval
    @SerialName("active_witnesses")
    override val activeWitnesses: FlatSet<WitnessIdType>, // updated once per maintenance interval // TODO
    // n.b. witness scheduling is done by witness_schedule object
) : AbstractObject(), GlobalPropertyIdType


@Serializable
data class K201_DynamicGlobalPropertyObject(
    @SerialName("id")
    override val id: DynamicGlobalPropertyId,
    @SerialName("head_block_number")
    override val headBlockNumber: UInt32,
    @SerialName("head_block_id")
    override val headBlockId: BlockIdType,
    @SerialName("time") @Serializable(TimePointSecSerializer::class)
    override val time: Instant,
    @SerialName("current_witness")
    override val currentWitness: WitnessIdType,
    @SerialName("next_maintenance_time") @Serializable(TimePointSecSerializer::class)
    override val nextMaintenanceTime: Instant,
    @SerialName("last_vote_tally_time") @Serializable(TimePointSecSerializer::class)
    override val lastVoteTallyTime: Instant,
    @SerialName("last_budget_time") @Serializable(TimePointSecSerializer::class)
    override val lastBudgetTime: Instant,
    @SerialName("witness_budget")
    override val witnessBudget: ShareType,
    @SerialName("total_pob")
    override val totalPob: ShareType,
    @SerialName("total_inactive")
    override val totalInactive: ShareType,
    @SerialName("accounts_registered_this_interval")
    override val accountsRegisteredThisInterval: UInt32, //  = 0
    /**
     *  Every time a block is missed this increases by
     *  RECENTLY_MISSED_COUNT_INCREMENT,
     *  every time a block is found it decreases by
     *  RECENTLY_MISSED_COUNT_DECREMENT.  It is
     *  never less than 0.
     */
    @SerialName("recently_missed_count")
    override val recentlyMissedCount: UInt32, // = 0
    /**
     * The current absolute slot number.  Equal to the total
     * number of slots since genesis.  Also equal to the total
     * number of missed slots plus head_block_number.
     */
    @SerialName("current_aslot")
    override val currentAslot: UInt64, // = 0
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
    override val dynamicFlags: UInt32, // = 0
    @SerialName("last_irreversible_block_num")
    override val lastIrreversibleBlockNum: UInt32, // = 0


) : AbstractObject(), DynamicGlobalPropertyIdType {

//    public:
//    static constexpr uint8_t space_id = implementation_ids;
//    static constexpr uint8_t type_id  = impl_dynamic_global_property_object_type;

    // TODO: 2022/4/6
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