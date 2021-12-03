package autobc.pages

import autobc.bot.Bot
import autobc.elements.Element
import autobc.elements.image.Image
import autobc.frame.Window
import autobc.robot.Mouse
import autobc.util.SafeSikuli
import autobc.util.ScreenUtil
import autobc.util.ScreenUtil.findDifferencePercentage
import autobc.util.retry
import org.sikuli.basics.Settings
import org.sikuli.script.Match
import org.sikuli.script.Screen
import java.awt.Color
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Rectangle

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

    fun findElement(
        element: Element,
        exact: Boolean = false,
        timeout: Double = 5.0
    ): Match? {
        SafeSikuli.waitSafe(screen, element.image, timeout)
        return SafeSikuli.findSafe(screen, element.image, exact)
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

    inline fun moveMouseToElement(
        element: Element,
        randomPosition: Boolean = true,
        forever: Boolean = true,
        exact: Boolean = false,
        click: Boolean = false,
        timeout: Double = 5.0,
        maxTimeout: Double = Bot.MAX_TIMEOUT, // Only Used in Forever Action
        bodyFind: () -> Unit = {},
        bodyNotFind: () -> Unit = Bot::restart,
    ) {
        var elementMatch: Match? = null
        if (forever) {
            foreverAction(maxTimeout) {
                elementMatch = findElement(element, exact, timeout)!!
            }
            if (elementMatch == null) {
                Window.log("Failed to find ${element.name}", Color.RED)
                bodyNotFind()
                return
            }
        } else {
            elementMatch = findElement(element, exact, timeout)
        }
        if (elementMatch != null) {
            val bounds = element.getBounds()
            val elementPosition = Point(elementMatch!!.x, elementMatch!!.y)
            if (randomPosition) {
                Mouse.moveMouse(elementPosition, bounds[0], bounds[1])
            } else {
                Mouse.moveMouse(elementPosition)
            }
            if (click) Mouse.click()
            bodyFind()
        }
    }

    inline fun foreverAction(maxTimeout: Double, body: () -> Unit) =
        retry(maxDuration = maxTimeout) { body() }

    protected fun detectCaptcha(timeout: Double = Settings.AutoWaitTimeout.toDouble()) {

        var found = false
        for (attempt in 1..3) {

            Window.log("Detecting Captcha...")

            val popupWidth = Image("images/Captcha/popup.png")
            val popupHeight = Image("images/Captcha/popup_height.png")

            val popupWidthMatch = screen.exists(popupWidth.image, timeout)
            val popupHeightMatch = screen.exists(popupHeight.image, timeout)

            if (popupWidthMatch != null && popupHeightMatch != null) {

                moveMouseToElement(Element("images/Captcha/slide.png"))

                val popupCaptcha =
                    Rectangle(popupWidthMatch.x, popupWidthMatch.y, popupWidthMatch.w, popupHeightMatch.h)

                val moveCondition = {
                    val bufferedImage = ScreenUtil.printScreen(popupCaptcha)
                    Thread.sleep(1L)
                    val neededDifference = 0.68
                    val diff = bufferedImage.findDifferencePercentage(ScreenUtil.printScreen(popupCaptcha))
                    if (diff <= neededDifference) found = true
                    if (found) Window.log("Correct Captcha", Color.GREEN)
                    !found
                }

                Window.log("Trying Complete The Captcha, Attempt: $attempt")

                val slideBar = SafeSikuli.findSafe(screen, Image("images/Captcha/slide_bar.png").image, true)

                val width = slideBar?.w ?: popupCaptcha.width

                Mouse.holdClick()
                while (!found) {
                    var mousePos = MouseInfo.getPointerInfo().location
                    val smooth = (8000..9000).random()
                    val minSteps = 10.0
                    Mouse.mouseSmooth(
                        mousePos.x + width,
                        mousePos.y,
                        minSteps = minSteps,
                        smooth = smooth,
                        moveCondition = moveCondition
                    )
                    mousePos = MouseInfo.getPointerInfo().location
                    Mouse.mouseSmooth(
                        mousePos.x - width,
                        mousePos.y,
                        minSteps = minSteps,
                        smooth = smooth,
                        moveCondition = moveCondition
                    )
                }
                Thread.sleep((600L..1000L).random())

                Mouse.releaseClick()

                Window.log("Captcha Completed (Maybe)", Color.ORANGE)
            } else {
                Window.log("Captcha Not Found!", Color.GREEN)
                break
            }
        }

    }

    abstract fun action()

}
