package dev.flavored.yap.layout

import java.awt.font.FontRenderContext
import kotlin.math.max

class Row : Container() {

    // TODO: This is not the best way to handle this...
    override fun measure(containerWidth: Float, fontRenderContext: FontRenderContext) {
        if (children.isEmpty()) {
            return
        }

        var posX = x
        var posY = y
        var maxWidth = 0f
        var rowHeight = children.first().height

        for (child in children) {
            child.measure(containerWidth, fontRenderContext)
            if (posX + child.width > containerWidth) {
                posX = x
                posY += rowHeight
                rowHeight = 0f
            }
            posX += child.width
            maxWidth = max(maxWidth, posX)
            rowHeight = max(rowHeight, child.height)
        }

        width = maxWidth
        height = posY + rowHeight
    }

    override fun layout(containerWidth: Float, containerHeight: Float, fontRenderContext: FontRenderContext) {
        if (children.isEmpty()) {
            return
        }

        var posX = x
        var posY = y
        var rowHeight = children.first().height

        for (child in children) {
            if (posX + child.width > containerWidth) {
                posX = x
                posY += rowHeight
                rowHeight = 0f
            }
            child.x = posX
            child.y = posY
            posX += child.width
            rowHeight = max(rowHeight, child.height)

            if (child is Container) {
                child.layout(width, height, fontRenderContext)
            }
        }
    }
}