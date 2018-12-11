package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.value.Value

import java.util.HashMap
import java.util.HashSet

class Delimeter(var shape: String, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value? {
        return null
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    override fun toString(): String {
        return shape
    }

    companion object {


        // all delimeters
        val delims: MutableSet<String> = HashSet()

        // map open delimeters to their matched closing ones
        val delimMap: MutableMap<String, String> = HashMap()


        fun addDelimiterPair(open: String, close: String) {
            delims.add(open)
            delims.add(close)
            delimMap[open] = close
        }


        fun addDelimiter(delim: String) {
            delims.add(delim)
        }


        fun isDelimiter(c: Char): Boolean {
            return delims.contains(Character.toString(c))
        }


        fun isOpen(c: Node): Boolean {
            return c is Delimeter && delimMap.keys.contains(c.shape)
        }


        fun isClose(c: Node): Boolean {
            return c is Delimeter && delimMap.values.contains(c.shape)
        }


        fun match(open: Node, close: Node): Boolean {
            if (open !is Delimeter || close !is Delimeter) {
                return false
            }
            val matched = delimMap[open.shape]
            return matched != null && matched == close.shape
        }
    }
}
