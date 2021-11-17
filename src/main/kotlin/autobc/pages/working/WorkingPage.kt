package autobc.pages.working

import autobc.bot.Bot
import autobc.elements.button.Button
import autobc.elements.message.Message
import autobc.frame.Window
import autobc.pages.Page
import autobc.pages.menu.MenuPage
import java.awt.Color
import java.time.Duration
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

    private var isWorking = false
    private var waitingHeroesSeconds = 0
    private var afkSeconds = 0

    override fun action() {

        isWorking = true
        Window.log("Starting Work!", Color.GREEN)

        thread(name = "WorkingThread") {
            while (isWorking) {
                Bot.runningSeconds++
                afkSeconds++
                if (Bot.isSomeoneSleeping) {
                    waitingHeroesSeconds++
                }
                Thread.sleep(1000L)
            }
        }

        while (!Bot.isDisconnected) {

            // AFK For some minutes after some hours to prevent Bot Detection
            if (afkSeconds >= Duration.ofHours(Bot.afkAfterHours.toLong()).seconds) {
                Window.log("Staying AFK for ${Bot.afkTimeToWaitInMinutes} minutes")
                Bot.sleep(Duration.ofMinutes(Bot.afkTimeToWaitInMinutes.toLong()).toMillis())
                Bot.setNewAfkTime()
                afkSeconds = 0
            }

            // Verify if is disconnected
            if (existsElement(errorMessage, exact = true, timeout = 0.0)) {
                // Sleep some minutes to prevent Bot Detection
                Bot.sleep(Duration.ofMinutes((1..3).random().toLong()).toMillis())
                isWorking = false
                moveMouseToElement(okButton, click = true)
                Bot.disconnected()
                // Break And LogIn again
                break
            }

            moveMouseToElement(newMapButton, forever = false, click = true, timeout = 0.0, bodyFind = {
                Bot.mapsCompleted++
                Window.log("+1 Map Completed, Total = ${Bot.mapsCompleted}", Color.GREEN)
            })

            // Verify Sleeping Heroes
            if (!Bot.isSomeoneSleeping) {
                for (sleepingImage in sleepingTicks) {
                    if (existsElement(sleepingImage, forever = false, exact = false, timeout = 0.0)) {
                        Bot.isSomeoneSleeping = true
                        Window.log("Sleeping Hero Found", Color.ORANGE)
                        Window.log("Waiting ${Bot.minutesToWaitHeroesSleeping} minutes to put Heroes to Work again")
                        break
                    }
                }
            } else {
                if (waitingHeroesSeconds >= Duration.ofMinutes(Bot.minutesToWaitHeroesSleeping.toLong()).seconds) {
                    moveMouseToElement(backButton, click = true)
                    Window.log("Back to Main Menu", Color.ORANGE)
                    MenuPage().action()
                    waitingHeroesSeconds = 0
                    Bot.isSomeoneSleeping = false
                    Window.log("Starting Work Again", Color.GREEN)
                }
            }

            Thread.sleep(500L)
        }

    }

}