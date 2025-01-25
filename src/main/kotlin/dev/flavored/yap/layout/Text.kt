package dev.flavored.yap.layout

import java.awt.Graphics2D
import java.awt.font.FontRenderContext
import java.awt.font.LineBreakMeasurer
import java.awt.font.TextAttribute
import java.awt.font.TextLayout
import java.text.AttributedString

class Text(text: String) : Block() {
    private val attributedString = AttributedString(text.padEnd(1, ' ')).apply {
        addAttribute(TextAttribute.SIZE, 13.3f)
    }
    private val lines = mutableListOf<Line>()

    override fun measure(containerWidth: Float, fontRenderContext: FontRenderContext) {
        lines.clear()

        val paragraph = attributedString.iterator
        val paragraphStart = paragraph.beginIndex
        val paragraphEnd = paragraph.endIndex
        val measurer = LineBreakMeasurer(paragraph, fontRenderContext)

        var relativePosY = 0f
        var blockWidth = 0f
        measurer.position = paragraphStart

        while (measurer.position < paragraphEnd) {
            val layout = measurer.nextLayout(containerWidth)
            val relativePosX = if (layout.isLeftToRight) 0f else containerWidth - layout.advance

            relativePosY += layout.ascent
            blockWidth = maxOf(blockWidth, layout.advance)
            lines.add(Line(layout, layout.advance, relativePosX, relativePosY))
            relativePosY += layout.descent + layout.leading
        }

        width = blockWidth
        height = relativePosY
    }

    override fun render(graphics: Graphics2D) {
        lines.forEach { line ->
            line.layout.draw(graphics, x + line.relativePosX, y + line.relativePosY)
        }
    }

    private data class Line(val layout: TextLayout, val width: Float, val relativePosX: Float, val relativePosY: Float)
}