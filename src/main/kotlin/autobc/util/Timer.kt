package autobc.util

class Timer {
    var prevMS: Long
        private set

    init {
        prevMS = 0L
        reset()
    }

    fun delay(d: Double): Boolean {
        return hasReached(d, true)
    }

    fun delay(i: Int): Boolean {
        return hasReached(i.toDouble(), true)
    }

    @JvmOverloads
    fun hasReached(d: Double, reset: Boolean = false): Boolean {
        if (timePassed >= d) {
            if (reset) reset()
            return true
        }
        return false
    }

    fun reset() {
        prevMS = time
    }

    private val time: Long
        get() = System.nanoTime() / 1000000L

    private val timePassed: Long
        get() = time - prevMS
}