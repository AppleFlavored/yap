package dev.flavored.yap.network

import java.io.IOException
import java.net.InetSocketAddress
import java.net.URI
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class YapClient {
    fun get(uri: URI): Result<YapResponse> {
        return makeRequest(Method.GET, uri, byteArrayOf())
    }

    fun post(uri: URI, body: ByteArray): Result<YapResponse> {
        return makeRequest(Method.POST, uri, body)
    }

    private fun makeRequest(method: Method, uri: URI, content: ByteArray): Result<YapResponse> {
        if (uri.scheme != "yap") {
            return Result.failure(IllegalArgumentException("URI schemes other than yap:// are not supported."))
        }

        val channel = SocketChannel.open()
        val port = if (uri.port >= 0) uri.port else 5713
        try {
            channel.connect(InetSocketAddress(uri.host, port))
        } catch (e: IOException) {
            return Result.failure(e)
        }

        val path = (uri.path ?: "/").ifEmpty { "/" }
        val requestBuffer = createRequestBuffer(method, path, content.size)
        requestBuffer.put(content)
        requestBuffer.flip()
        channel.write(requestBuffer)

        val response = parseResponseBuffer(channel)
        channel.close()

        return Result.success(response)
    }

    private fun createRequestBuffer(method: Method, path: String, contentLength: Int): ByteBuffer {
        // Request Header:
        // <1:method> <2:path_len> <path_len:path> <4:content_length>
        val buffer = ByteBuffer.allocate(7 + path.length + contentLength)
        buffer.put(method.value)
        buffer.putShort(path.length.toShort())
        buffer.put(path.toByteArray())
        buffer.putInt(contentLength)

        return buffer
    }

    private fun parseResponseBuffer(channel: SocketChannel): YapResponse {
        // Response Header:
        // <2:status_code> <4:content_length>
        val headerBuffer = ByteBuffer.allocate(6)
        while (headerBuffer.hasRemaining()) {
            channel.read(headerBuffer)
        }
        headerBuffer.flip()

        val statusCode = headerBuffer.getShort()
        val contentLength = headerBuffer.getInt()

        val contentBuffer = ByteBuffer.allocate(contentLength)
        while (contentBuffer.hasRemaining()) {
            channel.read(contentBuffer)
        }
        contentBuffer.flip()

        return YapResponse(statusCode, "No-Type", contentLength, contentBuffer.array())
    }
}