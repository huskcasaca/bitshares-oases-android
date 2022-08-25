package bitshareskit.errors

enum class ErrorCode(val code: Int, val des: String) {

//
//    const val CODE_ALREADY_BROADCAST = 0x00
//    const val CODE_NO_OPERATION = 0x10
//    const val CODE_NO_HEADER_BLOCK = 0x11
//    const val CODE_NO_PRIVATE_KEY = 0x12
//    const val CODE_FEE_NOT_CALCULATED = 0x20
//
//    const val CODE_INSUFFICIENT_OWNER_KEY = 0x90
//    const val CODE_INSUFFICIENT_ACTIVE_KEY = 0x90
//
//    const val CODE_TIMEOUT = 0xA0
//
//    const val CODE_UNKNOWN_ERROR = 0xFF

    UNSPECIFIED_EXCEPTION           (0, ""),
    UNHANDLED_EXCEPTION             (1, ""),
    TIMEOUT_EXCEPTION               (2, ""),
    FILE_NOT_FOUND_EXCEPTION        (3, ""),
    PARSE_ERROR_EXCEPTION           (4, ""),
    INVALID_ARG_EXCEPTION           (5, ""),
    KEY_NOT_FOUND_EXCEPTION         (6, ""),
    BAD_CAST_EXCEPTION              (7, ""),
    OUT_OF_RANGE_EXCEPTION          (8, ""),
    CANCELED_EXCEPTION              (9, ""),
    ASSERT_EXCEPTION                (10, ""),
    EOF_EXCEPTION                   (11, ""),
    STD_EXCEPTION                   (13, ""),
    INVALID_OPERATION_EXCEPTION     (14, ""),
    UNKNOWN_HOST_EXCEPTION          (15, ""),
    NULL_OPTIONAL                   (16, ""),
    AES_ERROR                       (18, ""),
    OVERFLOW                        (19, ""),
    UNDERFLOW                       (20, ""),
    DIVIDE_BY_ZERO                  (21, ""),
    METHOD_NOT_FOUND_EXCEPTION      (22, ""),

    NO_CONNECTION                   (404, "no network connection" ),
    ALREADY_BROADCAST               (1000, "transaction already broadcast" ),
    MISSING_OPERATION               (1001, "transaction has no operation" ),
    MISSING_HEADER_BLOCK            (1002, "transaction has no header block attached" ),
    FEE_NOT_CALCULATED              (1003, "transaction fee not calculated" ),
    BROADCAST_TIMEOUT               (1004, "broadcast timeout" ),

    UNSUPPORTED_API                 (2000, "unsupported api type" ),
    NO_SUPPORTED_API                (2001, "no supported api type" ),

    UNKNOWN_ERROR                   (9999, "unknown error" ),


    // tx code 303000X

    CHAIN_EXCEPTION                 (3000000, "blockchain exception" ),
    DATABASE_QUERY_EXCEPTION        (3010000, "database query exception" ),
    BLOCK_VALIDATE_EXCEPTION        (3020000, "block validation exception" ),
    TRANSACTION_EXCEPTION           (3030000, "transaction validation exception" ),
    OPERATION_VALIDATE_EXCEPTION    (3040000, "operation validation exception" ),
    OPERATION_EVALUATE_EXCEPTION    (3050000, "operation evaluation exception" ),
    UTILITY_EXCEPTION               (3060000, "utility method exception" ),
    UNDO_DATABASE_EXCEPTION         (3070000, "undo database exception" ),
    UNLINKABLE_BLOCK_EXCEPTION      (3080000, "unlinkable block" ),
    BLACK_SWAN_EXCEPTION            (3090000, "black swan" ),

    // tx code 303000X
    MISSING_ACTIVE_AUTH             (3030001, "missing required active authority" ),
    MISSING_OWNER_AUTH              (3030002, "missing required owner authority" ),
    MISSING_OTHER_AUTH              (3030003, "missing required other authority" ),
    IRRELEVANT_SIG                  (3030004, "irrelevant signature included" ),
    DUPLICATE_SIG                   (3030005, "duplicate signature included" ),
    INVALID_COMMITTEE_APPROVAL      (3030006, "committee account cannot directly approve transaction" ),
    INSUFFICIENT_FEE                (3030007, "insufficient fee" ),

    INVALID_PTS_ADDRESS             (3060001, "invalid pts address" ),
//    INSUFFICIENT_FEEDS              (37006, "insufficient feeds" ),

    POP_EMPTY_CHAIN                 (3070001, "there are no blocks to pop" ),




}
