package com.astontraineeship.recyclerviewtraining

object CreateId {
    private var value = 0

    fun getNext() = value++
}