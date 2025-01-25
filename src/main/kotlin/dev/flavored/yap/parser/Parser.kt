package dev.flavored.yap.parser

import dev.flavored.yap.document.*
import org.slf4j.LoggerFactory

class Parser(source: String) {
    private val logger = LoggerFactory.getLogger(Parser::class.java)
    private val tokenizer = Tokenizer(source)
    private var currentToken: Token = tokenizer.next()
    private val openElements = mutableListOf<ParentElement>()

    fun parse(): Document {
        // If the source is empty, return an empty document.
        if (currentToken.kind == TokenKind.EOF) {
            return Document()
        }

        openElements.add(Document())
        while (tokenizer.hasNext()) {
            parseElement()
        }

        val document = openElements.removeLast()
        if (document is Document) {
            return document
        } else {
            logger.warn("Expected document as last element (${openElements.count()} left on stack)")
            return Document()
        }
    }

    private fun parseElement() {
        if (currentToken.kind == TokenKind.SYMBOL) {
            val elementName = currentToken.value
            advance()

            if (currentToken.kind == TokenKind.OPEN_PAREN) {
                advance()
                if (currentToken.kind == TokenKind.CLOSE_PAREN) {
                    consume(TokenKind.CLOSE_PAREN)
                    return handleElementWithNoAttributes(elementName)
                }

                if (currentToken.kind == TokenKind.SYMBOL) {
                    val attributes = parseAttributes()
                    consume(TokenKind.CLOSE_PAREN)
                    return handleElementWithAttributes(elementName, attributes)
                }

                if (currentToken.kind == TokenKind.STRING) {
                    val textContent = currentToken.value
                    advance()
                    consume(TokenKind.CLOSE_PAREN)
                    return handleElementWithNoAttributes(elementName, textContent)
                }

                return handleElementWithNoAttributes(elementName)
            }

            if (currentToken.kind == TokenKind.OPEN_BRACE) {
                advance()
                return handleParentElement(elementName)
            }
        }

        if (currentToken.kind == TokenKind.CLOSE_BRACE) {
            advance()
            openElements.removeLast()
            return
        }

        advance()
    }

    private fun parseAttributes(): Map<String, String> {
        val attributes = mutableMapOf<String, String>()
        while (tokenizer.hasNext() && currentToken.kind != TokenKind.CLOSE_PAREN) {
            if (currentToken.kind != TokenKind.SYMBOL) {
                logger.warn("Expected attribute name, but found '${currentToken.value}' instead.")
                skipMalformedAttribute()
                continue
            }

            val attributeName = currentToken.value
            advance()
            consume(TokenKind.EQ)

            if (currentToken.kind != TokenKind.STRING) {
                logger.warn("Expected attribute value, but found '${currentToken.value}' instead.")
                skipMalformedAttribute()
                continue
            }
            val attributeValue = currentToken.value

            attributes[attributeName] = attributeValue
        }
        println(attributes)
        return attributes
    }

    private fun skipMalformedAttribute() {
        while (tokenizer.hasNext() && currentToken.kind != TokenKind.CLOSE_PAREN && currentToken.kind != TokenKind.SYMBOL) {
            advance()
        }
    }

    private fun handleParentElement(elementName: String) {
        val element = when (elementName) {
            "row" -> ContainerElement(ContainerElement.Direction.ROW)
            "col" -> ContainerElement(ContainerElement.Direction.COLUMN)
            else -> {
                logger.warn("Encountered unknown parent element '$elementName' while parsing.")
                ContainerElement(ContainerElement.Direction.ROW)
            }
        }
        openElements.last().children.add(element)
        openElements.add(element)
    }

    private fun handleElementWithNoAttributes(elementName: String, textContent: String = "") {
        val element = when (elementName) {
            "p" -> ParagraphElement().apply { text = textContent }
            else -> {
                logger.warn("Encountered unknown element '$elementName' while parsing.")
                ParagraphElement()
            }
        }
        openElements.last().children.add(element)
    }

    private fun handleElementWithAttributes(elementName: String, attributes: Map<String, String>) {
        val element = when (elementName) {
            "img" -> ImageElement(attributes["src"] ?: "")
            else -> {
                logger.warn("Encountered unknown element with attributes '$elementName' while parsing.")
                ParagraphElement()
            }
        }
        openElements.last().children.add(element)
    }

    private fun consume(kind: TokenKind) {
        if (currentToken.kind == kind) {
            advance()
        }
    }

    private fun advance() {
        currentToken = tokenizer.next()
    }
}