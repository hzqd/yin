package org.yinwang.yin.ast


import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.TypeChecker
import org.yinwang.yin.Util
import org.yinwang.yin.value.*

import java.util.*

class Call(var op: Node, var args: Argument, file: String, start: Int, end: Int, line: Int, col: Int) : Node(file, start, end, line, col) {


    override fun interp(s: Scope): Value {
        val opv = this.op.interp(s)
        if (opv is Closure) {
            val funScope = Scope(opv.env)
            val params = opv.`fun`.params

            // set default values for parameters
            if (opv.properties != null) {
                Declare.mergeDefault(opv.properties, funScope)
            }

            if (!args.positional.isEmpty() && args.keywords.isEmpty()) {
                for (i in args.positional.indices) {
                    val value = args.positional[i].interp(s)
                    funScope.putValue(params[i].id, value)
                }
            } else {
                // try to bind all arguments
                for (param in params) {
                    val actual = args.keywords[param.id]
                    if (actual != null) {
                        val value = actual.interp(funScope)
                        funScope.putValue(param.id, value)
                    }
                }
            }
            return opv.`fun`.body.interp(funScope)
        } else if (opv is RecordType) {
            val values = Scope()

            // set default values for fields
            Declare.mergeDefault(opv.properties, values)

            // instantiate
            return RecordValue(opv.name, opv, values)
        } else if (opv is PrimFun) {
            val args = Node.interpList(this.args.positional, s)
            return opv.apply(args, this)
        } else {  // can't happen
            Util.abort(this.op, "calling non-function: $opv")
            return Value.VOID
        }
    }


    override fun typecheck(s: Scope): Value? {
        val `fun` = this.op.typecheck(s)
        if (`fun` is FunType) {
//            TypeChecker.self.uncalled.remove(funtype);

            val funScope = Scope(`fun`.env)
            val params = `fun`.`fun`.params

            // set default values for parameters
            if (`fun`.properties != null) {
                Declare.mergeType(`fun`.properties, funScope)
            }

            if (!args.positional.isEmpty() && args.keywords.isEmpty()) {
                // positional
                if (args.positional.size != params.size) {
                    Util.abort(this.op,
                            "calling function with wrong number of arguments. expected: " + params.size
                                    + " actual: " + args.positional.size)
                }

                for (i in args.positional.indices) {
                    val value = args.positional[i].typecheck(s)
                    val expected = funScope.lookup(params[i].id)
                    if (!Type.subtype(value, expected, false)) {
                        Util.abort(args.positional[i], "type error. expected: $expected, actual: $value")
                    }
                    funScope.putValue(params[i].id, value)
                }
            } else {
                // keywords
                val seen = HashSet<String>()

                // try to bind all arguments
                for (param in params) {
                    val actual = args.keywords[param.id]
                    if (actual != null) {
                        seen.add(param.id)
                        val value = actual.typecheck(funScope)
                        val expected = funScope.lookup(param.id)
                        if (!Type.subtype(value, expected, false)) {
                            Util.abort(actual, "type error. expected: $expected, actual: $value")
                        }
                        funScope.putValue(param.id, value)
                    } else {
                        Util.abort(this, "argument not supplied for: $param")
                        return Value.VOID
                    }
                }

                // detect extra arguments
                val extra = ArrayList<String>()
                for (id in args.keywords.keys) {
                    if (!seen.contains(id)) {
                        extra.add(id)
                    }
                }

                if (!extra.isEmpty()) {
                    Util.abort(this, "extra keyword arguments: $extra")
                    return Value.VOID
                }
            }

            val retType = `fun`.properties.lookupPropertyLocal(Constants.RETURN_ARROW, "type")
            if (retType != null) {
                if (retType is Node) {
                    // evaluate the return type because it might be (typeof x)
                    return retType.typecheck(funScope)
                } else {
                    Util.abort("illegal return type: $retType")
                    return null
                }
            } else {
                if (TypeChecker.self.callStack.contains(`fun`)) {
                    Util.abort(op, "You must specify return type for recursive functions: $op")
                    return null
                }

                TypeChecker.self.callStack.add(`fun`)
                val actual = `fun`.`fun`.body.typecheck(funScope)
                TypeChecker.self.callStack.remove(`fun`)
                return actual
            }
        } else if (`fun` is RecordType) {
            val values = Scope()

            // set default values for fields
            Declare.mergeDefault(`fun`.properties, values)

            // set actual values, overwrite defaults if any
            for ((key, value) in args.keywords) {
                if (!`fun`.properties.keySet().contains(key)) {
                    Util.abort(this, "extra keyword argument: $key")
                }

                val actual = args.keywords[key].typecheck(s)
                val expected = `fun`.properties.lookupLocalType(key)
                if (!Type.subtype(actual, expected, false)) {
                    Util.abort(this, "type error. expected: $expected, actual: $actual")
                }
                values.putValue(key, value.typecheck(s))
            }

            // check uninitialized fields
            for (field in `fun`.properties.keySet()) {
                if (values.lookupLocal(field) == null) {
                    Util.abort(this, "field is not initialized: $field")
                }
            }

            // instantiate
            return RecordValue(`fun`.name, `fun`, values)
        } else if (`fun` is PrimFun) {
            if (`fun`.arity >= 0 && args.positional.size != `fun`.arity) {
                Util.abort(this, "incorrect number of arguments for primitive " +
                        `fun`.name + ", expecting " + `fun`.arity + ", but got " + args.positional.size)
                return null
            } else {
                val args = Node.typecheckList(this.args.positional, s)
                return `fun`.typecheck(args, this)
            }
        } else {
            Util.abort(this.op, "calling non-function: $`fun`")
            return Value.VOID
        }

    }


    override fun toString(): String {
        return if (args.positional.size != 0) {
            "($op $args)"
        } else {
            "($op)"
        }
    }

}
