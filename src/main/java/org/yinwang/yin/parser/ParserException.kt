package org.yinwang.yin.parser


import org.yinwang.yin.ast.Node

class ParserException : Exception {
    var line: Int = 0
    var col: Int = 0
    var start: Int = 0


    constructor(message: String, line: Int, col: Int, start: Int) : super(message) {
        this.line = line
        this.col = col
        this.start = start
    }


    constructor(message: String, node: Node) : super(message) {
        this.line = node.line
        this.col = node.col
        this.start = node.start
    }


    override fun toString(): String {
        return (line + 1).toString() + ":" + (col + 1) + " parsing error " + message
    }
}
