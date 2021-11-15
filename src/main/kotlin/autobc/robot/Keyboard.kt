package autobc.robot

import java.awt.Robot
import java.awt.event.KeyEvent

object Keyboard {

    private val robot = Robot()

    fun maximizeShortcut() {
        robot.autoDelay = 250
        robot.keyPress(KeyEvent.VK_WINDOWS)
        robot.keyPress(KeyEvent.VK_UP)
        robot.keyRelease(KeyEvent.VK_WINDOWS)
        robot.keyRelease(KeyEvent.VK_UP)
    }

}