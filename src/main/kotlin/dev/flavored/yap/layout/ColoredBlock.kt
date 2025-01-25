package dev.flavored.yap.layout

import java.awt.Color
import java.awt.Graphics2D
import java.awt.font.FontRenderContext

class ColoredBlock(width: Float, height: Float) : Block() {
    private val color = Color((0..0xffffff).random())

    init {
        this.width = width
        this.height = height
    }

    override fun measure(containerWidth: Float, fontRenderContext: FontRenderContext) {
    }

    override fun render(graphics: Graphics2D) {
        val previousColor = graphics.color
        graphics.color = color
        graphics.fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
        graphics.color = previousColor
    }
}