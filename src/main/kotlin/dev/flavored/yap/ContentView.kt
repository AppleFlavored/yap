package dev.flavored.yap

import dev.flavored.yap.document.Document
import dev.flavored.yap.layout.*
import dev.flavored.yap.network.ResourceLoader
import dev.flavored.yap.network.YapClient
import dev.flavored.yap.parser.Parser
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.InputStream
import java.net.URI
import javax.swing.JComponent
import javax.swing.SwingWorker
import javax.swing.event.MouseInputAdapter

class ContentView : JComponent() {
    private val logger = LoggerFactory.getLogger(ContentView::class.java)
    private val client = YapClient()
    private val resourceLoader = ResourceLoader(client)
    private var renderingBuffer = BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)
    private val body = Column()
    private val layoutBuilder = LayoutBuilder(body)

    init {
        isOpaque = true
        isFocusable = true

        val inputListener = ContentViewInputListener(this)
        addMouseListener(inputListener)
    }

    private val text = Text("Hello, World! This is a long line of text that will be broken up into multiple lines if it extends beyond the limits of its container.")
    private val text2 = Text("This is another block of text independent of the first one. It will also be broken up into multiple lines if it extends beyond the limits of its container.")

    private val row = Row().apply {
        children.add(Text("This is a row of text."))
        children.add(ColoredBlock(100f, 100f))
        children.add(ColoredBlock(100f, 100f))
        children.add(ColoredBlock(100f, 200f))
        children.add(ColoredBlock(100f, 100f))
        children.add(ColoredBlock(100f, 100f))
        children.add(ColoredBlock(100f, 100f))
    }

    init {
        body.children.add(row)
        body.children.add(text)
        body.children.add(text2)
        body.children.add(ImageBlock(URI.create("internal:///images/demo1.png")))
    }

    fun loadPage(uri: URI): Result<Unit> {
        val loadTask = PageRequestTask(client, uri) { document ->
            layoutBuilder.build(document)
            repaint()
        }
        loadTask.execute()

        return Result.success(Unit)
    }

    override fun paintComponent(g: Graphics) {
        g as Graphics2D
        g.background = Color.WHITE
        g.clearRect(0, 0, width, height)

        body.layout(width.toFloat(), height.toFloat(), g.fontRenderContext)

        if (renderingBuffer.width.toFloat() != body.width || renderingBuffer.height.toFloat() != body.height) {
            renderingBuffer = BufferedImage(body.width.toInt(), body.height.toInt(), BufferedImage.TYPE_INT_ARGB)
            preferredSize = Dimension(parent.width, body.height.toInt())
            repaintBuffer()
        }

        g.drawImage(renderingBuffer, 0, 0, null)
    }

    private fun repaintBuffer() {
        val g = renderingBuffer.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB)

        g.color = Color.BLACK
        body.render(g)

        g.dispose()
    }

    private class ContentViewInputListener(private val view: ContentView) : MouseInputAdapter() {
        override fun mouseClicked(e: MouseEvent) {
            view.requestFocusInWindow()
        }
    }

    private class PageRequestTask(private val client: YapClient, private val uri: URI, private val readyCallback: (Document) -> Unit) : SwingWorker<Document, Any>() {
        override fun doInBackground(): Document {
            val content = when (uri.scheme) {
                "yap" -> {
                    val response = client.get(uri).getOrThrow()
                    response.getBodyAsString()
                }
                "internal" -> {
                    val stream = ContentView::class.java.getResourceAsStream(uri.path)
                        ?: InputStream.nullInputStream()
                    stream.reader(Charsets.UTF_8).readText()
                }
                else -> throw IllegalArgumentException("Unsupported URI scheme: ${uri.scheme}")
            }

            val parser = Parser(content)
            return parser.parse()
        }

        override fun done() {
            readyCallback(get())
        }
    }
}