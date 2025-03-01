package dev.flavored.yap

import dev.flavored.yap.parser.Parser

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty("awt.useSystemAAFontSettings", "lcd")

        val parser = Parser()
        parser.reset("p(\"Hello, Yap!\") img(src:\"example.png\")")
        val document = parser.parse()
        println(document.printToString())

//        val client = ProtocolClient()
//        val response = client.send(Request.builder(URI("yap://localhost"))
//            .body("Hello, Yap!")
//            .build())
//
//        val responseBody = response.getOrNull() ?: return
//        println(responseBody.getBodyAsString().getOrNull())
    }
}