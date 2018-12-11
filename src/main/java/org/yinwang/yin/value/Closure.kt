package org.yinwang.yin.value


import org.yinwang.yin.Scope
import org.yinwang.yin.ast.Fun

class Closure(var `fun`: Fun, var properties: Scope, var env: Scope) : Value() {


    override fun toString(): String {
        return `fun`.toString()
    }

}
