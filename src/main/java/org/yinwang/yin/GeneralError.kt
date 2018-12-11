package org.yinwang.yin


import org.yinwang.yin.ast.Node

class GeneralError : Exception {
    var msg: String
    var location: Node? = null


    constructor(location: Node, msg: String) {
        this.msg = msg
        this.location = location
    }


    constructor(msg: String) {
        this.msg = msg
    }


    override fun toString(): String {
        return if (location != null) {
            location!!.fileLineCol + ": " + msg
        } else {
            msg
        }
    }

}
