package org.yinwang.yin


import java.util.Arrays

object Constants {

    // delimiters and delimeter pairs
    val LINE_COMMENT = "--"

    val PAREN_BEGIN = "("
    val PAREN_END = ")"

    val CURLY_BEGIN = "{"
    val CURLY_END = "}"

    val SQUARE_BEGIN = "["
    val SQUARE_END = "]"

    val ATTRIBUTE_ACCESS = "."
    val RETURN_ARROW = "->"

    val STRING_START = "\""
    val STRING_END = "\""
    val STRING_ESCAPE = "\\"


    // keywords
    val SEQ_KEYWORD = "seq"
    val FUN_KEYWORD = "fun"
    val IF_KEYWORD = "if"
    val DEF_KEYWORD = "define"
    val ASSIGN_KEYWORD = "set!"
    val RECORD_KEYWORD = "record"
    val DECLARE_KEYWORD = "declare"
    val UNION_KEYWORD = "U"

    var IDENT_CHARS = Arrays.asList('~', '!', '@', '#', '$', '%', '^', '&', '*', '-', '_', '=', '+', '|',
            ':', ';', ',', '<', '>', '?', '/')


}
