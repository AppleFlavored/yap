package dev.flavored.yap.layout

import java.awt.Graphics2D
import java.awt.font.FontRenderContext

abstract class Container : Block() {
    val children = mutableListOf<Block>()

    abstract fun layout(containerWidth: Float, containerHeight: Float, fontRenderContext: FontRenderContext)

    override fun measure(containerWidth: Float, fontRenderContext: FontRenderContext) {
        // Containers measure themselves during layout (except rows for some reason?)
    }

    override fun render(graphics: Graphics2D) {
        children.forEach { it.render(graphics) }

//        val previousColor = graphics.color
//        graphics.color = Color.RED
//        graphics.drawRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
//        graphics.color = previousColor
    }
}