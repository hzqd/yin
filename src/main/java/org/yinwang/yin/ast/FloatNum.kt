package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.value.FloatValue
import org.yinwang.yin.value.Value

class FloatNum(var content: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {
    var value: Double = 0.toDouble()


    init {
        this.value = java.lang.Double.parseDouble(content)

    }


    override fun interp(s: Scope): Value {
        return FloatValue(value)
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    override fun toString(): String {
        return java.lang.Double.toString(value)
    }

    companion object {


        fun parse(content: String, file: String, start: Int, end: Int, line: Int, col: Int): FloatNum? {
            try {
                return FloatNum(content, file, start, end, line, col)
            } catch (e: NumberFormatException) {
                return null
            }

        }
    }

}
