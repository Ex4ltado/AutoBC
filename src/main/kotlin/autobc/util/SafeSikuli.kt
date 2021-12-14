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

    fun waitSafe(sikuli: Screen, target: Image, timeout: Double = 3.0): Match? {
        return try {
            sikuli.wait(target, timeout)
        } catch (e: FindFailed) {
            return null
        }
    }
    
    fun exists(sikuli: Screen, target: Image, exact: Boolean = true, timeout: Double = 3.0): Match? {
        return try {
            if (exact) sikuli.exists(Pattern(target).exact(), timeout) else sikuli.exists(target, timeout)
        } catch (e: FindFailed) {
            null
        }
    }

}