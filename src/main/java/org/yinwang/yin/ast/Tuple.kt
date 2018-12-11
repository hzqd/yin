package org.yinwang.yin.ast

import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

import java.util.ArrayList


class Tuple(elements: List<Node>, var open: Node?, var close: Node?, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {
    var elements: List<Node> = ArrayList()


    val head: Node?
        get() = if (elements.isEmpty()) {
            null
        } else {
            elements[0]
        }


    init {
        this.elements = elements
    }


    override fun interp(s: Scope): Value? {
        return null
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    override fun toString(): String {
        val sb = StringBuilder()

        for (i in elements.indices) {
            sb.append(elements[i].toString())
            if (i != elements.size - 1) {
                sb.append(" ")
            }
        }

        return (if (open == null) "" else open).toString() + sb.toString() + if (close == null) "" else close
    }
}
