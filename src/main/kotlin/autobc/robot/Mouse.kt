package autobc.robot

import autobc.util.Direction
import com.github.joonasvali.naturalmouse.api.MouseMotionFactory
import com.github.joonasvali.naturalmouse.util.FactoryTemplates
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent
import java.lang.Math.toRadians
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.sqrt


object Mouse {

    private var mouseMotionFactory: MouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory()

    private val robot = Robot().also { it.autoDelay = 0 }

    fun click() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        //Thread.sleep(100L) // 30
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun holdClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun releaseClick() {
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun clickAndSleep(millis: Long) {
        click()
        Thread.sleep(millis)
    }

    fun moveMouse(x: Int, y: Int) {
        mouseSmooth(x, y)
    }

    fun moveMouse(point: Point) {
        mouseSmooth(point.x, point.y)
    }

    fun moveMouse(point: Point, width: Int, height: Int) {
        val randX = (0..width).random()
        val randY = (0..height).random()
        mouseSmooth(point.x + randX, point.y + randY)
    }

    private fun mouseSmooth(x: Int, y: Int) {
        mouseMotionFactory.move(x, y)
    }

    fun mouseSmooth(x: Int, y: Int, smooth: Int = 1000) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val point = Point(x, y)
        val a2b = point.distance(mousePosition)
        if (a2b == 0.0) return
        val sqa2b = sqrt(a2b)
        val steps = sqa2b * 5
        val dx = (x - mousePosition.x) / steps
        val dy = (y - mousePosition.y) / steps
        val dt = smooth / steps
        var step = 1.0
        while (step < steps) {
            Thread.sleep(dt.toLong())
            robot.mouseMove((mousePosition.x + dx * step).toInt(), (mousePosition.y + dy * step).toInt())
            step++
        }
    }

    fun mouseSmooth(x: Int, y: Int, minSteps: Double = 3.0, smooth: Int = 1000, moveCondition: () -> Boolean) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val point = Point(x, y)
        val a2b = point.distance(mousePosition)
        if (a2b == 0.0) return
        val sqa2b = sqrt(a2b)
        val steps = sqa2b * minSteps
        val dx = (x - mousePosition.x) / steps
        val dy = (y - mousePosition.y) / steps
        val dt = smooth / steps
        var step = 1.0
        while (step < steps) {
            if (moveCondition()) {
                robot.mouseMove((mousePosition.x + dx * step).toInt(), (mousePosition.y + dy * step).toInt())
                Thread.sleep(dt.toLong())
                step++
            } else {
                break
            }
        }
    }


    fun mouseSmoothHumanized(position: Point, minSteps: Double, smooth: Int, moveCondition: () -> Boolean) {
        val mousePosition = MouseInfo.getPointerInfo().location

        val distance = mousePosition.distance(position)
        val sqrt = sqrt(distance)

        val steps = sqrt * minSteps
        val radSteps = toRadians(180 / steps)

        val xOffset = (position.x - mousePosition.x) / steps
        val yOffset = (position.y - mousePosition.y) / steps

        var x = radSteps
        var y = radSteps

        var waviness = 2.8
        if (distance < 120) waviness = 0.5

        var multiplier = 1

        val random = ThreadLocalRandom.current()
        if (random.nextBoolean()) x *= floor(random.nextDouble() * waviness + 1)
        if (random.nextBoolean()) y *= floor(random.nextDouble() * waviness + 1)
        if (random.nextBoolean()) multiplier *= -1

        val offset = random.nextDouble() * (1.6 + sqrt(steps)) + 6 + 2

        val totalSteps = steps.toInt() + 2
        for (i in 1..totalSteps) {
            if (moveCondition()) {
                val stepX = mousePosition.x + ((xOffset * i).toInt() + multiplier * (offset * sin(x * i)).toInt())
                val stepY = mousePosition.y + ((yOffset * i).toInt() + multiplier * (offset * sin(y * i)).toInt())
                robot.mouseMove(stepX, stepY)
                Thread.sleep((smooth / totalSteps).toLong())
            } else return
        }

        robot.mouseMove(position.x, position.y)
    }

    fun scroll(direction: Direction, force: Int = 1, scrolls: Int = 10) {
        val wheelAmt = when (direction) {
            Direction.UP -> {
                -force
            }
            Direction.DOWN -> {
                force
            }
        }
        for (i in 0..scrolls) {
            robot.mouseWheel(wheelAmt)
            Thread.sleep(50)
        }
    }

    fun scrollWithSteps(direction: Direction, force: Int = 2, scrolls: Int = 10, action: () -> Unit) {
        val wheelAmt = when (direction) {
            Direction.UP -> {
                -force
            }
            Direction.DOWN -> {
                force
            }
        }
        for (i in 0..scrolls) {
            robot.mouseWheel(wheelAmt)
            Thread.sleep(50)
            action()
        }
    }

}