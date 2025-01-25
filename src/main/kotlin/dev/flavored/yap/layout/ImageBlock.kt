package dev.flavored.yap.layout

import dev.flavored.yap.Application
import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.image.BufferedImage
import java.net.URI

class ImageBlock(resourceUri: URI) : Block() {
    private val emptyImage = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    private val image = Application.resourceLoader.loadImageResource(resourceUri) ?: emptyImage

    override fun measure(containerWidth: Float, fontRenderContext: FontRenderContext) {
        width = image.width.toFloat()
        height = image.height.toFloat()
    }

    override fun render(graphics: Graphics2D) {
        graphics.drawImage(image, x.toInt(), y.toInt(), width.toInt(), height.toInt(), null)
    }
}