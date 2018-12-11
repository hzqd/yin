package org.yinwang.yin.value


class BoolValue(var value: Boolean) : Value() {

    override fun toString(): String {
        return if (value) "true" else "false"
    }

}
