package autobc.util

import org.sikuli.script.*

object SafeSikuli {

    fun findSafe(sikuli: Screen, target: Image, exact: Boolean): Match? {
        return try {
            if (exact) sikuli.find(Pattern(target).exact()) else sikuli.find(target)
        } catch (e: FindFailed) {
            null
        }
    }

    fun <PSI> waitSafe(sikuli: Screen, target: PSI, timeout: Double = 3.0): Match? {
        return try {
            sikuli.wait(target, timeout)
        } catch (e: FindFailed) {
            return null
        }
    }

    /* This method don't need to cast with Try because 'exists' don't Throw exceptions */
    fun exists(sikuli: Screen, target: Image, exact: Boolean = true, timeout: Double = 3.0): Match? {
        return try {
            if (exact) sikuli.exists(Pattern(target).exact(), timeout) else sikuli.exists(target, timeout)
        } catch (e: FindFailed) {
            null
        }
    }

}