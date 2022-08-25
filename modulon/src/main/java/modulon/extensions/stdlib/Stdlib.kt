package modulon.extensions.stdlib

suspend fun loop(action: suspend () -> Boolean) {
    while (action()) Unit
}