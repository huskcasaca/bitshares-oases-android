package graphene.protocol

interface AbstractType {
    val id: AbstractIdType                          get() = this as AbstractIdType
}

interface K100_NullType: AbstractType {
    override val id: K100_NullIdType                get() = this as K100_NullIdType
}

interface K101_BaseType: AbstractType {
    override val id: K101_BaseIdType                get() = this as K101_BaseIdType
}

interface K102_AccountType: AbstractType {
    override val id: K102_AccountIdType                  get() = this as K102_AccountIdType
    val membershipExpirationDate: ChainTimePoint    get() = ChainTimePoint.STANDARD_EXPIRATION_DATE_TIME
    val registrar: K102_AccountType                 get() = emptyIdType()
    val referrer: K102_AccountType                  get() = emptyIdType()
    val lifetimeReferrer: K102_AccountType          get() = emptyIdType()
    val networkFeePercentage: UInt                  get() = 0U
    val lifetimeReferrerFeePercentage: UInt         get() = 0U
    val referrerRewardsFeePercentage: UInt          get() = 0U
    val name: String                                get() = emptyString()
    val owner: Authority                            get() = emptyComponent()
    val active: Authority                           get() = emptyComponent()
    val options: AccountOptions                     get() = emptyComponent()
    val whiteListingAccounts: Set<K102_AccountType> get() = emptySet()
    val blackListingAccounts: Set<K102_AccountType> get() = emptySet()
    val whiteListedAccounts: Set<K102_AccountType>  get() = emptySet()
    val blackListedAccounts: Set<K102_AccountType>  get() = emptySet()
}

interface K103_AssetType: AbstractType {
    override val id: K103_AssetIdType               get() = this as K103_AssetIdType
    val symbol: String                              get() = emptyString()
    val issuerId: K102_AccountType                  get() = emptyIdType()
    val precision: UByte                            get() = 0U
    val options: AssetOptions                       get() = emptyComponent()
    val dynamicDataId: K203AssetDynamicType         get() = emptyIdType()
//    var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}

interface K104_ForceSettlementType: AbstractType {
    override val id: K104_ForceSettlementIdType     get() = this as K104_ForceSettlementIdType
}

interface K105_Type: AbstractType {
    override val id: K105_IdType                    get() = this as K105_IdType
}

interface K106_Type: AbstractType {
    override val id: K106_IdType                    get() = this as K106_IdType
}

interface K107_Type: AbstractType {
    override val id: K107_IdType                    get() = this as K107_IdType
}

interface K108_Type: AbstractType {
    override val id: K108_IdType                    get() = this as K108_IdType
}

interface K109_Type: AbstractType {
    override val id: K109_IdType                    get() = this as K109_IdType
}

interface K110_Type: AbstractType {
    override val id: K110_IdType                    get() = this as K110_IdType
}

interface K111_Type: AbstractType {
    override val id: K111_IdType                    get() = this as K111_IdType
}

interface K112_Type: AbstractType {
    override val id: K112_IdType                    get() = this as K112_IdType
}

interface K113_Type: AbstractType {
    override val id: K113_IdType                    get() = this as K113_IdType
}

interface K114_Type: AbstractType {
    override val id: K114_IdType                    get() = this as K114_IdType
}

interface K115_Type: AbstractType {
    override val id: K115_IdType                    get() = this as K115_IdType
}


interface K116_Type: AbstractType {
    override val id: K116_IdType                    get() = this as K116_IdType
}


interface K117_Type: AbstractType {
    override val id: K117_IdType                    get() = this as K117_IdType
}


interface K118_Type: AbstractType {
    override val id: K118_IdType                    get() = this as K118_IdType
}


interface K119_Type: AbstractType {
    override val id: K119_IdType                    get() = this as K119_IdType
}


interface K120_Type: AbstractType {
    override val id: K120_IdType                    get() = this as K120_IdType
}


interface K121_Type: AbstractType {
    override val id: K121_IdType                    get() = this as K121_IdType
}


interface K122_Type: AbstractType {
    override val id: K122_IdType                    get() = this as K122_IdType
}



interface K203AssetDynamicType: AbstractType {
    override val id: K203AssetDynamicId             get() = this as K203AssetDynamicId
    val currentSupply: UInt64                       get() = 0U
    val confidentialSupply: UInt64                  get() = 0U
    val accumulatedFees: UInt64                     get() = 0U
    val accumulatedCollateralFees: UInt64           get() = 0U
    val feePool: UInt64                             get() = 0U
}

interface K204AssetBitassetType: AbstractType {
    override val id: K204AssetBitassetId            get() = this as K204AssetBitassetId
    val currentSupply: UInt64                       get() = 0U
    val confidentialSupply: UInt64                  get() = 0U
    val accumulatedFees: UInt64                     get() = 0U
    val accumulatedCollateralFees: UInt64           get() = 0U
    val feePool: UInt64                             get() = 0U
}