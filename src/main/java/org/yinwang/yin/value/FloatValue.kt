package org.yinwang.yin.value


class FloatValue(var value: Double) : Value() {


    override fun toString(): String {
        return java.lang.Double.toString(value)
    }

}
