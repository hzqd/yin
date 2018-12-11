package org.yinwang.yin.value


class IntValue(var value: Int) : Value() {


    override fun toString(): String {
        return Integer.toString(value)
    }

}
