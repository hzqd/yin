package org.yinwang.yin.parser

import org.yinwang.yin.Constants
import org.yinwang.yin.Scope
import org.yinwang.yin.Util
import org.yinwang.yin.ast.*

import java.util.ArrayList
import java.util.LinkedHashMap


/**
 * Parser
 * parse S-expression-like structure into more structured data
 * with Classes, fields etc that can be easily accessed
 */
object Parser {

    @Throws(ParserException::class)
    fun parse(file: String): Node? {
        val preparser = PreParser(file)
        val prenode = preparser.parse()
        return parseNode(prenode)
    }


    @Throws(ParserException::class)
    fun parseNode(prenode: Node): Node {

        if (prenode !is Tuple) {
            // Case 1: node is not of form (..) or [..], return the node itself
            return prenode
        } else {
            // Case 2: node is of form (..) or [..]
            val elements = prenode.elements

            if (delimType(prenode.open, Constants.SQUARE_BEGIN)) {
                // Case 2.1: node is of form [..]
                return VectorLiteral(parseList(elements), prenode.file, prenode.start, prenode.end, prenode.line, prenode.col)
            } else {
                // Case 2.2: node is (..)
                if (elements.isEmpty()) {
                    // Case 2.2.1: node is (). This is not allowed
                    throw ParserException("syntax error", prenode)
                } else {
                    // Case 2.2.2: node is of form (keyword ..)
                    val keyNode = elements[0]

                    return if (keyNode is Name) {
                        when (keyNode.id) {
                            Constants.SEQ_KEYWORD -> parseBlock(prenode)
                            Constants.IF_KEYWORD -> parseIf(prenode)
                            Constants.DEF_KEYWORD -> parseDef(prenode)
                            Constants.ASSIGN_KEYWORD -> parseAssign(prenode)
                            Constants.DECLARE_KEYWORD -> parseDeclare(prenode)
                            Constants.FUN_KEYWORD -> parseFun(prenode)
                            Constants.RECORD_KEYWORD -> parseRecordDef(prenode)
                            else -> parseCall(prenode)
                        }
                    } else {
                        // applications whose operator is not a name
                        // e.g. ((foo 1) 2)
                        parseCall(prenode)
                    }
                }
            }
        }
    }


