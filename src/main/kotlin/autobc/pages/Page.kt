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

    protected fun detectCaptcha(timeout: Double = 1.0/*Settings.AutoWaitTimeout.toDouble()*/) {

        var attempt = 1
        var smoothOffset = 0
        val differenceLimit = 0.68 // Insert your value!!
        var neededDifference = differenceLimit
        val points = ArrayList<Double>()
        while (true) {
            Mouse.releaseClick()

            if (attempt > 4) {
                Bot.restart()
                break
            }

            Window.log("Detecting Captcha...")

            Thread.sleep(1000L)

            val popupWidth = Image("images/Captcha/popup.png")
            val popupHeight = Image("images/Captcha/popup_height.png")
            val slide = Element("images/Captcha/slide.png")

            val popupWidthMatch = screen.exists(Pattern(popupWidth.image).exact(), timeout) ?: break
            val popupHeightMatch = screen.exists(Pattern(popupHeight.image).exact(), timeout) ?: break
            val slideMatch = screen.exists(Pattern(slide.image).similar(0.9), timeout) ?: break

            Mouse.moveMouse(Point(slideMatch.x, slideMatch.y), slideMatch.w, slideMatch.h)

            val popupCaptcha =
                Rectangle(popupWidthMatch.x, popupWidthMatch.y, popupWidthMatch.w, popupHeightMatch.h)

            var found = false
            val moveCondition = {
                val bufferedImage = ScreenUtil.printScreen(popupCaptcha)
                val diff = bufferedImage.findDifferencePercentage(ScreenUtil.printScreen(popupCaptcha))
                points.add(diff)
                if (diff <= neededDifference) found = true
                !found
            }

            Window.log("Trying Complete The Captcha, Attempt: $attempt")

            val slideBar = SafeSikuli.findSafe(screen, Image("images/Captcha/slide_bar.png").image, true)

            val width = slideBar?.w ?: popupCaptcha.width

            Mouse.holdClick()
            var moveMouseAttempt = 1
            while (!found && moveMouseAttempt <= 4) {
                var mousePos = MouseInfo.getPointerInfo().location
                fun slideMouse(x: Int) {
                    val smooth = (1500 + smoothOffset..2000 + smoothOffset).random()
                    val minSteps = 10.0 + attempt
                    Mouse.mouseSmooth(
                        x,
                        mousePos.y,
                        minSteps = minSteps,
                        smooth = smooth,
                        moveCondition = moveCondition
                    )
                    smoothOffset += 1000
                    moveMouseAttempt++
                }
                slideMouse(mousePos.x + width)
                mousePos = MouseInfo.getPointerInfo().location
                slideMouse(mousePos.x - width)
                val lastIndexes = 2
                neededDifference = if (points.size < lastIndexes * attempt) {
                    neededDifference + (attempt * 0.1)
                } else {
                    points.filter { it >= differenceLimit }.sorted().slice(0..lastIndexes * attempt).average()
                }
            }
            Thread.sleep((1000L..1500L).random())
            Mouse.releaseClick()

            Window.log("Captcha Completed (Maybe)", Color.ORANGE)
            smoothOffset += 2000
            attempt++
            points.clear()

            Thread.sleep(3000L)
        }
        Window.log("Captcha Not Found!", Color.GREEN)

    }

    abstract fun action()

}
