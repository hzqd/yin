package org.yinwang.yin.value

object Type {


    val BOOL: Value = BoolType()
    val INT: Value = IntType()
    val STRING: Value = StringType()

    fun subtype(type1: Value, type2: Value, ret: Boolean): Boolean {
        if (!ret && type2 is AnyType) {
            return true
        }

        if (type1 is UnionType) {
            for (t in type1.values) {
                if (!subtype(t, type2, false)) {
                    return false
                }
            }
            return true
        } else return if (type2 is UnionType) {
            type2.values.contains(type1)
        } else {
            type1 == type2
        }
    }

}
