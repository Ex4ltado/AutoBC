package autobc.bot

import autobc.frame.Window
import autobc.pages.Page
import autobc.pages.login.LoginPage
import autobc.pages.menu.MenuPage
import autobc.pages.working.WorkingPage
import autobc.robot.Keyboard
import autobc.util.DesktopUtil
import autobc.util.HwndUtil
import autobc.util.Resources
import java.awt.Color
import java.awt.EventQueue
import java.time.Duration
import java.util.*
import kotlin.math.round

object Bot {

    private const val PAGE_URI = "https://app.bombcrypto.io"
    private val steps = arrayListOf(LoginPage(), MenuPage(), WorkingPage())
    private var browserWindow: Long = 0L
    private var isRunning = false
    private var isWorking = false
    private var MAX_AFK_HOURS = 8
    private var MAX_AFK_TIME_TO_WAIT_IN_MINUTES = 120
    private var MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = 80
    var METAMASK_PASSWORD: String = "Password"
    private var MIN_AFK_HOURS = 6
    private var MIN_AFK_TIME_TO_WAIT_IN_MINUTES = 80
    private var MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = 45

    var afkAfterHours = 5
    var afkTimeToWaitInMinutes = 80
    var BROWSER_PAGE_NAME: String = "Bombcrypto"
    var isAFK = false
    var isDisconnected = false
    var isSomeoneSleeping = false
    var mapsCompleted: Int = 0
    var MAX_TIMEOUT: Double = (2.0 * 60.0) * 1000 // 2 Minutes
    var minutesToWaitHeroesSleeping = 45
    var ONLY_PUT_FULL_HEROES_TO_WORK = false
    var runningSeconds = 1

    fun start() {
        loadConfiguration()
        EventQueue.invokeLater { Window.create() }
        getBrowserWindow()
        loop()
    }

    fun restart() {
        isRunning = false
        Window.log("Restarting...")
        getBrowserWindow()
        bringBrowserToForeground()
        sleep(Duration.ofSeconds(5).toMillis())
        Keyboard.reloadPage()
        sleep(Duration.ofSeconds(10).toMillis())
        loop()
    }

    private fun getBrowserWindow() {
        browserWindow = HwndUtil.getWindowByWindowName(BROWSER_PAGE_NAME)
    }

    private fun bringBrowserToForeground() {
        if (browserWindow > 0L) {
            HwndUtil.setForegroundWindow(browserWindow)
        } else {
            DesktopUtil.openBrowserUrl(PAGE_URI)
        }
    }

    private fun loop() {
        isRunning = true
        while (isRunning) steps.forEach(Page::action)
    }

    private fun loadConfiguration() {
        val properties = Properties()
        properties.load(Resources.findFile("config.properties").inputStream())

        BROWSER_PAGE_NAME = properties["BROWSER_PAGE_NAME"].toString()
        METAMASK_PASSWORD = properties["METAMASK_PASSWORD"].toString()

        ONLY_PUT_FULL_HEROES_TO_WORK = properties["ONLY_PUT_FULL_HEROES_TO_WORK"].toString().toBooleanStrict()

        MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MIN_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()
        MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MAX_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()

        MIN_AFK_HOURS = properties["MIN_AFK_AFTER_HOURS"].toString().toInt()
        MAX_AFK_HOURS = properties["MAX_AFK_AFTER_HOURS"].toString().toInt()

        MIN_AFK_TIME_TO_WAIT_IN_MINUTES = properties["MIN_AFK_TIME_TO_WAIT_IN_MINUTES"].toString().toInt()
        MAX_AFK_TIME_TO_WAIT_IN_MINUTES = properties["MAX_AFK_TIME_TO_WAIT_IN_MINUTES"].toString().toInt()

        afkAfterHours = (MIN_AFK_HOURS..MAX_AFK_HOURS).random()
        afkTimeToWaitInMinutes = (MIN_AFK_TIME_TO_WAIT_IN_MINUTES..MAX_AFK_TIME_TO_WAIT_IN_MINUTES).random()

        MAX_TIMEOUT = properties["MAX_TIMEOUT"].toString().toDouble()

        setSleepTime()
        setAfkTime()
    }

    fun setSleepTime() {
        minutesToWaitHeroesSleeping =
            (MIN_MINUTES_TO_WAIT_HEROES_SLEEPING..MAX_MINUTES_TO_WAIT_HEROES_SLEEPING).random()
    }

    fun setAfkTime() {
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
