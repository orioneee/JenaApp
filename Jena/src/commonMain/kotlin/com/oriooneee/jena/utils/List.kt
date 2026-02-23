package com.oriooneee.jena.utils

fun <T> List<T>.containsAny(
    vararg elements: T
): Boolean {
    for (element in elements) {
        if (this.contains(element)) {
            return true
        }
    }
    return false
}