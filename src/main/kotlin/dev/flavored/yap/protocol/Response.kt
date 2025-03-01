package dev.flavored.yap.protocol

import java.io.InputStream
import java.nio.ByteBuffer

data class Response(val status: Short, val body: InputStream) {
    fun getBodyAsString(): Result<String> {
        val maybeBytes = runCatching { body.readAllBytes() }
        return maybeBytes.map { bytes -> bytes.toString(Charsets.UTF_8) }
    }

    fun getBodyAsBuffer(): Result<ByteBuffer> {
        val maybeBytes = runCatching { body.readAllBytes() }
        return maybeBytes.map { bytes -> ByteBuffer.wrap(bytes) }
    }
}

data class ResponseHeader(val status: Short, val contentLength: Int) {
    constructor(buffer: ByteBuffer) : this(
        buffer.getShort(),
        buffer.getInt()
    )

    companion object {
        const val SIZE = Short.SIZE_BYTES + Int.SIZE_BYTES
    }
}