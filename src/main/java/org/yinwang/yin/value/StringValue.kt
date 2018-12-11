package org.yinwang.yin.value


class StringValue(var value: String) : Value() {


    override fun toString(): String {
        return "\"" + value + "\""
    }

}
