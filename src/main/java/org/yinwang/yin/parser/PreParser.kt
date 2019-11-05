package org.yinwang.yin.parser

import org.yinwang.yin.Constants
import org.yinwang.yin.Util
import org.yinwang.yin.ast.Delimeter
import org.yinwang.yin.ast.Name
import org.yinwang.yin.ast.Node
import org.yinwang.yin.ast.Tuple

import java.util.ArrayList


/**
 * parse text into a meanlingless but more structured format
 * similar to S-expressions but with less syntax
 * (just matched (..) [..] and {..})
 */
class PreParser(file: String) {

    internal var file: String
    internal var lexer: Lexer


    init {
        this.file = Util.unifyPath(file)
        this.lexer = Lexer(file)
    }


    /**
     * Get next node from token stream
     */
    @Throws(ParserException::class)
    fun nextNode(): Node? {
        return nextNode1(0)
    }


    /**
     * Helper for nextNode, which does the real work
     *
     * @return a Node or null if file ends
     */
    @Throws(ParserException::class)
    fun nextNode1(depth: Int): Node? {
        val first = lexer.nextToken() ?: return null

        // end of file

        if (Delimeter.isOpen(first)) {   // try to get matched (...)
            val elements = ArrayList<Node>()
            var next: Node?
            next = nextNode1(depth + 1)
            while (!Delimeter.match(first, next)) {
                if (next == null) {
                    throw ParserException("unclosed delimeter till end of file: $first", first)
                } else if (Delimeter.isClose(next)) {
                    throw ParserException("unmatched closing delimeter: " +
                            next.toString() + " does not close " + first.toString(), next)
                } else {
                    elements.add(next)
                }
                next = nextNode1(depth + 1)
            }
            return Tuple(elements, first, next, first.file, first.start, next!!.end, first.line, first.col)
        } else return if (depth == 0 && Delimeter.isClose(first)) {
            throw ParserException("unmatched closing delimeter: " + first.toString() +
                    " does not close any open delimeter", first)
        } else {
            first
        }
    }


    /**
     * Parse file into a Node
     *
     * @return a Tuple containing the file's parse tree
     */
    @Throws(ParserException::class)
    fun parse(): Node {
        val elements = ArrayList<Node>()
        elements.add(Name.genName(Constants.SEQ_KEYWORD))      // synthetic block keyword

        var s = nextNode()
        val first = s
        var last: Node? = null
        while (s != null) {
            elements.add(s)
            last = s
            s = nextNode()
        }

        return Tuple(
                elements,
                Name.genName(Constants.PAREN_BEGIN),
                Name.genName(Constants.PAREN_END),
                file,
                first?.start ?: 0,
                last?.end ?: 0,
                0, 0
        )
    }

    companion object {


        @Throws(ParserException::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val p = PreParser(args[0])
            Util.msg("preparser result: " + p.parse())
        }
    }

}
