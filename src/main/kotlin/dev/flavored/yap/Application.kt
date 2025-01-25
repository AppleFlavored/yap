package dev.flavored.yap

import dev.flavored.yap.network.ResourceLoader
import dev.flavored.yap.network.ServerInstance
import dev.flavored.yap.network.YapClient
import java.net.InetSocketAddress
import javax.swing.UIManager

object Application {
    private val crossContextClient = YapClient()
    // NOTE: Resource loaders should probably be per-context.
    val resourceLoader = ResourceLoader(crossContextClient)

    private var runAsServer = false

    private fun parseArguments(args: Array<String>) {
        if (args.isEmpty()) {
            return
        }

        for (i in args.indices) {
            when (args[i]) {
                "--server" -> runAsServer = true
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parseArguments(args)

        if (runAsServer) {
            val server = ServerInstance()
            server.start(InetSocketAddress(5713))
            return
        }

        System.setProperty("awt.useSystemAAFontSettings", "lcd");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ignored: Exception) {
        }

        val window = BrowserWindow()
        window.start()
    }
}