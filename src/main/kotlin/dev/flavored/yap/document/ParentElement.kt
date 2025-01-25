package dev.flavored.yap.document

abstract class ParentElement : Element() {
    val children = mutableListOf<Element>()

    fun dump(level: Int = 0): String {
        val builder = StringBuilder()
        for (child in children) {
            builder.append("  ".repeat(level))
            builder.appendLine(child.toString())
            if (child is ParentElement) {
                builder.append(child.dump(level + 1))
            }
        }
        return builder.toString()
    }
}