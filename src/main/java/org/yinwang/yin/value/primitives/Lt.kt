package org.yinwang.yin.value.primitives


import org.yinwang.yin.Util
import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.*

class Lt : PrimFun("<", 2) {


    override fun apply(args: List<Value>, location: Node): Value? {

        val v1 = args[0]
        val v2 = args[1]
        if (v1 is IntValue && v2 is IntValue) {
            return BoolValue(v1.value < v2.value)
        }
        if (v1 is FloatValue && v2 is FloatValue) {
            return BoolValue(v1.value < v2.value)
        }
        if (v1 is FloatValue && v2 is IntValue) {
            return BoolValue(v1.value < v2.value)
        }
        if (v1 is IntValue && v2 is FloatValue) {
            return BoolValue(v1.value < v2.value)
        }

        Util.abort(location, "incorrect argument types for <: $v1, $v2")
        return null
    }


    override fun typecheck(args: List<Value>, location: Node): Value {
        val v1 = args[0]
        val v2 = args[1]

        if (!(v1 is IntType || v1 is FloatValue) || !(v2 is IntType || v2 is FloatValue)) {
            Util.abort(location, "incorrect argument types for <: $v1, $v2")
        }

        return Type.BOOL
    }

}
