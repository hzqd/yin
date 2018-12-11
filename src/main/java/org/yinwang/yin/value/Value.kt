package org.yinwang.yin.value


abstract class Value {
    companion object {
        val VOID: Value = VoidValue()
        val TRUE: Value = BoolValue(true)
        val FALSE: Value = BoolValue(false)
        val ANY: Value = AnyType()
    }

}
