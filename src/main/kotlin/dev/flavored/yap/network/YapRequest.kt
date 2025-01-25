package dev.flavored.yap.network

data class YapRequest(
    val method: Method,
    val path: String,
    val content: ByteArray,
)
