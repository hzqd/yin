package org.yinwang.yin.ast

import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.BoolType
import org.yinwang.yin.value.BoolValue
import org.yinwang.yin.value.UnionType
import org.yinwang.yin.value.Value

class If(var test: Node, var then: Node, var orelse: Node, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        val tv = Node.interp(test, s)
        return if ((tv as BoolValue).value) {
            Node.interp(then, s)
        } else {
            Node.interp(orelse, s)
        }
    }


    override fun typecheck(s: Scope): Value? {
        val tv = Node.typecheck(test, s)
        if (tv !is BoolType) {
            Util.abort(test, "test is not boolean: $tv")
            return null
        }
        val type1 = Node.typecheck(then, s)
        val type2 = Node.typecheck(orelse, s)
        return UnionType.union(type1, type2)
    }


    override fun toString(): String {
        return "(" + Constants.IF_KEYWORD + " " + test + " " + then + " " + orelse + ")"
    }

}
