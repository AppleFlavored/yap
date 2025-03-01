package dev.flavored.yap.parser

data class Token(val kind: TokenKind, val lexeme: String, val position: Int)