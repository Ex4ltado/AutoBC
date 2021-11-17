package autobc.bot

import autobc.frame.Window
import autobc.pages.Page
import autobc.pages.login.LoginPage
import autobc.pages.menu.MenuPage
import autobc.pages.working.WorkingPage
import autobc.robot.Keyboard
import autobc.util.Resources
import java.awt.Color
import java.awt.EventQueue
import java.time.Duration
import java.util.*
import kotlin.math.round

object Bot {

    private val steps = arrayListOf(LoginPage(), MenuPage(), WorkingPage())

    private var isRunning = false
    var isDisconnected = false

    private var isWorking = false
    var isSomeoneSleeping = false
    var mapsCompleted: Int = 0

    var runningSeconds = 1

    var MAX_TIMEOUT: Double = (10.0 * 60.0) * 1000 // 10 Minutes

    var NUMBER_OF_HEROES = 10

    private var MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = 45
    private var MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = 80

    var minutesToWaitHeroesSleeping = MIN_MINUTES_TO_WAIT_HEROES_SLEEPING

    private var MIN_AFK_HOURS = 6
    private var MAX_AFK_HOURS = 8

    var afkAfterHours = 5

    private var MIN_AFK_TIME_TO_WAIT_IN_MINUTES = 80
    private var MAX_AFK_TIME_TO_WAIT_IN_MINUTES = 120

    var afkTimeToWaitInMinutes = 80

    fun start() {
        loadConfiguration()
        EventQueue.invokeLater { Window.create() }
        loop()
    }

    fun restart() {
        isRunning = false
        Window.log("Restarting...")
        Keyboard.reloadPage()
        sleep(Duration.ofSeconds(10).toMillis())
        loop()
    }

    private fun loop() {
        isRunning = true
        while (isRunning) steps.forEach(Page::action)
    }

    private fun loadConfiguration() {
        val properties = Properties()
        properties.load(Resources.findFile("config.properties").inputStream())
        NUMBER_OF_HEROES = properties["NUMBER_OF_HEROES"].toString().toInt()
        MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MIN_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()
        MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MAX_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()

        MIN_AFK_HOURS = properties["MIN_AFK_AFTER_HOURS"].toString().toInt()
        MAX_AFK_HOURS = properties["MAX_AFK_AFTER_HOURS"].toString().toInt()

        MIN_AFK_TIME_TO_WAIT_IN_MINUTES = properties["MIN_AFK_TIME_TO_WAIT_IN_MINUTES"].toString().toInt()
        MAX_AFK_TIME_TO_WAIT_IN_MINUTES = properties["MAX_AFK_TIME_TO_WAIT_IN_MINUTES"].toString().toInt()

        afkAfterHours = (MIN_AFK_HOURS..MAX_AFK_HOURS).random()
        afkTimeToWaitInMinutes = (MIN_AFK_TIME_TO_WAIT_IN_MINUTES..MAX_AFK_TIME_TO_WAIT_IN_MINUTES).random()

        MAX_TIMEOUT = properties["MAX_TIMEOUT"].toString().toDouble()
    }

    fun setSleepMinutes() {
        minutesToWaitHeroesSleeping =
            (MIN_MINUTES_TO_WAIT_HEROES_SLEEPING..MAX_MINUTES_TO_WAIT_HEROES_SLEEPING).random()
    }

    fun setNewAfkTime() {
        afkAfterHours = (MIN_AFK_HOURS..MAX_AFK_HOURS).random()
        afkTimeToWaitInMinutes = (MIN_AFK_TIME_TO_WAIT_IN_MINUTES..MAX_AFK_TIME_TO_WAIT_IN_MINUTES).random()
    }

    fun disconnected() {
        Window.log("Disconnected...", Color.RED)
        isDisconnected = true
        isWorking = false
        isSomeoneSleeping = false
        Thread.sleep(5000)
        //restart()
    }

    fun isOnLinux(): Boolean {
        val operationalSystem = System.getProperty("os.name").lowercase()
        return operationalSystem.indexOf("nux") >= 0
    }

    fun sleep(millis: Long) {
        Window.log("Sleeping for ${((millis / 1000.0) / 60.0).roundCase()} minute(s)")
        Thread.sleep(millis)
    }

    private fun Double.roundCase(): Double {
        var multiplier = 1.0
        repeat(2) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}
