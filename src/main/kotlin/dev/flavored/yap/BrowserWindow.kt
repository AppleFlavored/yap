package dev.flavored.yap

import java.awt.*
import java.net.URI
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.WindowConstants

class BrowserWindow {
    private val frame = JFrame("Browser Demo").apply {
        defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        setSize(900, 600)
        minimumSize = Dimension(400, 300)
    }
    private val contentView = ContentView()

    fun start() {
        val topPanel = createTopPanel()
        frame.contentPane.add(topPanel, BorderLayout.NORTH)

        val scrollPane = JScrollPane(contentView).apply {
            border = null
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
        frame.contentPane.add(scrollPane, BorderLayout.CENTER)

        frame.isVisible = true
    }

    private fun createTopPanel(): JPanel {
        val addressField = JTextField("").apply {
            font = Font("Times", Font.PLAIN, 13)
            border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
            margin.set(0, 5, 0, 0)
            addActionListener {
                val result = contentView.loadPage(URI.create(text))
                if (result.isFailure) {
                    contentView.loadPage(URI.create("internal:///pages/network-error.yap"))
                    return@addActionListener
                }
                contentView.requestFocusInWindow()
            }
        }

        return JPanel().apply {
            layout = BoxLayout(this@apply, BoxLayout.X_AXIS)
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 5, 4, 5)
            )
            add(addressField)
        }
    }
}