package org.yinwang.yin.value.primitives


import org.yinwang.yin.Util
import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.*

class Sub : PrimFun("-", 2) {


    override fun apply(args: List<Value>, location: Node): Value? {
        val v1 = args[0]
        val v2 = args[1]
        if (v1 is IntValue && v2 is IntValue) {
            return IntValue(v1.value - v2.value)
        }
        if (v1 is FloatValue && v2 is FloatValue) {
            return FloatValue(v1.value - v2.value)
        }
        if (v1 is FloatValue && v2 is IntValue) {
            return FloatValue(v1.value - v2.value)
        }
        if (v1 is IntValue && v2 is FloatValue) {
            return FloatValue(v1.value - v2.value)
        }

        Util.abort(location, "incorrect argument types for -: $v1, $v2")
        return null
    }


    override fun typecheck(args: List<Value>, location: Node): Value? {
        val v1 = args[0]
        val v2 = args[1]

        if (v1 is FloatType || v2 is FloatType) {
            return FloatType()
        }
        if (v1 is IntType && v2 is IntType) {
            return Type.INT
        }
        Util.abort(location, "incorrect argument types for -: $v1, $v2")
        return null
    }
}
