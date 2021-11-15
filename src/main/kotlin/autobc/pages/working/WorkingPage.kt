package autobc.pages.working

import autobc.bot.Bot
import autobc.elements.button.Button
import autobc.elements.message.Message
import autobc.frame.Window
import autobc.pages.Page
import autobc.pages.menu.MenuPage
import java.awt.Color
import kotlin.concurrent.thread

class WorkingPage : Page() {

    private val sleepingTicks = arrayOf(
        Button("images/Mining Map/sleeping/1_tick_sleeping_hero.png"),
        Button("images/Mining Map/sleeping/2_tick_sleeping_hero.png"),
        Button("images/Mining Map/sleeping/3_tick_sleeping_hero.png"),
        Button("images/Mining Map/sleeping/4_tick_sleeping_hero.png")
    )

    private val errorMessage = Message("images/Errors/error_popup.png")
    private val okButton = Button("images/Global Buttons/button_ok.png")
    private val backButton = Button("images/Global Buttons/button_back.png")
    private val newMapButton = Button("images/Mining Map/new_map.png")

    private var timerRunning = false

    override fun action() {

        Bot.isWorking = true
        Window.log("Starting Work!", Color.GREEN)

        thread {
            timerRunning = true
            while (timerRunning) {
                Bot.runningSeconds++
                Thread.sleep(1000L)
            }
        }

        while (!Bot.isDisconnected) {
            // Verify Error
            if (existsElement(errorMessage, exact = true, timeout = 0.0)) {
                moveMouseToElement(okButton, click = true)
                timerRunning = false
                Thread.sleep(1001L)
                Bot.disconnected()
                // Break And LogIn again
                break
            }

            moveMouseToElement(newMapButton, forever = false, click = true, timeout = 0.0) {
                Bot.mapsCompleted++
                Window.log("+1 Map Completed", Color.GREEN)
            }

            // Verify Sleeping Heroes
            if (!Bot.isSomeoneSleeping) {
                sleepingTicks.forEach {
                    if (existsElement(it, forever = false, exact = false, timeout = 0.0)) {
                        Window.log("Sleeping Hero Founded", Color.ORANGE)
                        Bot.isSomeoneSleeping = true
                        return@forEach
                    }
                }
            } else {
                if ((Bot.runningSeconds / 60) % Bot.minutesToWaitHeroesSleeping == 0) {
                    moveMouseToElement(backButton, click = true)
                    Window.log("Back to Main Menu", Color.ORANGE)
                    MenuPage().action()
                }
            }
            Thread.sleep(500L)
        }

    }

}