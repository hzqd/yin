package org.yinwang.yin


import org.yinwang.yin.ast.Declare
import org.yinwang.yin.ast.Node
import org.yinwang.yin.parser.Parser
import org.yinwang.yin.parser.ParserException
import org.yinwang.yin.value.FunType
import org.yinwang.yin.value.Type
import org.yinwang.yin.value.Value

import java.util.ArrayList
import java.util.HashSet

class TypeChecker(var file: String) {
    var uncalled: MutableSet<FunType> = HashSet()
    var callStack: MutableSet<FunType> = HashSet()


    fun typecheck(file: String): Value? {
        val program: Node?
        try {
            program = Parser.parse(file)
        } catch (e: ParserException) {
            Util.abort("parsing error: $e")
            return null
        }

        val s = Scope.buildInitTypeScope()
        val ret = program!!.typecheck(s)

        while (!uncalled.isEmpty()) {
            val toRemove = ArrayList(uncalled)
            for (ft in toRemove) {
                invokeUncalled(ft, s)
            }
            uncalled.removeAll(toRemove)
        }

        return ret
    }


    fun invokeUncalled(`fun`: FunType, s: Scope) {
        val funScope = Scope(`fun`.env)
        if (`fun`.properties != null) {
            Declare.mergeType(`fun`.properties, funScope)
        }

        TypeChecker.self.callStack.add(`fun`)
        val actual = `fun`.`fun`.body.typecheck(funScope)
        TypeChecker.self.callStack.remove(`fun`)

        val retNode = `fun`.properties.lookupPropertyLocal(Constants.RETURN_ARROW, "type")

        if (retNode == null || retNode !is Node) {
            Util.abort("illegal return type: " + retNode!!)
            return
        }

        val expected = retNode.typecheck(funScope)
        if (!Type.subtype(actual, expected, true)) {
            Util.abort(`fun`.`fun`, "type error in return value, expected: $expected, actual: $actual")
        }
    }

    companion object {

        var self: TypeChecker


        @JvmStatic
        fun main(args: Array<String>) {
            val tc = TypeChecker(args[0])
            TypeChecker.self = tc
            val result = tc.typecheck(args[0])
            Util.msg(result!!.toString())
        }
    }

}
