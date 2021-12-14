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
import kotlin.concurrent.thread

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

    @Volatile
    private var didCaptcha = false
    protected fun detectCaptcha(timeout: Double = Settings.AutoWaitTimeout.toDouble()) {

        var attempt = 1
        var smoothOffset = 0

        /*
        *  0.0 = Auto Detect
        */
        val differenceLimit = 0.68

        val points = ArrayList<Double>()
        while (true) {
            didCaptcha = false
            var neededDifference = differenceLimit

            Window.log("Detecting Captcha...")

            Thread.sleep(1000L)

            val popupWidth = Image("images/Captcha/popup.png")
            val popupHeight = Image("images/Captcha/popup_height.png")
            val slider = Element("images/Captcha/slider.png")

            val popupWidthMatch = screen.exists(Pattern(popupWidth.image).exact(), timeout) ?: break
            val popupHeightMatch = screen.exists(Pattern(popupHeight.image).exact(), timeout) ?: break
            val sliderMatch = screen.exists(Pattern(slider.image).similar(0.9), timeout) ?: break

            if (attempt > 4) {
                Bot.restart()
                break
            }
            val popupCaptcha =
                Rectangle(popupWidthMatch.x, popupWidthMatch.y, popupWidthMatch.w, popupHeightMatch.h)

            if (!didCaptcha) {
                thread {
                    while (!didCaptcha) {
                        val popupPrintScreen = ScreenUtil.printScreen(popupCaptcha)
                        val diff = popupPrintScreen.findDifferencePercentage(ScreenUtil.printScreen(popupCaptcha))
                        points.add(diff)
                        if (diff <= neededDifference) didCaptcha = true
                    }
                }
            }

            Mouse.moveMouse(Point(sliderMatch.x + 1, sliderMatch.y + 1), sliderMatch.w - 1, sliderMatch.h - 1)

            Window.log("Trying Complete The Captcha, Attempt: $attempt")

            val sliderRightCorner =
                SafeSikuli.findSafe(screen, Image("images/Captcha/slider_right_corner.png").image, false) ?: break

            val width = sliderRightCorner.x.minus(sliderMatch.x)

            val moveCondition = { !didCaptcha }

            Mouse.holdClick()
            var moveMouseAttempt = 1
            while (!didCaptcha && moveMouseAttempt <= 4) {
                var mousePos = MouseInfo.getPointerInfo().location
                fun slideMouse(x: Int) {
                    val smooth = (4500 + smoothOffset..5500 + smoothOffset).random()
                    val minSteps = 15.0
                    Mouse.mouseSmoothHumanized(
                        Point(
                            x,
                            mousePos.y
                        ),
                        minSteps = minSteps,
                        smooth = smooth,
                        moveCondition = moveCondition
                    )
                    smoothOffset += 1000
                    moveMouseAttempt++
                    val lastIndexes = 0
                    neededDifference = points.filter { it >= differenceLimit }.sorted().slice(0..lastIndexes).average()
                }
                slideMouse(mousePos.x + width)
                mousePos = MouseInfo.getPointerInfo().location
                slideMouse(mousePos.x - width)
            }
            Thread.sleep((1000L..1500L).random())
            Mouse.releaseClick()
            smoothOffset += 2000
            attempt++
            points.clear()
            Thread.sleep(3000L)
        }
        Window.log("Captcha Not Found!", Color.GREEN)

    }

    abstract fun action()

}
