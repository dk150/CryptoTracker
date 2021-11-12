@file:Suppress("MemberVisibilityCanBePrivate")

package rs.dk150.cryptotracker.data

/**
 * Class used with live data to track whether its event
 * has already been handled (in this project used to show
 * toast message on successful response only once)
 */
open class SingleEvent<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    fun getContent(): T {
        hasBeenHandled = true
        return content
    }
}