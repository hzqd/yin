package org.yinwang.yin.ast


import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.value.RecordType
import org.yinwang.yin.value.RecordValue
import org.yinwang.yin.value.Value

class Attr(var value: Node, var attr: Name, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value? {
        val record = value.interp(s)
        if (record is RecordValue) {
            val a = record.properties.lookupLocal(attr.id)
            if (a != null) {
                return a
            } else {
                Util.abort(attr, "attribute $attr not found in record: $record")
                return null
            }
        } else {
            Util.abort(attr, "getting attribute of non-record: $record")
            return null
        }
    }


    override fun typecheck(s: Scope): Value? {
        val record = value.typecheck(s)
        if (record is RecordValue) {
            val a = record.properties.lookupLocal(attr.id)
            if (a != null) {
                return a
            } else {
                Util.abort(attr, "attribute $attr not found in record: $record")
                return null
            }
        } else {
            Util.abort(attr, "getting attribute of non-record: $record")
            return null
        }
    }


    operator fun set(v: Value, s: Scope) {
        val record = value.interp(s)
        if (record is RecordType) {
            val a = record.properties.lookup(attr.id)
            if (a != null) {
                record.properties.putValue(attr.id, v)
            } else {
                Util.abort(attr,
                        "can only assign to existing attribute in record, $attr not found in: $record")
            }
        } else {
            Util.abort(attr, "setting attribute of non-record: $record")
        }
    }


    override fun toString(): String {
        return "$value.$attr"
    }

}
