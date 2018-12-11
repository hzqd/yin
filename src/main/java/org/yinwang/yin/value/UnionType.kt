package org.yinwang.yin.value


import org.yinwang.yin.Constants
import java.util.HashSet


class UnionType : Value() {

    var values: MutableSet<Value> = HashSet()


    fun add(value: Value) {
        if (value is UnionType) {
            values.addAll(value.values)
        } else {
            values.add(value)
        }
    }


    fun size(): Int {
        return values.size
    }


    fun first(): Value {
        return values.iterator().next()
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.PAREN_BEGIN).append("U ")

        var first = true
        for (v in values) {
            if (!first) {
                sb.append(" ")
            }
            sb.append(v)
            first = false
        }

        sb.append(Constants.PAREN_END)
        return sb.toString()
    }

    companion object {


        fun union(values: Collection<Value>): Value {
            val u = UnionType()
            for (v in values) {
                u.add(v)
            }
            return if (u.size() == 1) {
                u.first()
            } else {
                u
            }
        }


        fun union(vararg values: Value): Value {
            val u = UnionType()
            for (v in values) {
                u.add(v)
            }
            return if (u.size() == 1) {
                u.first()
            } else {
                u
            }
        }
    }

}
