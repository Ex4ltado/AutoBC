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
import org.sikuli.script.Pattern
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

        var attempt = 1
        var smoothOffset = 0
        while (true) {
            Mouse.releaseClick()

            Window.log("Detecting Captcha...")

            val popupWidth = Image("images/Captcha/popup.png")
            val popupHeight = Image("images/Captcha/popup_height.png")
            val slide = Element("images/Captcha/slide.png")

            val popupWidthMatch = screen.exists(Pattern(popupWidth.image).exact(), timeout)
            val popupHeightMatch = screen.exists(Pattern(popupHeight.image).exact(), timeout)
            val slideMatch = screen.exists(Pattern(slide.image).similar(0.9), timeout)

            if (popupWidthMatch != null && popupHeightMatch != null && slideMatch != null) {

                moveMouseToElement(slide)

                val popupCaptcha =
                    Rectangle(popupWidthMatch.x, popupWidthMatch.y, popupWidthMatch.w, popupHeightMatch.h)

                var found = false
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
                    fun moveMouse(x: Int) {
                        val smooth = (5000 + smoothOffset..7000 + smoothOffset).random()
                        val minSteps = 3.0
                        Mouse.mouseSmooth(
                            x,
                            mousePos.y,
                            minSteps = minSteps,
                            smooth = smooth,
                            moveCondition = moveCondition
                        )
                        smoothOffset += 1000
                    }
                    moveMouse(mousePos.x + width)
                    mousePos = MouseInfo.getPointerInfo().location
                    moveMouse(mousePos.x - width)
                }
                Thread.sleep((1000L..1500L).random())
                Mouse.releaseClick()

                Window.log("Captcha Completed (Maybe)", Color.ORANGE)
                smoothOffset += 1000
                attempt++

                Thread.sleep(2000L)
            } else {
                Window.log("Captcha Not Found!", Color.GREEN)
                break
            }
        }

    }

    abstract fun action()

}
