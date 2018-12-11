package org.yinwang.yin.parser


import org.yinwang.yin.Constants
import org.yinwang.yin.Util
import org.yinwang.yin.ast.*

import java.util.ArrayList


/**
 * Lexer
 * split text stream into tokens, nubmers, delimeters etc
 */
class Lexer(file: String) {

    var file: String
    var text: String? = null

    // current offset indicators
    var offset: Int = 0
    var line: Int = 0
    var col: Int = 0


    init {
        this.file = Util.unifyPath(file)
        this.text = Util.readFile(file)
        this.offset = 0
        this.line = 0
        this.col = 0

        if (text == null) {
            Util.abort("failed to read file: $file")
        }

        Delimeter.addDelimiterPair(Constants.PAREN_BEGIN, Constants.PAREN_END)
        Delimeter.addDelimiterPair(Constants.SQUARE_BEGIN, Constants.SQUARE_END)

        Delimeter.addDelimiter(Constants.ATTRIBUTE_ACCESS)
    }


    fun forward() {
        if (text!![offset] == '\n') {
            line++
            col = 0
            offset++
        } else {
            col++
            offset++
        }
    }


    fun skip(n: Int) {
        for (i in 0 until n) {
            forward()
        }
    }


    fun skipSpaces(): Boolean {
        var found = false

        while (offset < text!!.length && Character.isWhitespace(text!![offset])) {
            found = true
            forward()
        }
        return found
    }


    fun skipComments(): Boolean {
        var found = false

        if (text!!.startsWith(Constants.LINE_COMMENT, offset)) {
            found = true

            // skip to line end
            while (offset < text!!.length && text!![offset] != '\n') {
                forward()
            }
            if (offset < text!!.length) {
                forward()
            }
        }
        return found
    }


    fun skipSpacesAndComments() {
        while (skipSpaces() || skipComments()) {
            // actions are performed by skipSpaces() and skipComments()
        }
    }


    @Throws(ParserException::class)
    fun scanString(): Node {
        val start = offset
        val startLine = line
        val startCol = col
        skip(Constants.STRING_START.length)    // skip quote mark

        while (true) {
            // detect runaway strings at end of file or at newline
            if (offset >= text!!.length || text!![offset] == '\n') {
                throw ParserException("runaway string", startLine, startCol, offset)
            } else if (text!!.startsWith(Constants.STRING_END, offset)) {
                skip(Constants.STRING_END.length)    // skip quote mark
                break
            } else if (text!!.startsWith(Constants.STRING_ESCAPE, offset) && offset + 1 < text!!.length) {
                skip(Constants.STRING_ESCAPE.length + 1)
            } else {
                forward()
            }// other characters (string content)
            // skip any char after STRING_ESCAPE
            // end of string
        }

        val end = offset
        val content = text!!.substring(
                start + Constants.STRING_START.length,
                end - Constants.STRING_END.length)

        return Str(content, file, start, end, startLine, startCol)
    }


    @Throws(ParserException::class)
    fun scanNumber(): Node {
        val start = offset
        val startLine = line
        val startCol = col

        while (offset < text!!.length && isNumberChar(text!![offset])) {
            forward()
        }

        val content = text!!.substring(start, offset)

        val intNum = IntNum.parse(content, file, start, offset, startLine, startCol)
        if (intNum != null) {
            return intNum
        } else {
            val floatNum = FloatNum.parse(content, file, start, offset, startLine, startCol)
            return floatNum ?: throw ParserException("incorrect number format: $content", startLine, startCol, start)
        }
    }


    fun scanNameOrKeyword(): Node {
        val start = offset
        val startLine = line
        val startCol = col

        while (offset < text!!.length && isIdentifierChar(text!![offset])) {
            forward()
        }

        val content = text!!.substring(start, offset)
        return if (content.startsWith(":")) {
            Keyword(content.substring(1), file, start, offset, startLine, startCol)
        } else {
            Name(content, file, start, offset, startLine, startCol)
        }
    }


    /**
     * Lexer
     *
     * @return a token or null if file ends
     */
    @Throws(ParserException::class)
    fun nextToken(): Node? {

        skipSpacesAndComments()

        // end of file
        if (offset >= text!!.length) {
            return null
        }

        run {
            // case 1. delimiters
            val cur = text!![offset]
            if (Delimeter.isDelimiter(cur)) {
                val ret = Delimeter(Character.toString(cur), file, offset, offset + 1, line, col)
                forward()
                return ret
            }
        }

        // case 2. string
        if (text!!.startsWith(Constants.STRING_START, offset)) {
            return scanString()
        }

        // case 3. number
        if (Character.isDigit(text!![offset]) || ((text!![offset] == '+' || text!![offset] == '-')
                        && offset + 1 < text!!.length && Character.isDigit(text!![offset + 1]))) {
            return scanNumber()
        }

        // case 4. name or keyword
        if (isIdentifierChar(text!![offset])) {
            return scanNameOrKeyword()
        }

        // case 5. syntax error
        throw ParserException("unrecognized syntax: " + text!!.substring(offset, offset + 1),
                line, col, offset)
    }

    companion object {


        fun isNumberChar(c: Char): Boolean {
            return Character.isLetterOrDigit(c) || c == '.' || c == '+' || c == '-'
        }


        fun isIdentifierChar(c: Char): Boolean {
            return Character.isLetterOrDigit(c) || Constants.IDENT_CHARS.contains(c)
        }


        @Throws(ParserException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val lex = Lexer(args[0])

            val tokens = ArrayList<Node>()
            var n = lex.nextToken()
            while (n != null) {
                tokens.add(n)
                n = lex.nextToken()
            }
            Util.msg("lexer result: ")
            for (node in tokens) {
                Util.msg(node.toString())
            }
        }
    }
}
