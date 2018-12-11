package org.yinwang.yin.value.primitives


import org.yinwang.yin.Util
import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.*

class Not : PrimFun("not", 1) {


    override fun apply(args: List<Value>, location: Node): Value? {

        val v1 = args[0]
        if (v1 is BoolValue) {
            return BoolValue(!v1.value)
        }
        Util.abort(location, "incorrect argument type for not: $v1")
        return null
    }


    override fun typecheck(args: List<Value>, location: Node): Value? {
        val v1 = args[0]
        if (v1 is BoolType) {
            return Type.BOOL
        }
        Util.abort(location, "incorrect argument type for not: $v1")
        return null
    }
}
