package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.Value

class Keyword(var id: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    fun asName(): Name {
        return Name(id, file, start, end, line, col)
    }


    override fun interp(s: Scope): Value? {
        Util.abort(this, "keyword used as value")
        return null
    }


    override fun typecheck(s: Scope): Value? {
        Util.abort(this, "keyword used as value")
        return null
    }


    override fun toString(): String {
        return ":$id"
    }
}
