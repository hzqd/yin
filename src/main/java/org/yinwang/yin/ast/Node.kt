package org.yinwang.yin.ast

import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

import java.util.ArrayList

abstract class Node protected constructor(var file: String, var start: Int, var end: Int, var line: Int, var col: Int) {


    val fileLineCol: String
        get() = file + ":" + (line + 1) + ":" + (col + 1)


    abstract fun interp(s: Scope): Value


    abstract fun typecheck(s: Scope): Value

    companion object {


        fun interp(node: Node, s: Scope): Value {
            return node.interp(s)
        }


        fun typecheck(node: Node, s: Scope): Value {
            return node.typecheck(s)
        }


        fun interpList(nodes: List<Node>, s: Scope): List<Value> {
            val values = ArrayList<Value>()
            for (n in nodes) {
                values.add(n.interp(s))
            }
            return values
        }


        fun typecheckList(nodes: List<Node>, s: Scope): List<Value> {
            val types = ArrayList<Value>()
            for (n in nodes) {
                types.add(n.typecheck(s))
            }
            return types
        }


        fun printList(nodes: List<Node>): String {
            val sb = StringBuilder()
            var first = true
            for (e in nodes) {
                if (!first) {
                    sb.append(" ")
                }
                sb.append(e)
                first = false
            }
            return sb.toString()
        }
    }

}
