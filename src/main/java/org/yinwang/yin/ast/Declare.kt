package org.yinwang.yin.ast

import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.Value


class Declare(var propertyForm: Scope, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        //        mergeProperties(propsNode, s);
        return Value.VOID
    }


    override fun typecheck(s: Scope): Value? {
        return null
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.PAREN_BEGIN)
        sb.append(Constants.DECLARE_KEYWORD).append(" ")

        for (field in propertyForm.keySet()) {
            val props = propertyForm.lookupAllProps(field)
            for ((key, value) in props) {
                sb.append(" :$key $value")
            }
        }

        sb.append(Constants.PAREN_END)
        return sb.toString()
    }

    companion object {


        fun mergeDefault(properties: Scope, s: Scope) {
            for (key in properties.keySet()) {
                val defaultValue = properties.lookupPropertyLocal(key, "default")
                if (defaultValue == null) {
                    continue
                } else if (defaultValue is Value) {
                    val existing = s.lookup(key)
                    if (existing == null) {
                        s.putValue(key, defaultValue)
                    }
                } else {
                    Util.abort("default value is not a value, shouldn't happen")
                }
            }
        }


        fun mergeType(properties: Scope, s: Scope) {
            for (key in properties.keySet()) {
                if (key == Constants.RETURN_ARROW) {
                    continue
                }
                val type = properties.lookupPropertyLocal(key, "type")
                if (type == null) {
                    continue
                } else if (type is Value) {
                    val existing = s.lookup(key)
                    if (existing == null) {
                        s.putValue(key, type)
                    }
                } else {
                    Util.abort("illegal type, shouldn't happen$type")
                }
            }
        }


        fun evalProperties(unevaled: Scope, s: Scope): Scope {
            val evaled = Scope()

            for (field in unevaled.keySet()) {
                val props = unevaled.lookupAllProps(field)
                for ((key, v) in props) {
                    if (v is Node) {
                        val vValue = v.interp(s)
                        evaled.put(field, key, vValue)
                    } else {
                        Util.abort("property is not a node, parser bug: $v")
                    }
                }
            }
            return evaled
        }


        fun typecheckProperties(unevaled: Scope, s: Scope): Scope {
            val evaled = Scope()

            for (field in unevaled.keySet()) {
                if (field == Constants.RETURN_ARROW) {
                    evaled.putProperties(field, unevaled.lookupAllProps(field))
                } else {
                    val props = unevaled.lookupAllProps(field)
                    for ((key, v) in props) {
                        if (v is Node) {
                            val vValue = v.typecheck(s)
                            evaled.put(field, key, vValue)
                        } else {
                            Util.abort("property is not a node, parser bug: $v")
                        }
                    }
                }
            }
            return evaled
        }
    }
}
