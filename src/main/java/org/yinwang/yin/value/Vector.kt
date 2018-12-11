package org.yinwang.yin.value


import org.yinwang.yin.Constants

class Vector(var values: MutableList<Value>) : Value() {


    operator fun set(idx: Int, value: Value) {
        values[idx] = value
    }


    fun size(): Int {
        return values.size
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.SQUARE_BEGIN)

        var first = true
        for (v in values) {
            if (!first) {
                sb.append(" ")
            }
            sb.append(v)
            first = false
        }

        sb.append(Constants.SQUARE_END)
        return sb.toString()
    }

}
