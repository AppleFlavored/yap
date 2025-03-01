package dev.flavored.yap.protocol

import java.io.ByteArrayInputStream
import java.lang.RuntimeException
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class ProtocolClient {
    fun send(request: Request): Result<Response> {
        val host = requireNotNull(request.uri.host) { "Hostname must not be null" }
        val port = request.uri.port.takeIf { it >= 0 } ?: 5713

        val address = InetSocketAddress(host, port)
        if (address.isUnresolved) {
            return Result.failure(RuntimeException("Unable to resolve host: ${request.uri.host}"))
        }

        val socket = runCatching { SocketChannel.open(address) }
            .getOrElse { return Result.failure(it) }

        socket.write(request.serializeToBytes())

        val responseHeaderBuffer = ByteBuffer.allocate(ResponseHeader.SIZE)
        socket.read(responseHeaderBuffer)
        responseHeaderBuffer.flip()
        val responseHeader = ResponseHeader(responseHeaderBuffer)

        val responseBody = ByteBuffer.allocate(responseHeader.contentLength)
        socket.read(responseBody)
        val stream = ByteArrayInputStream(responseBody.array().copyOf())
        return Result.success(Response(responseHeader.status, stream))
    }
}