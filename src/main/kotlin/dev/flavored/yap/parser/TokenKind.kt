package dev.flavored.yap.parser

enum class TokenKind {
    EOF,
    UNKNOWN,
    IDENTIFIER,
    STRING,

    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    COLON,
    COMMA
}