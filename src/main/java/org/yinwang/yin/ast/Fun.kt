package org.yinwang.yin.ast


import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.TypeChecker
import org.yinwang.yin.value.Closure
import org.yinwang.yin.value.FunType
import org.yinwang.yin.value.Value

class Fun(var params: List<Name>, var propertyForm: Scope?, var body: Node, file: String, start: Int, end: Int, line: Int, col: Int)// unevaluated property form
    : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        // evaluate and cache the properties in the closure
        val properties = if (propertyForm == null) null else Declare.evalProperties(propertyForm!!, s)
        return Closure(this, properties, s)
    }


    override fun typecheck(s: Scope): Value {
        // evaluate and cache the properties in the closure
        val properties = if (propertyForm == null) null else Declare.typecheckProperties(propertyForm!!, s)
        val ft = FunType(this, properties, s)
        TypeChecker.self.uncalled.add(ft)
        return ft
    }


    override fun toString(): String {
        return "(" + Constants.FUN_KEYWORD + " (" + params + ") " + body + ")"
    }

}
