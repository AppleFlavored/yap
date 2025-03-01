package dev.flavored.yap.ui

import dev.flavored.yap.dom.Document
import java.awt.Graphics
import javax.swing.JComponent

class DocumentView : JComponent() {
    private var document: Document? = null

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
    }
}