package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.value.IntType
import org.yinwang.yin.value.IntValue
import org.yinwang.yin.value.Type
import org.yinwang.yin.value.Value

class IntNum(content: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {

    var content: String
    var value: Int = 0
    var base: Int = 0


    init {
        var content = content
        this.content = content

        val sign: Int
        if (content.startsWith("+")) {
            sign = 1
            content = content.substring(1)
        } else if (content.startsWith("-")) {
            sign = -1
            content = content.substring(1)
        } else {
            sign = 1
        }

        if (content.startsWith("0b")) {
            base = 2
            content = content.substring(2)
        } else if (content.startsWith("0x")) {
            base = 16
            content = content.substring(2)
        } else {
            base = 10
        }

        this.value = Integer.parseInt(content, base)
        if (sign == -1) {
            this.value = -this.value
        }
    }


    override fun interp(s: Scope): Value {
        return IntValue(value)
    }


    override fun typecheck(s: Scope): Value {
        return Type.INT
    }


    override fun toString(): String {
        return Integer.toString(value)
    }

    companion object {


        fun parse(content: String, file: String, start: Int, end: Int, line: Int, col: Int): IntNum? {
            try {
                return IntNum(content, file, start, end, line, col)
            } catch (e: NumberFormatException) {
                return null
            }

        }
    }

}
