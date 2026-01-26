package com.oriooneee.jet.navigation.engine.utils

object Log {
    fun d(tag: String, message: String) {
        println("DEBUG: [$tag] $message")
    }

    fun e(tag: String, message: String) {
        println("ERROR: [$tag] $message")
    }
}
