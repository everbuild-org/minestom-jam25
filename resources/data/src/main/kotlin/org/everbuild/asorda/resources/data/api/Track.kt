package org.everbuild.asorda.resources.data.api

abstract class Track<T>(protected var element: T) {
    abstract fun increment()
    fun getAndIncrement() : T {
        val result = element
        increment()
        return result
    }

    companion object {
        private class CharTrack(char: Char) : Track<Char>(char) {
            override fun increment() {
                element++
            }
        }

        fun char(char: Char): Track<Char> = CharTrack(char)
    }
}

