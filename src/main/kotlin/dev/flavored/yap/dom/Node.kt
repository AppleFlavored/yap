package dev.flavored.yap.dom

import java.util.Collections

abstract class Node {
    private var parent: Node? = null
    private val children = mutableListOf<Node>()

    fun getParent(): Node? {
        return parent
    }

    fun getChildrenView(): List<Node> {
        return Collections.unmodifiableList(children)
    }

    open fun appendChild(node: Node) {
        children.add(node)
        node.parent = this
    }

    open fun removeChild(node: Node) {
        children.remove(node)
        node.parent = null
    }

    fun printToString(): String {
        val builder = StringBuilder()
        printToString(builder, 0)
        return builder.toString()
    }

    private fun printToString(builder: StringBuilder, level: Int) {
        builder.append(" ".repeat(level * 2)).append(this.toString()).append('\n')
        for (child in children) {
            child.printToString(builder, level + 1)
        }
    }
}