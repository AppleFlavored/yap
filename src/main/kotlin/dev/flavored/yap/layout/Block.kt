package dev.flavored.yap.layout

import java.awt.Graphics2D
import java.awt.font.FontRenderContext

abstract class Block {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    abstract fun measure(containerWidth: Float, fontRenderContext: FontRenderContext)

    abstract fun render(graphics: Graphics2D)
}