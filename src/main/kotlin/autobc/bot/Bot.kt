package autobc.bot

import autobc.frame.Window
import autobc.pages.Page
import autobc.pages.login.LoginPage
import autobc.pages.menu.MenuPage
import autobc.pages.working.WorkingPage
import autobc.util.Resources
import java.awt.Color
import java.awt.EventQueue
import java.io.File
import java.io.InputStream
import java.util.*

object Bot {

    private val steps = arrayListOf(LoginPage(), MenuPage(), WorkingPage())

    var isDisconnected = false

    var isWorking = false
    var isSomeoneSleeping = false
    var mapsCompleted: Int = 0

    var runningSeconds = 0

    var NUMBER_OF_HEROES = 10

    private var MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = 15
    private var MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = 25

    var minutesToWaitHeroesSleeping = MIN_MINUTES_TO_WAIT_HEROES_SLEEPING

    private fun loadConfiguration() {
        val properties = Properties()
        properties.load(Resources.findFile("config.properties").inputStream())
        NUMBER_OF_HEROES = properties["NUMBER_OF_HEROES"].toString().toInt()
        MIN_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MIN_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()
        MAX_MINUTES_TO_WAIT_HEROES_SLEEPING = properties["MAX_MINUTES_TO_WAIT_HEROES_SLEEPING"].toString().toInt()
    }

    fun setSleepMinutes() {
        minutesToWaitHeroesSleeping =
            (MIN_MINUTES_TO_WAIT_HEROES_SLEEPING..MAX_MINUTES_TO_WAIT_HEROES_SLEEPING).random()
    }

    fun start() {
        loadConfiguration()
        EventQueue.invokeLater { Window.create() }
        steps.forEach(Page::action)
    }

    fun disconnected() {
        Window.log("Disconnected...", Color.RED)
        isDisconnected = true
        isWorking = false
        isSomeoneSleeping = false
    }

}