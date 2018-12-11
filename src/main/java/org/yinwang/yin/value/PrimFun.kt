package org.yinwang.yin.value


import org.yinwang.yin.ast.Node

abstract class PrimFun protected constructor(var name: String, var arity: Int) : Value() {


    // how to apply the primitive to args (must be positional)
    abstract fun apply(args: List<Value>, location: Node): Value


    abstract fun typecheck(args: List<Value>, location: Node): Value


    override fun toString(): String {
        return name
    }

}
