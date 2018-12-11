package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

import java.math.BigInteger

class BigInt(content: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {

    var content: String
    var value: BigInteger
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
        } else if (content.startsWith("0o")) {
            base = 8
            content = content.substring(2)
        } else {
            base = 10
        }

        var value1 = BigInteger(content, base)
        if (sign == -1) {
            value1 = value1.negate()
        }
        this.value = value1
    }


    override fun interp(s: Scope): Value? {
        return null
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    override fun toString(): String {
        return content
    }

    companion object {


        fun parse(content: String, file: String, start: Int, end: Int, line: Int, col: Int): BigInt? {
            try {
                return BigInt(content, file, start, end, line, col)
            } catch (e: NumberFormatException) {
                return null
            }

        }
    }

}
