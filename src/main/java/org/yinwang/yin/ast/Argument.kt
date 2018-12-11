package org.yinwang.yin.ast


import org.yinwang.yin.Util

import java.util.ArrayList
import java.util.LinkedHashMap

class Argument(var elements: List<Node>) {
    var positional: MutableList<Node> = ArrayList()
    var keywords: MutableMap<String, Node> = LinkedHashMap()


    init {
        var hasName = false
        var hasKeyword = false

        run {
            var i = 0
            while (i < elements.size) {
                if (elements[i] is Keyword) {
                    hasKeyword = true
                    i++
                } else {
                    hasName = true
                }
                i++
            }
        }

        if (hasName && hasKeyword) {
            Util.abort(elements[0], "mix positional and keyword arguments not allowed: $elements")
        }

        var i = 0
        while (i < elements.size) {
            val key = elements[i]
            if (key is Keyword) {
                val id = key.id
                positional.add(key.asName())

                if (i >= elements.size - 1) {
                    Util.abort(key, "missing value for keyword: $key")
                } else {
                    val value = elements[i + 1]
                    if (value is Keyword) {
                        Util.abort(value, "keywords can't be used as values: $value")
                    } else {
                        if (keywords.containsKey(id)) {
                            Util.abort(key, "duplicated keyword: $key")
                        }
                        keywords[id] = value
                        i++
                    }
                }
            } else {
                positional.add(key)
            }
            i++
        }
    }


    override fun toString(): String {
        val sb = StringBuilder()
        var first = true
        for (e in elements) {
            if (!first) {
                sb.append(" ")
            }
            sb.append(e)
            first = false
        }
        return sb.toString()
    }

}
