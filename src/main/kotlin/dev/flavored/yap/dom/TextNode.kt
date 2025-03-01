package dev.flavored.yap.dom

class TextNode(val text: String) : Node() {
    override fun appendChild(node: Node) {
        throw UnsupportedOperationException("Text nodes cannot have children")
    }

    override fun removeChild(node: Node) {
        throw UnsupportedOperationException("Text nodes cannot have children")
    }

    override fun toString(): String {
        return "TextNode('$text')"
    }
}