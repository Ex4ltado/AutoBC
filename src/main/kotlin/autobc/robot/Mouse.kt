package autobc.robot

import autobc.util.Direction
import com.github.joonasvali.naturalmouse.api.MouseMotion
import com.github.joonasvali.naturalmouse.api.MouseMotionFactory
import com.github.joonasvali.naturalmouse.util.FactoryTemplates
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent


object Mouse {

    var motionFactory: MouseMotionFactory = FactoryTemplates.createFastGamerMotionFactory()

    private val robot = Robot()

    fun click() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(30L)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
    }

    fun clickAndSleep(millis: Long) {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK)
        Thread.sleep(30L)
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK)
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
        motionFactory.move(x, y)
    }

    /*private fun mouseSmooth(x: Int, y: Int, smooth: Int = 1000) {
        val mousePosition = MouseInfo.getPointerInfo().location
        val point = Point(x, y)
        val a2b = point.distance(mousePosition)
        if (a2b == 0.0) return
        val sqa2b = sqrt(a2b)
        val steps = sqa2b * 3
        val dx = (x - mousePosition.x) / steps
        val dy = (y - mousePosition.y) / steps
        val dt = smooth / steps
        var step = 1.0
        while (step < steps) {
            Thread.sleep(dt.toLong())
            robot.mouseMove((mousePosition.x + dx * step).toInt(), (mousePosition.y + dy * step).toInt())
            step++
        }
    }*/

    fun scroll(direction: Direction, force: Int = 2, scrolls: Int = 10) {
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

}