package org.yinwang.yin


import org.yinwang.yin.ast.*
import org.yinwang.yin.value.RecordType
import org.yinwang.yin.value.Value
import org.yinwang.yin.value.Vector

import java.util.HashSet

object Binder {

    fun define(pattern: Node, value: Value?, env: Scope) {
        if (pattern is Name) {
            val id = pattern.id
            val v = env.lookupLocal(id)
            if (v != null) {
                Util.abort(pattern, "trying to redefine name: $id")
            } else {
                env.putValue(id, value)
            }
        } else if (pattern is RecordLiteral) {
            if (value is RecordType) {
                val elms1 = pattern.map
                val elms2 = value.properties
                if (elms1.keys == elms2.keySet()) {
                    for (k1 in elms1.keys) {
                        define(elms1[k1], elms2.lookupLocal(k1), env)
                    }
                } else {
                    Util.abort(pattern, "define with records of different attributes: " +
                            elms1.keys + " v.s. " + elms2.keySet())
                }
            } else {
                Util.abort(pattern, "define with incompatible types: record and " + value!!)
            }
        } else if (pattern is VectorLiteral) {
            if (value is Vector) {
                val elms1 = pattern.elements
                val elms2 = value.values
                if (elms1.size == elms2.size) {
                    for (i in elms1.indices) {
                        define(elms1[i], elms2[i], env)
                    }
                } else {
                    Util.abort(pattern,
                            "define with vectors of different sizes: " + elms1.size + " v.s. " + elms2.size)
                }
            } else {
                Util.abort(pattern, "define with incompatible types: vector and " + value!!)
            }
        } else {
            Util.abort(pattern, "unsupported pattern of define: $pattern")
        }
    }


    fun assign(pattern: Node, value: Value?, env: Scope) {
        if (pattern is Name) {
            val id = pattern.id
            val d = env.findDefiningScope(id)

            if (d == null) {
                Util.abort(pattern, "assigned name was not defined: $id")
            } else {
                d.putValue(id, value)
            }
        } else if (pattern is Subscript) {
            pattern.set(value, env)
        } else if (pattern is Attr) {
            pattern.set(value, env)
        } else if (pattern is RecordLiteral) {
            if (value is RecordType) {
                val elms1 = pattern.map
                val elms2 = value.properties
                if (elms1.keys == elms2.keySet()) {
                    for (k1 in elms1.keys) {
                        assign(elms1[k1], elms2.lookupLocal(k1), env)
                    }
                } else {
                    Util.abort(pattern, "assign with records of different attributes: " +
                            elms1.keys + " v.s. " + elms2.keySet())
                }
            } else {
                Util.abort(pattern, "assign with incompatible types: record and " + value!!)
            }
        } else if (pattern is VectorLiteral) {
            if (value is Vector) {
                val elms1 = pattern.elements
                val elms2 = value.values
                if (elms1.size == elms2.size) {
                    for (i in elms1.indices) {
                        assign(elms1[i], elms2[i], env)
                    }
                } else {
                    Util.abort(pattern, "assign vectors of different sizes: " + elms1.size + " v.s. " + elms2.size)
                }
            } else {
                Util.abort(pattern, "assign incompatible types: vector and " + value!!)
            }
        } else {
            Util.abort(pattern, "unsupported pattern of assign: $pattern")
        }
    }


    fun checkDup(pattern: Node) {
        checkDup1(pattern, HashSet())
    }


    fun checkDup1(pattern: Node, seen: MutableSet<String>) {

        if (pattern is Name) {
            val id = pattern.id
            if (seen.contains(id)) {
                Util.abort(pattern, "duplicated name found in pattern: $pattern")
            } else {
                seen.add(id)
            }
        } else if (pattern is RecordLiteral) {
            for (v in pattern.map.values) {
                checkDup1(v, seen)
            }
        } else if (pattern is VectorLiteral) {
            for (v in pattern.elements) {
                checkDup1(v, seen)
            }
        }
    }

}
