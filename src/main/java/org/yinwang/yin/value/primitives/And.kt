package org.yinwang.yin.value.primitives


import org.yinwang.yin.Util
import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.*

class And : PrimFun("and", 2) {


    override fun apply(args: List<Value>, location: Node): Value? {

        val v1 = args[0]
        val v2 = args[1]
        if (v1 is BoolValue && v2 is BoolValue) {
            return BoolValue(v1.value && v2.value)
        }

        Util.abort(location, "incorrect argument types for and: $v1, $v2")
        return null
    }


    override fun typecheck(args: List<Value>, location: Node): Value? {
        val v1 = args[0]
        val v2 = args[1]

        if (v1 is BoolType && v2 is BoolType) {
            return Type.BOOL
        }
        Util.abort(location, "incorrect argument types for and: $v1, $v2")
        return null
    }
}
