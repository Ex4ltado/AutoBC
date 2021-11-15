package autobc.pages

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
        timeout: Double = 0.0
    ): Boolean {
        if (forever) {
            foreverAction {
                SafeSikuli.exists(screen, element.image, exact, timeout)!!
                return true
            }
        }
        return SafeSikuli.exists(screen, element.image) != null
    }

    private fun findElement(element: Element, timeout: Double = 5.0): Point? {
        SafeSikuli.waitSafe(screen, element.image, timeout)
        val find = SafeSikuli.findSafe(screen, element.image) ?: return null
        return Point(find.x, find.y)
    }

    fun foreverElementStepAction(
        elements: Array<Element>,
        minDelayBetweenActions: Long = 1000L,
        maxDelayBetweenActions: Long = 2000L
    ) {
        elements.forEach {
            val delayBetweenActions = (minDelayBetweenActions..maxDelayBetweenActions).random()
            moveMouseToElement(it, forever = true, click = true)
            Thread.sleep(delayBetweenActions)
        }
    }

    fun moveMouseToElement(
        element: Element,
        randomPosition: Boolean = true,
        forever: Boolean = true,
        click: Boolean = false,
        timeout: Double = 5.0,
        body: (() -> Unit)? = null
    ) {
        var elementPosition: Point? = Point()
        if (forever) {
            foreverAction {
                elementPosition!!.location = findElement(element, timeout) ?: throw Exception()
            }
        } else {
            elementPosition = findElement(element, timeout)
        }
        if (elementPosition != null) {
            val bounds = element.getBounds()
            if (randomPosition) Mouse.moveMouse(elementPosition, bounds[0], bounds[1]) else Mouse.moveMouse(
                elementPosition
            )
            if (click) Mouse.click()
            body?.invoke()
        }
    }

    private inline fun foreverAction(body: () -> Unit) = retry { body() }

    abstract fun action()

}
