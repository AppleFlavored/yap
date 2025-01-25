package dev.flavored.yap.document

class ContainerElement(val direction: Direction = Direction.COLUMN) : ParentElement() {
    override fun toString(): String {
        return "ContainerElement(direction=$direction)"
    }

    enum class Direction {
        ROW,
        COLUMN
    }
}