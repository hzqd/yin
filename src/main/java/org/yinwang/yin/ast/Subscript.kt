package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.IntValue
import org.yinwang.yin.value.Value
import org.yinwang.yin.value.Vector

class Subscript(var value: Node, var index: Node, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value? {
        val vector = value.interp(s)
        val indexValue = index.interp(s)

        if (vector !is Vector) {
            Util.abort(value, "subscripting non-vector: $vector")
            return null
        }

        if (indexValue !is IntValue) {
            Util.abort(value, "subscript $index is not an integer: $indexValue")
            return null
        }

        val values = vector.values
        val i = indexValue.value

        if (i >= 0 && i < values.size) {
            return values[i]
        } else {
            Util.abort(this, "subscript out of bound: " + i + " v.s. [0, " + (values.size - 1) + "]")
            return null
        }
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    operator fun set(v: Value, s: Scope) {
        val vector = value.interp(s)
        val indexValue = index.interp(s)

        if (vector !is Vector) {
            Util.abort(value, "subscripting non-vector: $vector")
        }

        if (indexValue !is IntValue) {
            Util.abort(value, "subscript $index is not an integer: $indexValue")
        }

        val vector1 = vector as Vector
        val i = (indexValue as IntValue).value

        if (i >= 0 && i < vector1.size()) {
            vector1.set(i, v)
        } else {
            Util.abort(this, "subscript out of bound: " + i + " v.s. [0, " + (vector1.size() - 1) + "]")
        }
    }


    override fun toString(): String {
        return "(ref $value $index)"
    }

}
