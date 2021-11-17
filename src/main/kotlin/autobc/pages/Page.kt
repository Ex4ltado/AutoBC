package autobc.pages

import autobc.bot.Bot
import autobc.elements.Element
import autobc.robot.Mouse
import autobc.util.SafeSikuli
import autobc.util.retry
import org.sikuli.script.Screen
import java.awt.Point

abstract class Page {
    private val screen: Screen = Screen()

    fun existsElement(
        element: Element,
        forever: Boolean = false,
        exact: Boolean = true,
        timeout: Double = 0.0,
        maxTimeout: Double = Bot.MAX_TIMEOUT, // Only Used in Forever Action
    ): Boolean {
        if (forever) {
            foreverAction(maxTimeout) {
                SafeSikuli.exists(screen, element.image, exact, timeout)!!
                return true
            }
        }
        return SafeSikuli.exists(screen, element.image) != null
    }

    private fun findElement(
        element: Element,
        exact: Boolean = false,
        timeout: Double = 5.0
    ): Point? {
        SafeSikuli.waitSafe(screen, element.image, timeout)
        val find = SafeSikuli.findSafe(screen, element.image, exact) ?: return null
        return Point(find.x, find.y)
    }

    fun foreverElementStepAction(
        elements: Array<Element>,
        minDelayBetweenActions: Long = 1000L,
        maxDelayBetweenActions: Long = 2000L,
        exact: Boolean = false,
    ) {
        elements.forEach {
            val delayBetweenActions = (minDelayBetweenActions..maxDelayBetweenActions).random()
            moveMouseToElement(it, forever = true, exact = exact, click = true)
            Thread.sleep(delayBetweenActions)
        }
    }

    fun moveMouseToElement(
        element: Element,
        randomPosition: Boolean = true,
        forever: Boolean = true,
        exact: Boolean = false,
        click: Boolean = false,
        timeout: Double = 5.0,
        maxTimeout: Double = Bot.MAX_TIMEOUT, // Only Used in Forever Action
        bodyFind: (() -> Unit)? = null,
        bodyNotFind: () -> Unit = Bot::restart,
    ) {
        var elementPosition: Point? = null
        if (forever) {
            foreverAction(maxTimeout) {
                elementPosition = findElement(element, exact, timeout)!!
            }
            if (elementPosition == null) {
                bodyNotFind()
                return
            }
        } else {
            elementPosition = findElement(element, exact, timeout)
        }
        if (elementPosition != null) {
            val bounds = element.getBounds()
            if (randomPosition) {
                Mouse.moveMouse(elementPosition!!, bounds[0], bounds[1])
            } else Mouse.moveMouse(
                elementPosition!!
            )
            if (click) Mouse.click()
            bodyFind?.invoke()
        }
    }

    private inline fun foreverAction(maxTimeout: Double, body: () -> Unit) =
        retry(maxDuration = maxTimeout) { body() }

    abstract fun action()

}
