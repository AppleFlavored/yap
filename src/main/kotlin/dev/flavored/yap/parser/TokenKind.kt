package dev.flavored.yap.parser

enum class TokenKind {
    EOF,
    CHARACTER,
    SYMBOL,
    STRING,
    NUMBER,
    EQ,
    OPEN_PAREN,
    CLOSE_PAREN,
    OPEN_BRACE,
    CLOSE_BRACE,
}