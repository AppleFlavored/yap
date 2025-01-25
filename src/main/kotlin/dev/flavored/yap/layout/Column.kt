package dev.flavored.yap.layout

import java.awt.font.FontRenderContext

class Column : Container() {

    override fun layout(containerWidth: Float, containerHeight: Float, fontRenderContext: FontRenderContext) {
        var posY = y
        for (child in children) {
            child.x = x
            child.y = posY

            child.measure(containerWidth, fontRenderContext)
            posY += child.height
        }
        width = children.maxOf { it.width }
        height = posY - y

        for (child in children) {
            if (child is Container) {
                child.layout(width, height, fontRenderContext)
            }
        }
    }
}