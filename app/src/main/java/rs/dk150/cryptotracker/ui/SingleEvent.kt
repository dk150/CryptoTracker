@file:Suppress("MemberVisibilityCanBePrivate")

package rs.dk150.cryptotracker.ui

open class SingleEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content, even if it's already been handled.
     */
    fun getContent(): T {
        hasBeenHandled = true
        return content
    }
}