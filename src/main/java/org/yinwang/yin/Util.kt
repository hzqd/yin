package org.yinwang.yin

import org.yinwang.yin.ast.Node

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths

object Util {

    fun readFile(path: String): String? {
        try {
            val encoded = Files.readAllBytes(Paths.get(path))
            return Charset.forName("UTF-8").decode(ByteBuffer.wrap(encoded)).toString()
        } catch (e: IOException) {
            return null
        }

    }


    fun msg(m: String) {
        println(m)
    }


    fun abort(m: String) {
        System.err.println(m)
        System.err.flush()
        Thread.dumpStack()
        System.exit(1)
    }


    fun abort(loc: Node, msg: String) {
        System.err.println(loc.fileLineCol + " " + msg)
        System.err.flush()
        Thread.dumpStack()
        System.exit(1)
    }


    fun joinWithSep(ls: Collection<Any>, sep: String): String {
        val sb = StringBuilder()
        var i = 0
        for (s in ls) {
            if (i > 0) {
                sb.append(sep)
            }
            sb.append(s.toString())
            i++
        }
        return sb.toString()
    }


    fun unifyPath(filename: String): String {
        return unifyPath(File(filename))
    }


    fun unifyPath(file: File): String {
        try {
            return file.canonicalPath
        } catch (e: Exception) {
            abort("Failed to get canonical path")
            return ""
        }

    }

}
