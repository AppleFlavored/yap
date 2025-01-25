package dev.flavored.yap.network

import org.slf4j.LoggerFactory
import java.awt.image.BufferedImage
import java.io.IOException
import java.io.InputStream
import java.net.URI
import javax.imageio.ImageIO

class ResourceLoader(private val client: YapClient) {
    private val logger = LoggerFactory.getLogger(ResourceLoader::class.java)

    fun loadImageResource(uri: URI): BufferedImage? {
        val stream = loadResource(uri)
        try {
            return ImageIO.read(stream)
        } catch (exception: IOException) {
            logger.warn("Failed to read image resource from $uri: ${exception.message}")
            return null
        }
    }

    fun loadResource(uri: URI): InputStream {
        return when (uri.scheme) {
            "yap" -> loadNetworkResource(uri)
            "internal" -> loadInternalResource(uri)
            else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
        }
    }

    private fun loadNetworkResource(uri: URI): InputStream {
        assert(uri.scheme == "yap") { "URI scheme must be yap://" }
        val response = client.get(uri).getOrElse { exception ->
            logger.warn("Failed to load network resource from $uri: ${exception.message}")
            return InputStream.nullInputStream()
        }
        if (!response.isSuccessful) {
            logger.warn("Request for network resource returned non-successful status code ${response.statusCode}")
            return InputStream.nullInputStream()
        }
        return response.getBodyAsInputStream()
    }

    private fun loadInternalResource(uri: URI): InputStream {
        assert(uri.scheme == "internal") { "URI scheme must be internal://" }
        return ResourceLoader::class.java.getResourceAsStream(uri.path)
            ?: InputStream.nullInputStream()
    }
}