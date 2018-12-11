package org.yinwang.yin.ast

import org.yinwang.yin.Binder
import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

class Def(var pattern: Node, var value: Node, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        val valueValue = value.interp(s)
        Binder.checkDup(pattern)
        Binder.define(pattern, valueValue, s)
        return Value.VOID
    }


    override fun typecheck(s: Scope): Value {
        val t = value.typecheck(s)
        Binder.checkDup(pattern)
        Binder.define(pattern, t, s)
        return Value.VOID
    }


    override fun toString(): String {
        return "(" + Constants.DEF_KEYWORD + " " + pattern + " " + value + ")"
    }

}
