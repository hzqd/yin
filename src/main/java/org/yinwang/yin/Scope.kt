package org.yinwang.yin


import org.yinwang.yin.value.BoolValue
import org.yinwang.yin.value.Type
import org.yinwang.yin.value.Value
import org.yinwang.yin.value.primitives.*

import java.util.LinkedHashMap

class Scope {

    var table: MutableMap<String, Map<String, Any>> = LinkedHashMap()
    var parent: Scope? = null


    constructor() {
        this.parent = null
    }


    constructor(parent: Scope) {
        this.parent = parent
    }


    fun copy(): Scope {
        val ret = Scope()
        for (name in table.keys) {
            val props = LinkedHashMap<String, Any>()
            props.putAll(table[name])
            ret.table[name] = props
        }
        return ret
    }


    fun putAll(other: Scope) {
        for (name in other.table.keys) {
            val props = LinkedHashMap<String, Any>()
            props.putAll(other.table[name])
            table[name] = props
        }
    }


    fun lookup(name: String): Value? {
        val v = lookupProperty(name, "value")
        if (v == null) {
            return null
        } else if (v is Value) {
            return v
        } else {
            Util.abort("value is not a Value, shouldn't happen: $v")
            return null
        }
    }


    fun lookupLocal(name: String): Value? {
        val v = lookupPropertyLocal(name, "value")
        if (v == null) {
            return null
        } else if (v is Value) {
            return v
        } else {
            Util.abort("value is not a Value, shouldn't happen: $v")
            return null
        }
    }


    fun lookupType(name: String): Value? {
        val v = lookupProperty(name, "type")
        if (v == null) {
            return null
        } else if (v is Value) {
            return v
        } else {
            Util.abort("value is not a Value, shouldn't happen: $v")
            return null
        }
    }


    fun lookupLocalType(name: String): Value? {
        val v = lookupPropertyLocal(name, "type")
        if (v == null) {
            return null
        } else if (v is Value) {
            return v
        } else {
            Util.abort("value is not a Value, shouldn't happen: $v")
            return null
        }
    }


    fun lookupPropertyLocal(name: String, key: String): Any? {
        val item = table[name]
        return if (item != null) {
            item[key]
        } else {
            null
        }
    }


    fun lookupProperty(name: String, key: String): Any? {
        val v = lookupPropertyLocal(name, key)
        return v ?: if (parent != null) {
            parent!!.lookupProperty(name, key)
        } else {
            null
        }
    }


    fun lookupAllProps(name: String): Map<String, Any> {
        return table[name]
    }


    fun findDefiningScope(name: String): Scope? {
        val v = table[name]
        return if (v != null) {
            this
        } else if (parent != null) {
            parent!!.findDefiningScope(name)
        } else {
            null
        }
    }


    fun put(name: String, key: String, value: Any) {
        var item: MutableMap<String, Any>? = table[name]
        if (item == null) {
            item = LinkedHashMap()
        }
        item[key] = value
        table[name] = item
    }


    fun putProperties(name: String, props: Map<String, Any>) {
        var item: MutableMap<String, Any>? = table[name]
        if (item == null) {
            item = LinkedHashMap()
        }
        item.putAll(props)
        table[name] = item
    }


    fun putValue(name: String, value: Value) {
        put(name, "value", value)
    }


    fun putType(name: String, value: Value) {
        put(name, "type", value)
    }


    fun keySet(): Set<String> {
        return table.keys
    }


    fun containsKey(key: String): Boolean {
        return table.containsKey(key)
    }


    override fun toString(): String {
        val sb = StringBuffer()
        for (name in table.keys) {
            sb.append(Constants.SQUARE_BEGIN).append(name).append(" ")
            for ((key, value) in table[name]) {
                sb.append(":$key $value")
            }
            sb.append(Constants.SQUARE_END)
        }
        return sb.toString()
    }

    companion object {


        fun buildInitScope(): Scope {
            val init = Scope()

            init.putValue("+", Add())
            init.putValue("-", Sub())
            init.putValue("*", Mult())
            init.putValue("/", Div())

            init.putValue("<", Lt())
            init.putValue("<=", LtE())
            init.putValue(">", Gt())
            init.putValue(">=", GtE())
            init.putValue("=", Eq())
            init.putValue("and", And())
            init.putValue("or", Or())
            init.putValue("not", Not())

            init.putValue("print", Print())

            init.putValue("true", BoolValue(true))
            init.putValue("false", BoolValue(false))

            init.putValue("Int", Type.INT)
            init.putValue("Bool", Type.BOOL)
            init.putValue("String", Type.STRING)

            return init
        }


        fun buildInitTypeScope(): Scope {
            val init = Scope()

            init.putValue("+", Add())
            init.putValue("-", Sub())
            init.putValue("*", Mult())
            init.putValue("/", Div())

            init.putValue("<", Lt())
            init.putValue("<=", LtE())
            init.putValue(">", Gt())
            init.putValue(">=", GtE())
            init.putValue("=", Eq())
            init.putValue("and", And())
            init.putValue("or", Or())
            init.putValue("not", Not())
            init.putValue("U", U())

            init.putValue("true", Type.BOOL)
            init.putValue("false", Type.BOOL)

            init.putValue("Int", Type.INT)
            init.putValue("Bool", Type.BOOL)
            init.putValue("String", Type.STRING)
            init.putValue("Any", Value.ANY)

            return init
        }
    }

}
