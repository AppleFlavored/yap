package dev.flavored.yap.parser

import dev.flavored.yap.dom.*
import java.util.Optional

class Parser {
    private var tokenizer = Tokenizer("")
    private var currentToken: Token = tokenizer.nextToken()
    private var context = ParserContext.ELEMENTS
    private val openElements = mutableListOf<Element>()

    fun reset(source: String) {
        tokenizer = Tokenizer(source)
        currentToken = tokenizer.nextToken()
        openElements.clear()
    }

    fun parse(): Document {
        openElements.addLast(RootElement())

        while (currentToken.kind != TokenKind.EOF) {
            parseElement().ifPresent { openElements.last().appendChild(it) }
        }

        val document = Document()
        assert(openElements.size == 1) { "Expected one element in the open elements stack" }
        // Append the root element to the document node, and return.
        document.appendChild(openElements.removeLast())
        return document
    }

    private fun parseElement(): Optional<Element> {
        val nameToken = currentToken
        if (nameToken.kind != TokenKind.IDENTIFIER) {
            return Optional.empty()
        }
        advance()

        val element = createElement(nameToken.lexeme)

        // If an LPAREN is present, parse the attributes.
        context = ParserContext.ATTRIBUTES
        if (currentToken.kind == TokenKind.LPAREN) {
            parseAttributes(element)
        }

        context = ParserContext.ELEMENTS
        return Optional.of(element)
    }

    private fun parseAttributes(element: Element) {
        val attributes = mutableMapOf<String, String>()

        advance() // Skip the LPAREN token.
        if (currentToken.kind == TokenKind.RPAREN) {
            advance()
            return
        }

        // If the token is a STRING, then there are no attributes. Instead, append a text node to the open element.
        if (currentToken.kind == TokenKind.STRING) {
            element.appendChild(TextNode(currentToken.lexeme))
            advance() // Skip the STRING token.
            if (currentToken.kind == TokenKind.RPAREN) {
                advance()
            }
            return
        }

        // TODO: Handle the case where the LPAREN is missing.
        while (currentToken.kind != TokenKind.RPAREN) {
            val attributeName = currentToken
            if (attributeName.kind != TokenKind.IDENTIFIER) {
                handleParserError()
                element.setAttributes(attributes)
                return
            }
            advance()

            // Skip the COLON token. If it's not present, skip the attribute value.
            if (currentToken.kind != TokenKind.COLON) {
                handleParserError()
                element.setAttributes(attributes)
                return
            }

            advance()
            val attributeValue = currentToken
            if (attributeValue.kind != TokenKind.STRING) {
                handleParserError()
                element.setAttributes(attributes)
                return
            }
            advance() // Skip the attribute value token.

            attributes[attributeName.lexeme] = attributeValue.lexeme
        }

        // If the RPAREN token is present, skip it. Otherwise, continue as if it was present.
        if (currentToken.kind == TokenKind.RPAREN) {
            advance()
        }

        element.setAttributes(attributes)
    }

    private fun createElement(name: String): Element {
        val element = when (name) {
            "p" -> ParagraphElement()
            "img" -> ImageElement()
            else -> throw IllegalArgumentException("Unknown element: $name")
        }
        return element
    }

    private fun advance() {
        currentToken = tokenizer.nextToken()
    }

    private fun handleParserError() {
        // Skip tokens until we reach a context terminator.
        while (!isContextTerminator(currentToken)) {
            advance()
        }
        // Skip the terminator token itself.
        advance()
    }

    private fun isContextTerminator(token: Token): Boolean {
        if (token.kind == TokenKind.EOF) {
            return true
        }
        return when (context) {
            ParserContext.ELEMENTS -> {
                token.kind == TokenKind.RBRACE || token.kind == TokenKind.RPAREN
            }
            ParserContext.INSIDE_ELEMENT -> {
                token.kind == TokenKind.LBRACE
            }
            ParserContext.ATTRIBUTES -> {
                token.kind == TokenKind.RPAREN
            }
        }
    }
}

enum class ParserContext {
    ELEMENTS,
    INSIDE_ELEMENT,
    ATTRIBUTES
}