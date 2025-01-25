package dev.flavored.yap

import dev.flavored.yap.network.ResourceLoader
import dev.flavored.yap.network.YapClient
import javax.swing.UIManager

object Application {
    private val crossContextClient = YapClient()
    // NOTE: Resource loaders should probably be per-context.
    val resourceLoader = ResourceLoader(crossContextClient)

    private fun parseArguments(args: Array<String>) {
        if (args.isEmpty()) {
            return
        }

        for (i in args.indices) {
            when (args[i]) {
                "--server" -> TODO("Running as a server is not supported yet.")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        parseArguments(args)

        System.setProperty("awt.useSystemAAFontSettings", "lcd");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
        } catch (ignored: Exception) {
        }

        val window = BrowserWindow()
        window.start()
    }
}