package autobc.robot

import org.sikuli.script.Screen
import java.awt.Robot
import java.awt.event.KeyEvent

object Keyboard {

    private val robot = Robot().also { it.autoDelay = 250 }

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

    fun pressEnter() {
        robot.keyPress(KeyEvent.VK_ENTER)
        robot.keyRelease(KeyEvent.VK_ENTER)
    }

    fun type(text: String) {

        Screen().type(text)

        /*for (char in text) {
            val keyCode = KeyEvent.getExtendedKeyCodeForChar(char.code)
            if (KeyEvent.VK_UNDEFINED == keyCode) {
                continue
            }
            when (keyCode) {
                KeyEvent.VK_UNDERSCORE -> {
                    robot.keyPress(KeyEvent.VK_SHIFT)
                    robot.keyPress(KeyEvent.VK_MINUS)
                    robot.keyRelease(KeyEvent.VK_MINUS)
                    robot.keyRelease(KeyEvent.VK_SHIFT)
                }
                else -> {
                    press(keyCode)
                }
            }
        }
        robot.autoDelay = 250*/
    }

    private fun press(keyCode: Int) {
        robot.keyPress(keyCode)
        robot.delay((70..150).random())
        robot.keyRelease(keyCode)
        robot.delay((70..150).random())
    }

}