package org.yinwang.yin.ast

import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.RecordType
import org.yinwang.yin.value.Value

import java.util.LinkedHashMap


class RecordLiteral(contents: List<Node>, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {

    var map: MutableMap<String, Node> = LinkedHashMap()


    init {

        if (contents.size % 2 != 0) {
            Util.abort(this, "record initializer must have even number of elements")
        }

        var i = 0
        while (i < contents.size) {
            val key = contents[i]
            val value = contents[i + 1]
            if (key is Keyword) {
                if (value is Keyword) {
                    Util.abort(value, "keywords shouldn't be used as values: $value")
                } else {
                    map[key.id] = value
                }
            } else {
                Util.abort(key, "record initializer key is not a keyword: $key")
            }
            i += 2
        }
    }


    override fun interp(s: Scope): Value {
        val properties = Scope()
        for ((key, value) in map) {
            properties.putValue(key, value.interp(s))
        }
        return RecordType(null, this, properties)
    }


    override fun typecheck(s: Scope): Value {
        val properties = Scope()
        for ((key, value) in map) {
            properties.putValue(key, value.typecheck(s))
        }
        return RecordType(null, this, properties)
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.SQUARE_BEGIN)
        var first = true
        for ((key, value) in map) {
            if (!first) {
                sb.append(" ")
            }
            sb.append(":$key $value")
            first = false
        }
        sb.append(Constants.SQUARE_END)
        return sb.toString()
    }
}
