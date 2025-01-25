package dev.flavored.yap.document

class ParagraphElement : Element() {
    var text: String = ""

    override fun toString(): String {
        return "ParagraphElement(text=\"$text\")"
    }
}