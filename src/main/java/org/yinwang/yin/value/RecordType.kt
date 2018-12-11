package org.yinwang.yin.value


import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.ast.Node


class RecordType(var name: String?, var definition: Node, properties: Scope) : Value() {
    var properties: Scope


    init {
        this.properties = properties.copy()
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.PAREN_BEGIN)
        sb.append(Constants.RECORD_KEYWORD).append(" ")
        sb.append(if (name == null) "_" else name)

        for (field in properties.keySet()) {
            sb.append(" ").append(Constants.SQUARE_BEGIN)
            sb.append(field)

            val m = properties.lookupAllProps(field)
            for (key in m.keys) {
                val value = m[key]
                if (value != null) {
                    sb.append(" :$key $value")
                }
            }
            sb.append(Constants.SQUARE_END)
        }

        sb.append(Constants.PAREN_END)
        return sb.toString()
    }

}
