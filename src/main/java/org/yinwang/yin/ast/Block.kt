package org.yinwang.yin.ast

import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

import java.util.ArrayList


class Block(statements: List<Node>, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {
    var statements: List<Node> = ArrayList()


    init {
        this.statements = statements
    }


    override fun interp(s: Scope): Value {
        var s = s
        s = Scope(s)
        for (i in 0 until statements.size - 1) {
            statements[i].interp(s)
        }
        return statements[statements.size - 1].interp(s)
    }


    override fun typecheck(s: Scope): Value {
        var s = s
        s = Scope(s)
        for (i in 0 until statements.size - 1) {
            statements[i].typecheck(s)
        }
        return statements[statements.size - 1].typecheck(s)
    }


    override fun toString(): String {
        val sb = StringBuilder()
        val sep = if (statements.size > 5) "\n" else " "
        sb.append("(seq$sep")

        for (i in statements.indices) {
            sb.append(statements[i].toString())
            if (i != statements.size - 1) {
                sb.append(sep)
            }
        }

        sb.append(")")
        return sb.toString()
    }
}
