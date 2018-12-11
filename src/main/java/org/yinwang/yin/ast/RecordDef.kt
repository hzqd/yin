package org.yinwang.yin.ast

import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.RecordType
import org.yinwang.yin.value.Value


class RecordDef(var name: Name, var parents: List<Name>?, var propertyForm: Scope,
                file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {
    var properties: Scope? = null


    override fun interp(s: Scope): Value {
        val properties = Declare.evalProperties(propertyForm, s)

        if (parents != null) {
            for (p in parents!!) {
                val pv = p.interp(s)
                properties.putAll((pv as RecordType).properties)
            }
        }
        val r = RecordType(name.id, this, properties)
        s.putValue(name.id, r)
        return r
    }


    override fun typecheck(s: Scope): Value? {
        val properties = Declare.typecheckProperties(propertyForm, s)

        if (parents != null) {
            for (p in parents!!) {
                val pv = p.typecheck(s)
                if (pv !is RecordType) {
                    Util.abort(p, "parent is not a record: $pv")
                    return null
                }
                val parentProps = pv.properties

                // check for duplicated keys
                for (key in parentProps.keySet()) {
                    val existing = properties.lookupLocalType(key)
                    if (existing != null) {
                        Util.abort(p, "conflicting field " + key +
                                " inherited from parent " + p + ": " + pv)
                        return null
                    }
                }

                // add all properties or all fields in parent
                properties.putAll(parentProps)
            }
        }

        val r = RecordType(name.id, this, properties)
        s.putValue(name.id, r)
        return r
    }


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.PAREN_BEGIN)
        sb.append(Constants.RECORD_KEYWORD).append(" ")
        sb.append(name).append(" ")

        if (parents != null) {
            sb.append(" (" + Node.printList(parents!!) + ")")
        }

        for (field in propertyForm.keySet()) {
            sb.append("[$field")
            val props = propertyForm.lookupAllProps(field)
            for ((key, value) in props) {
                sb.append(" :$key $value")
            }
            sb.append("]")
        }

        sb.append(Constants.PAREN_END)
        return sb.toString()
    }
}