    @Throws(ParserException::class)
    fun parseBlock(tuple: Tuple): Block {
        val elements = tuple.elements
        val statements = parseList(elements.subList(1, elements.size))
        return Block(statements, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseIf(tuple: Tuple): If {
        val elements = tuple.elements
        if (elements.size != 4) {
            throw ParserException("incorrect format of if", tuple)
        }
        val test = parseNode(elements[1])
        val conseq = parseNode(elements[2])
        val alter = parseNode(elements[3])
        return If(test, conseq, alter, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseDef(tuple: Tuple): Def {
        val elements = tuple.elements
        if (elements.size != 3) {
            throw ParserException("incorrect format of definition", tuple)
        }
        val pattern = parseNode(elements[1])
        val value = parseNode(elements[2])
        return Def(pattern, value, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)

    }


    @Throws(ParserException::class)
    fun parseAssign(tuple: Tuple): Assign {
        val elements = tuple.elements
        if (elements.size != 3) {
            throw ParserException("incorrect format of definition", tuple)
        }
        val pattern = parseNode(elements[1])
        val value = parseNode(elements[2])
        return Assign(pattern, value, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseDeclare(tuple: Tuple): Declare {
        val elements = tuple.elements
        if (elements.size < 2) {
            throw ParserException("syntax error in record type definition", tuple)
        }
        val properties = parseProperties(elements.subList(1, elements.size))
        return Declare(properties, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseFun(tuple: Tuple): Fun {
        val elements = tuple.elements

        if (elements.size < 3) {
            throw ParserException("syntax error in function definition", tuple)
        }

        // construct parameter list
        val preParams = elements[1]
        if (preParams !is Tuple) {
            throw ParserException("incorrect format of parameters: $preParams", preParams)
        }

        // parse the parameters, test whether it's all names or all tuples
        var hasName = false
        var hasTuple = false
        val paramNames = ArrayList<Name>()
        val paramTuples = ArrayList<Node>()

        for (p in preParams.elements) {
            if (p is Name) {
                hasName = true
                paramNames.add(p)
            } else if (p is Tuple) {
                hasTuple = true
                val argElements = p.elements
                if (argElements.size == 0) {
                    throw ParserException("illegal argument format: $p", p)
                }
                if (argElements[0] !is Name) {
                    throw ParserException("illegal argument name : " + argElements[0], p)
                }

                val name = argElements[0] as Name
                if (name.id != Constants.RETURN_ARROW) {
                    paramNames.add(name)
                }
                paramTuples.add(p)
            }
        }

        if (hasName && hasTuple) {
            throw ParserException("parameters must be either all names or all tuples: $preParams", preParams)
        }

        val properties: Scope?
        if (hasTuple) {
            properties = parseProperties(paramTuples)
        } else {
            properties = null
        }

        // construct body
        val statements = parseList(elements.subList(2, elements.size))
        val start = statements[0].start
        val end = statements[statements.size - 1].end
        val body = Block(statements, tuple.file, start, end, tuple.line, tuple.col)

        return Fun(paramNames, properties, body,
                tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseRecordDef(tuple: Tuple): RecordDef {
        val elements = tuple.elements
        if (elements.size < 2) {
            throw ParserException("syntax error in record type definition", tuple)
        }

        val name = elements[1]
        val maybeParents = elements[2]

        val parents: MutableList<Name>?
        val fields: List<Node>

        if (name !is Name) {
            throw ParserException("syntax error in record name: $name", name)
        }

        // check if there are parents (record A (B C) ...)
        if (maybeParents is Tuple && delimType(maybeParents.open, Constants.PAREN_BEGIN)) {
            val parentNodes = maybeParents.elements
            parents = ArrayList()
            for (p in parentNodes) {
                if (p !is Name) {
                    throw ParserException("parents can only be names", p)
                }
                parents.add(p)
            }
            fields = elements.subList(3, elements.size)
        } else {
            parents = null
            fields = elements.subList(2, elements.size)
        }

        val properties = parseProperties(fields)
        return RecordDef(name, parents, properties, tuple.file,
                tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseCall(tuple: Tuple): Call {
        val elements = tuple.elements
        val func = parseNode(elements[0])
        val parsedArgs = parseList(elements.subList(1, elements.size))
        val args = Argument(parsedArgs)
        return Call(func, args, tuple.file, tuple.start, tuple.end, tuple.line, tuple.col)
    }


    @Throws(ParserException::class)
    fun parseList(prenodes: List<Node>): List<Node> {
        val parsed = ArrayList<Node>()
        for (s in prenodes) {
            parsed.add(parseNode(s))
        }
        return parsed
    }


    // treat the list of nodes as key-value pairs like (:x 1 :y 2)
    @Throws(ParserException::class)
    fun parseMap(prenodes: List<Node>): Map<String, Node> {
        val ret = LinkedHashMap<String, Node>()
        if (prenodes.size % 2 != 0) {
            throw ParserException("must be of the form (:key1 value1 :key2 value2), but got: $prenodes", prenodes[0])
        }

        var i = 0
        while (i < prenodes.size) {
            val key = prenodes[i]
            val value = prenodes[i + 1]
            if (key !is Keyword) {
                throw ParserException("key must be a keyword, but got: $key", key)
            }
            ret[key.id] = value
            i += 2
        }
        return ret
    }


    @Throws(ParserException::class)
    fun parseProperties(fields: List<Node>): Scope {
        val properties = Scope()
        for (field in fields) {
            if (!(field is Tuple &&
                            delimType(field.open, Constants.SQUARE_BEGIN) &&
                            field.elements.size >= 2)) {
                throw ParserException("incorrect form of descriptor: $field", field)
            } else {
                val elements = parseList(field.elements)
                val nameNode = elements[0]
                if (nameNode !is Name) {
                    throw ParserException("expect a name, but got: $nameNode", nameNode)
                }
                val id = nameNode.id
                if (properties.containsKey(id)) {
                    throw ParserException("duplicated name: $nameNode", nameNode)
                }

                val typeNode = elements[1]
                if (typeNode !is Name) {
                    throw ParserException("type must be a name, but got: $typeNode", typeNode)
                }
                properties.put(id, "type", typeNode)

                val props = parseMap(elements.subList(2, elements.size))
                val propsObj = LinkedHashMap<String, Any>()
                for ((key, value) in props) {
                    propsObj[key] = value
                }
                properties.putProperties(nameNode.id, propsObj)
            }
        }
        return properties
    }


    fun delimType(c: Node?, d: String): Boolean {
        return c is Delimeter && c.shape == d
    }


    @Throws(ParserException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val tree = Parser.parse(args[0])
        Util.msg(tree!!.toString())
    }

}
