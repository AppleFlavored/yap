package dev.flavored.yap.network

import java.io.InputStream

data class YapResponse(
    val statusCode: Short,
    val contentType: String,
    val contentLength: Int,
    val body: ByteArray,
) {
    val isSuccessful get() = statusCode in 200..299

    fun getBodyAsString(): String {
        return body.decodeToString()
    }

    fun getBodyAsInputStream(): InputStream {
        return body.inputStream()
    }
}