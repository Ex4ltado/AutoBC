package autobc.pages.menu

import autobc.bot.Bot
import autobc.elements.button.Button
import autobc.elements.image.Image
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
    private val buttonCloseHeroesList = Button("images/Global Buttons/button_close.png")

    private val scrollPosition = Image("images/Heroes List/scroll_position.png")
    private val restImage = Image("images/Heroes List/rest_image.png")

    override fun action() {
        // Check Heroes
        moveMouseToElement(buttonHeroesList, click = true)
        Window.log("Opening Heroes List", Color.GREEN)
        moveMouseToElement(scrollPosition)
        Window.log("Scrolling...", Color.WHITE)
        Mouse.scroll(Direction.DOWN, scrolls = 50)

        // Put Heroes To Work
        for (hero in 0 until Bot.NUMBER_OF_HEROES) {
            if (!existsElement(buttonPutHeroToWork, timeout = 4.0)) break
            moveMouseToElement(buttonPutHeroToWork, randomPosition = true)
            Window.log("Putting Hero to Work!", Color.GREEN)
            Mouse.clickAndSleep((550..1000).random().toLong())
        }
        Bot.isSomeoneSleeping = false

        // Close and Start Mining
        foreverElementStepAction(arrayOf(buttonCloseHeroesList, buttonMine))
        Bot.setSleepMinutes()
    }

}