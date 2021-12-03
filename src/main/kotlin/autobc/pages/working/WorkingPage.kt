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

    private val backButton = Button("images/Global Buttons/button_back.png")
    private val errorMessage = Message("images/Errors/error_popup.png")
    private val newMapButton = Button("images/Mining Map/new_map.png")
    private val okButton = Button("images/Global Buttons/button_ok.png")
    private var afkSeconds = 0
    private var isWorking = false


    /**
     * Prevent chest closed - In rare cases in maps missing 1 chest AutoBC can't detect Sleeping Heroes
     * and causing a Loop with Sleeping Heroes surrounding the last chest
     */
    private fun preventChestClosed() {
        val buttonMine = Button("images/Menu/button_mine.png")
        foreverElementStepAction(arrayOf(backButton, buttonMine), exact = true)
    }

    override fun action() {

        isWorking = true
        var waitingHeroesSeconds = 0
        var preventChestClosedSeconds = 0
        var minutesToTriggerPreventChestClosed = (30..40).random()

        Window.log("Starting Work!", Color.GREEN)

        thread(name = "WorkingThread") {
            while (isWorking) {
                Bot.runningSeconds++
                afkSeconds++
                if (Bot.isSomeoneSleeping) {
                    waitingHeroesSeconds++
                } else {
                    preventChestClosedSeconds++
                }
                Thread.sleep(1000L)
            }
        }

        while (!Bot.isDisconnected) {

            // AFK For some minutes after some hours running to prevent Bot Detection
            if (afkSeconds >= Bot.afkAfterHours * 60 * 60) {
                Window.log("Staying AFK for ${Bot.afkTimeToWaitInMinutes} minutes")
                Bot.isAFK = true
                Bot.sleep(Duration.ofMinutes(Bot.afkTimeToWaitInMinutes.toLong()).toMillis())
                Bot.setAfkTime()
                afkSeconds = 0
            }

            // Verify if is disconnected
            if (existsElement(errorMessage, exact = true, timeout = 0.0)) {
                // Sleep some minutes to prevent Bot Detection
                Bot.sleep(Duration.ofMinutes((2..5).random().toLong()).toMillis())
                isWorking = false
                moveMouseToElement(okButton, click = true)
                Bot.disconnected()
                // Break And LogIn again
                break
            }

            moveMouseToElement(newMapButton, forever = false, click = true, timeout = 0.0, bodyFind = {
                Bot.mapsCompleted++
                Window.log("+1 Map Completed, Total = ${Bot.mapsCompleted}", Color.GREEN)
                detectCaptcha()
            })

            if (preventChestClosedSeconds >= minutesToTriggerPreventChestClosed * 60) {
                preventChestClosed()
                preventChestClosedSeconds = 0
                minutesToTriggerPreventChestClosed = (30..40).random()
            }

            // Verify Sleeping Heroes
            if (Bot.isSomeoneSleeping) {
                if (waitingHeroesSeconds >= Bot.minutesToWaitHeroesSleeping * 60) {
                    Window.log("Trying put heroes to Work again", Color.ORANGE)
                    moveMouseToElement(backButton, click = true)
                    Bot.isSomeoneSleeping = false
                    waitingHeroesSeconds = 0
                    MenuPage().action()
                    Bot.setSleepTime()
                    Window.log("Starting Work Again", Color.GREEN)
                }
            } else {
                for (sleepingImage in sleepingTicks) {
                    if (existsElement(sleepingImage, forever = false, exact = false, timeout = 0.0)) {
                        Bot.isSomeoneSleeping = true
                        Window.log("Sleeping Hero Found", Color.ORANGE)
                        Window.log("Waiting ${Bot.minutesToWaitHeroesSleeping} minutes to put Heroes to Work again")
                        break
                    }
                }
            }

            Thread.sleep(500L)
        }

    }

}