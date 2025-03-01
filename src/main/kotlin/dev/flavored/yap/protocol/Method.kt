package dev.flavored.yap.protocol


enum class Method(val value: Byte) {
    GET(0),
    POST(1);

    companion object {
        fun fromValue(value: Byte): Method {
            return when (value) {
                0.toByte() -> GET
                1.toByte() -> POST
                else -> throw IllegalArgumentException("Invalid method value: $value")
            }
        }
    }
}