package autobc.util

inline fun <R> retry(
    delay: Long = 1000,
    maxDuration: Double? = null,
    noinline exceptionHandler: ((Throwable) -> Unit)? = null,
    body: () -> R
) {
    val startTime = Timer()
    while (!Thread.interrupted()) {
        if (maxDuration != null) {
            if (startTime.hasReached(maxDuration)) break
        }
        try {
            body()
            break
        } catch (t: Throwable) {
            exceptionHandler?.invoke(t)
            Thread.sleep(delay)
        }
    }
}