package dev.flavored.yap.parser


class Tokenizer(private val source: String) {
    private var position = 0

    fun next(): Token {
        while (peek().isWhitespace()) {
            position++
        }

        if (!hasNext()) {
            return Token(TokenKind.EOF, "\u0000")
        }

        val start = position

        // Tokenize a string literal.
        // TODO: Support escape sequences.
        if (peek() == '\"') {
            position++
            while (hasNext() && peek() != '\"') {
                position++
            }
            position++
            return Token(TokenKind.STRING, source.substring(start + 1, position - 1))
        }

        // Tokenize a number literal.
        if (peek().isDigit()) {
            position++
            while (peek().isDigit()) {
                position++
            }
            // If there is a decimal point and a digit, consume it and any following digits.
            if (peek() == '.' && peek(1).isDigit()) {
                position++
                while (peek().isDigit()) {
                    position++
                }
            }
            return Token(TokenKind.NUMBER, source.substring(start, position))
        }

        // Tokenize an symbol.
        if (peek().isLetter()) {
            position++
            while (peek().isLetterOrDigit()) {
                position++
            }
            return Token(TokenKind.SYMBOL, source.substring(start, position))
        }

        // Tokenize any unknown characters as a CHARACTER token.
        position++
        return when (source[start]) {
            '=' -> Token(TokenKind.EQ, "=")
            '(' -> Token(TokenKind.OPEN_PAREN, "(")
            ')' -> Token(TokenKind.CLOSE_PAREN, ")")
            '{' -> Token(TokenKind.OPEN_BRACE, "{")
            '}' -> Token(TokenKind.CLOSE_BRACE, "}")
            else -> Token(TokenKind.CHARACTER, source.substring(start, position))
        }
    }

    private fun peek(offset: Int = 0): Char {
        return if (hasNext()) {
            source[position + offset]
        } else {
            '\u0000'
        }
    }

    fun hasNext(): Boolean {
        return position < source.length
    }
}