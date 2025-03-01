package dev.flavored.yap.layout

import dev.flavored.yap.dom.Node
import dev.flavored.yap.dom.TextNode

object LayoutTreeBuilder {
    fun build(node: Node): LayoutNode {
        if (node is TextNode) {
            return LayoutNode(node)
        }
    }
}