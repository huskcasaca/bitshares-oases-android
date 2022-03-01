package bitcoinkit

/**
 * An ECException is thrown if an error occurs in an elliptic curve cryptographic
 * function
 */
class ECException : Exception {
    /**
     * Creates a new exception with a detail message
     *
     * @param msg Detail message
     */
    constructor(msg: String?) : super(msg) {}

    /**
     * Creates a new exception with a detail message and cause
     *
     * @param msg Detail message
     * @param t   Caught exception
     */
    constructor(msg: String?, t: Throwable?) : super(msg, t) {}
}