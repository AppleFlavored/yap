package dev.flavored.yap.extension

import java.nio.ByteBuffer

fun ByteBuffer.getString(length: Int): String {
    val bytes = ByteArray(length)
    get(bytes)
    return bytes.decodeToString()
}