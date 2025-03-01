package dev.flavored.yap.dom

abstract class Element(val elementName: String) : Node() {
    private val attributes: MutableMap<String, String> = mutableMapOf()

    fun setAttribute(name: String, value: String) {
        attributes[name] = value
    }

    fun setAttributes(attributes: Map<String, String>) {
        this.attributes.putAll(attributes)
    }

    fun getAttribute(name: String): String {
        return attributes[name] ?: ""
    }

    override fun toString(): String {
        return "Element(elementName='$elementName', attributes=$attributes)"
    }
}