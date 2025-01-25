package dev.flavored.yap.network

import dev.flavored.yap.extension.getString
import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicBoolean

class ServerInstance {
    private val logger = LoggerFactory.getLogger(ServerInstance::class.java)
    private val selector = Selector.open()
    private val serverChannel = ServerSocketChannel.open()
    private val stopped = AtomicBoolean(false)

    fun start(address: SocketAddress) {
        serverChannel.bind(address)
        serverChannel.configureBlocking(false)
        serverChannel.register(selector, SelectionKey.OP_ACCEPT)

        logger.info("Running as a server -- Listening on $address")

        while (!stopped.get()) {
            selector.select()
            val iterator = selector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (key.isAcceptable) {
                    val clientChannel = serverChannel.accept()
                    clientChannel.configureBlocking(false)
                    clientChannel.register(selector, SelectionKey.OP_READ)

                    logger.info("Connection from ${clientChannel.remoteAddress}")
                }
                if (key.isReadable) {
                    Thread.startVirtualThread {
                        handleConnection(key.channel() as SocketChannel)
                    }
                }
                iterator.remove()
            }
        }
    }

    private fun handleConnection(channel: SocketChannel) {
        val request = parseRequestBuffer(channel).getOrElse { exception ->
            logger.warn("Received invalid request buffer:\n${exception.stackTraceToString()}")
            channel.close()
            return
        }

        logger.info("Received request: $request")

        if (request.method != Method.GET) {
            val responseBuffer = createResponseBuffer(405, "Method Not Allowed".toByteArray())
            channel.write(responseBuffer)
            channel.close()
            return
        }

        return createResponseBuffer(200, "Hello, World!".toByteArray()).let { responseBuffer ->
            channel.write(responseBuffer)
            channel.close()
        }
    }

    // TODO: Create a buffer pool to avoid creating new buffers for each request.
    private fun parseRequestBuffer(channel: SocketChannel) = runCatching<YapRequest> {
        // Request Header:
        // <1:method> <2:path_len> <path_len:path> <4:content_length>
        val methodAndPathBuffer = ByteBuffer.allocate(3)
        while (methodAndPathBuffer.hasRemaining()) {
            channel.read(methodAndPathBuffer)
        }
        methodAndPathBuffer.flip()

        val method = Method.fromValue(methodAndPathBuffer.get())
        val pathLength = methodAndPathBuffer.getShort()

        val pathAndContentLengthBuffer = ByteBuffer.allocate(pathLength + 4)
        while (pathAndContentLengthBuffer.hasRemaining()) {
            channel.read(pathAndContentLengthBuffer)
        }
        pathAndContentLengthBuffer.flip()

        val path = pathAndContentLengthBuffer.getString(pathLength.toInt())
        val contentLength = pathAndContentLengthBuffer.getInt()

        val contentBuffer = ByteBuffer.allocate(contentLength)
        while (contentBuffer.hasRemaining()) {
            channel.read(contentBuffer)
        }
        contentBuffer.flip()

        println("Sending content buffer with capacity: ${contentBuffer.capacity()}")

        return@runCatching YapRequest(method, path, contentBuffer.array())
    }

    private fun createResponseBuffer(statusCode: Short, content: ByteArray): ByteBuffer {
        // Response Header:
        // <2:status_code> <4:content_length>
        val buffer = ByteBuffer.allocate(6 + content.size)
        buffer.putShort(statusCode)
        buffer.putInt(content.size)
        buffer.put(content)
        return buffer
    }
}