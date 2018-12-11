package org.yinwang.yin.value.primitives


import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.PrimFun
import org.yinwang.yin.value.Value

class Print : PrimFun("print", 1) {


    override fun apply(args: List<Value>, location: Node): Value {
        var first = true
        for (v in args) {
            if (!first) {
                print(", ")
            }
            print(v)
            first = false
        }
        println()
        return Value.VOID
    }


    override fun typecheck(args: List<Value>, location: Node): Value {
        return Value.VOID
    }
}
