package autobc.util

import org.sikuli.script.*

object SafeSikuli {

    fun <PSI> findSafe(sikuli: Screen, target: PSI): Match? {
        try {
            return sikuli.find(target)
        } catch (e: FindFailed) {
        }
        return null
    }

    fun <PSI> waitSafe(sikuli: Screen, target: PSI, timeout: Double = 3.0): Match? {
        try {
            return sikuli.wait(target)
        } catch (e: FindFailed) {
        }
        return null
    }

    fun exists(sikuli: Screen, target: Image, exact: Boolean = true, timeout: Double = 3.0): Match? {
        try {
            return if (exact) sikuli.exists(Pattern(target).exact(), timeout) else sikuli.exists(target, timeout)
        } catch (e: FindFailed) {
        }
        return null
    }

}