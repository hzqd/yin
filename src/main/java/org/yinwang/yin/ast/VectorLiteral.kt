package org.yinwang.yin.ast

import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value
import org.yinwang.yin.value.Vector

class VectorLiteral(var elements: List<Node>, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        return Vector(Node.interpList(elements, s))
    }


    override fun typecheck(s: Scope): Value {
        return Vector(Node.typecheckList(elements, s))
    }

}
