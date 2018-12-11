package org.yinwang.yin.value.primitives


import org.yinwang.yin.ast.Node
import org.yinwang.yin.value.PrimFun
import org.yinwang.yin.value.UnionType
import org.yinwang.yin.value.Value

class U : PrimFun("U", -1) {


    override fun apply(args: List<Value>, location: Node): Value? {
        return null
    }


    override fun typecheck(args: List<Value>, location: Node): Value {
        return UnionType.union(args)
    }

}
