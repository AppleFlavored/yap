package dev.flavored.yap.layout

import dev.flavored.yap.dom.Node

class LayoutNode(private val node: Node) {
    private var x: Int = 0
    private var y: Int = 0
    private var width: Int = 0
    private var height: Int = 0
    private val children = mutableListOf<LayoutNode>()

    fun addChild(child: LayoutNode) {
        children.add(child)
    }

    fun getChildren(): List<LayoutNode> {
        return children
    }
}