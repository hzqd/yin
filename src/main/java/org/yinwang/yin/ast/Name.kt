package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.Value

class Name(var id: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value? {
        return s.lookup(id)
    }


    override fun typecheck(s: Scope): Value {
        val v = s.lookup(id)
        if (v != null) {
            return v
        } else {
            Util.abort(this, "unbound variable: $id")
            return Value.VOID
        }
    }


    override fun toString(): String {
        return id
    }

    companion object {


        /**
         * Generate a name without location info
         */
        fun genName(id: String): Name {
            return Name(id, null, 0, 0, 0, 0)
        }
    }
}
