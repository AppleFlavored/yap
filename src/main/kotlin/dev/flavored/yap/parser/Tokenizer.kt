package dev.flavored.yap.parser

class Tokenizer(private val source: String) {
    private var position: Int = 0
    val hasNext get() = position < source.length

    fun nextToken(): Token {
        while (hasNext && peek().isWhitespace()) {
            position++
        }

        if (position >= source.length) {
            return Token(TokenKind.EOF, "\u0000", position)
        }

        val start = position
        if (peek().isLetter()) {
            position++
            while (hasNext && peek().isIdentifierPart()) {
                position++
            }
            return Token(TokenKind.IDENTIFIER, source.substring(start, position), position)
        }

        if (peek() == '\"') {
            position++
            while (hasNext && peek() != '\"') {
                position++
            }
            position++
            return Token(TokenKind.STRING, source.substring(start + 1, position - 1), position)
        }

        val kind = when (peek()) {
            '(' -> TokenKind.LPAREN
            ')' -> TokenKind.RPAREN
            '{' -> TokenKind.LBRACE
            '}' -> TokenKind.RBRACE
            ':' -> TokenKind.COLON
            ',' -> TokenKind.COMMA
            else -> TokenKind.UNKNOWN
        }
        position++
        return Token(kind, source.substring(start, position), position)
    }

    private fun peek(): Char {
        if (!hasNext) {
            return '\u0000'
        }
        return source[position]
    }

    private fun Char.isIdentifierPart() = isLetterOrDigit() || this == '_'
}