package io.github.pawgli.sudoku.utils

open class Event<out T>(private val content: T) {

    private var isHandled = false

    fun getContentIfNotHandled(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            return content
        }
    }

    fun getContent() = content
}

class CallbackEvent<out T>(content: T, val callback: () -> Unit) : Event<T>(content)

