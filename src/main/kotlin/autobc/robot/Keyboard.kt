package autobc.robot

import java.awt.Robot
import java.awt.event.KeyEvent

object Keyboard {

    private val robot = Robot()

    init {
        robot.autoDelay = 250
    }

    fun maximizeShortcut() {
        robot.keyPress(KeyEvent.VK_WINDOWS)
        robot.keyPress(KeyEvent.VK_UP)
        robot.keyRelease(KeyEvent.VK_WINDOWS)
        robot.keyRelease(KeyEvent.VK_UP)
    }

    fun reloadPage() {
        robot.keyPress(KeyEvent.VK_F5)
        robot.keyRelease(KeyEvent.VK_F5)
    }

}