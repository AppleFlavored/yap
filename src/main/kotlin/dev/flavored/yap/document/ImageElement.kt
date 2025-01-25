package dev.flavored.yap.document

class ImageElement(val src: String) : Element() {
    override fun toString(): String {
        return "ImageElement(url=\"$src\")"
    }
}