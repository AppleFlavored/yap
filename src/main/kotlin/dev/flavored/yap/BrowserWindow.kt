package dev.flavored.yap

import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.WindowConstants

class BrowserWindow {
    private val frame = JFrame().apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        size = Dimension(900, 600)
        minimumSize = Dimension(400, 300)
    }

    fun start() {
        frame.isVisible = true
    }
}