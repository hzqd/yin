package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.value.StringType
import org.yinwang.yin.value.StringValue
import org.yinwang.yin.value.Type
import org.yinwang.yin.value.Value

class Str(var value: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        return StringValue(value)
    }


    override fun typecheck(s: Scope): Value {
        return Type.STRING
    }


    override fun toString(): String {
        return "\"" + value + "\""
    }

}
