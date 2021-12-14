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

    //private val buttonUpgrade = Button("images/Heroes List/button_upgrade.png")
    //private val restImage = Image("images/Heroes List/rest_image.png")
    //private val scrollPosition = Image("images/Heroes List/scroll_position.png")
    private val buttonClose = Button("images/Global Buttons/button_close.png")
    private val buttonHeroesList = Button("images/Menu/button_heroes_list.png")
    private val buttonMine = Button("images/Menu/button_mine.png")
    private val buttonPutHeroToWork = Button("images/Heroes List/button_work.png")
    private val errorOverloadedServerMessage = Message("images/Heroes List/error_overloaded_server.png")
    private val fullEnergyHero = Image("images/Heroes List/full_energy_hero.png")
    private val scrollPart = Image("images/Heroes List/scroll_part.png")

    override fun action() {
        // Check Heroes
        Window.log("Waiting Heroes List", Color.ORANGE)
        moveMouseToElement(buttonHeroesList, click = true)
        detectCaptcha()
        Window.log("Opening Heroes List", Color.GREEN)
        Window.log("Scrolling...", Color.WHITE)
        moveMouseToElement(scrollPart, exact = true)
        Mouse.scroll(Direction.DOWN, scrolls = (18..20).random())

        putHeroesToWork(Bot.ONLY_PUT_FULL_HEROES_TO_WORK)

        Bot.isSomeoneSleeping = false

        // Close and Start Mining
        foreverElementStepAction(arrayOf(buttonClose, buttonMine))
        Bot.setSleepTime()
    }

    private fun putHeroesToWork(onlyPutFullHeroesToWork: Boolean) {
        // TODO: Only put full heroes to work
        var existAnyHeroSleeping = true
        while (existAnyHeroSleeping) {
            moveMouseToElement(
                buttonPutHeroToWork,
                randomPosition = true,
                exact = true,
                maxTimeout = (5000..7000).random().toDouble(),
                bodyNotFind = {
                    if (existsElement(errorOverloadedServerMessage, timeout = 1.0)) {
                        Window.log("Overloaded Server Message Found!", Color.ORANGE)
                        Bot.sleep((5000L..10000L).random())
                        moveMouseToElement(
                            buttonClose,
                            click = true
                        )
                    } else {
                        existAnyHeroSleeping = false
                    }
                }, bodyFind = {
                    Window.log("Putting Hero to Work!", Color.GREEN)
                    Mouse.clickAndSleep((1000..1200).random().toLong())
                })
        }

    }

}