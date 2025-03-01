package dev.flavored.yap.protocol

import java.net.URI
import java.nio.ByteBuffer

class Request(val uri: URI, val method: Method, private val body: ByteArray) {
    /**
     * Serializes the request to a [ByteBuffer].
     *
     * Request Format:
     * ```
     * - Method         (1 byte)
     * - Path Length    (2 bytes)
     * - Path           (variable length)
     * - Content Length (4 bytes)
     * ```
     */
    fun serializeToBytes(): ByteBuffer {
        val normalizedPath = if (!uri.path.isNullOrEmpty()) uri.path else "/"
        val pathBytes = normalizedPath.toByteArray(Charsets.UTF_8)

        val requestBuffer = ByteBuffer.allocate(7 + pathBytes.size + body.size)
        // Write the request header to the buffer.
        requestBuffer.put(method.value)
        requestBuffer.putShort(pathBytes.size.toShort())
        requestBuffer.put(pathBytes)
        requestBuffer.putInt(body.size)
        // Copy the request body to the buffer.
        requestBuffer.put(body)

        requestBuffer.flip()
        return requestBuffer
    }

    companion object {
        fun builder(uri: URI) = RequestBuilder(uri)
    }
}

class RequestBuilder(private val uri: URI) {
    private var method: Method = Method.GET
    private var body: ByteArray = ByteArray(0)

    fun method(method: Method): RequestBuilder {
        this.method = method
        return this
    }

    fun body(body: String): RequestBuilder {
        this.body = body.toByteArray(Charsets.UTF_8)
        return this
    }

    fun body(body: ByteArray): RequestBuilder {
        this.body = body
        return this
    }

    fun build(): Request {
        return Request(uri, method, body)
    }
}