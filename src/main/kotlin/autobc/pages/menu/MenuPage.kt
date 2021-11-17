package autobc.pages.menu

import autobc.bot.Bot
import autobc.elements.button.Button
import autobc.elements.image.Image
import autobc.elements.message.Message
import autobc.frame.Window
import autobc.pages.Page
import autobc.robot.Mouse
import autobc.util.Direction
import java.awt.Color

class MenuPage : Page() {

    private val buttonMine = Button("images/Menu/button_mine.png")
    private val buttonHeroesList = Button("images/Menu/button_heroes_list.png")
    private val buttonPutHeroToWork = Button("images/Heroes List/button_work_3.png")
    private val buttonPutHeroToWork2 = Button("images/Heroes List/button_work_2.png")
    private val buttonClose = Button("images/Global Buttons/button_close.png")
    private val buttonUpgrade = Button("images/Heroes List/button_upgrade.png")

    private val scrollPosition = Image("images/Heroes List/scroll_position.png")
    private val restImage = Image("images/Heroes List/rest_image.png")
    private val errorOverloadedServerMessage = Message("images/Heroes List/error_overloaded_server.png")

    override fun action() {
        // Check Heroes
        Window.log("Opening Heroes List", Color.GREEN)
        moveMouseToElement(buttonHeroesList, click = true)
        Window.log("Scrolling...", Color.WHITE)
        moveMouseToElement(scrollPosition, exact = true)
        Mouse.scroll(Direction.DOWN, scrolls = 50)

        // Put Heroes To Work
        var existAnyHeroSleeping = true
        for (hero in 0 until Bot.NUMBER_OF_HEROES) {
            if (!existAnyHeroSleeping) break
            /*if (!existsElement(buttonPutHeroToWork, timeout = 4.0, exact = true)) {
                if (existsElement(errorOverloadedServerMessage, timeout = 1.0)) {
                    moveMouseToElement(buttonClose, click = true)
                } else {
                    break
                }
            }*/
            moveMouseToElement(
                buttonPutHeroToWork,
                randomPosition = true,
                exact = true,
                maxTimeout = (6000..10000).random().toDouble(),
                bodyNotFind = {
                    if (existsElement(errorOverloadedServerMessage, timeout = 1.0)) moveMouseToElement(
                        buttonClose,
                        click = true
                    ) else existAnyHeroSleeping = false
                }, bodyFind = {
                    Window.log("Putting Hero to Work!", Color.GREEN)
                    Mouse.clickAndSleep((1000..1200).random().toLong())
                })
        }
        Bot.isSomeoneSleeping = false

        // Close and Start Mining
        foreverElementStepAction(arrayOf(buttonClose, buttonMine))
        Bot.setSleepMinutes()
    }

}