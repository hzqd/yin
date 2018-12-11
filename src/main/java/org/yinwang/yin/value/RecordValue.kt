package org.yinwang.yin.value


import org.yinwang.yin.Constants
import org.yinwang.yin.Scope


class RecordValue(var name: String?, var type: RecordType, var properties: Scope) : Value() {


    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(Constants.PAREN_BEGIN)
        sb.append(Constants.RECORD_KEYWORD).append(" ")
        sb.append(if (name == null) "_" else name)

        for (field in properties.keySet()) {
            sb.append(" ").append(Constants.SQUARE_BEGIN)
            sb.append(field).append(" ")
            sb.append(properties.lookupLocal(field))
            sb.append(Constants.SQUARE_END)
        }

        sb.append(Constants.PAREN_END)
        return sb.toString()
    }

}
