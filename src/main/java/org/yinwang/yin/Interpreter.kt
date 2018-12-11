package org.yinwang.yin


import org.yinwang.yin.ast.Node
import org.yinwang.yin.parser.Parser
import org.yinwang.yin.parser.ParserException
import org.yinwang.yin.value.Value

class Interpreter(internal var file: String) {


    fun interp(file: String): Value? {
        val program: Node?
        try {
            program = Parser.parse(file)
        } catch (e: ParserException) {
            Util.abort("parsing error: $e")
            return null
        }

        return program!!.interp(Scope.buildInitScope())
    }

    companion object {


        @JvmStatic
        fun main(args: Array<String>) {
            val i = Interpreter(args[0])
            Util.msg(i.interp(args[0])!!.toString())
        }
    }

}
