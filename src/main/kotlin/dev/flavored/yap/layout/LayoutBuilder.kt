package dev.flavored.yap.layout

import dev.flavored.yap.document.*
import java.net.URI

class LayoutBuilder(private val body: Column) {

    fun build(document: Document) {
        body.children.clear()

        document.children.forEach { element ->
            body.children.add(mapElementToBlock(element))
        }
    }

    private fun mapElementToBlock(element: Element): Block {
        return when (element) {
            is ParagraphElement -> Text(element.text)
            is ContainerElement -> when (element.direction) {
                ContainerElement.Direction.ROW -> Row().apply {
                    element.children.forEach { child ->
                        children.add(mapElementToBlock(child))
                    }
                }
                ContainerElement.Direction.COLUMN -> Column().apply {
                    element.children.forEach { child ->
                        children.add(mapElementToBlock(child))
                    }
                }
            }
            is ImageElement -> ImageBlock(URI.create(element.src))
            else -> Text("Unknown element ${element.javaClass.simpleName}")
        }
    }
}