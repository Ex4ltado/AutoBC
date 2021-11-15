package autobc

import autobc.bot.Bot
import org.sikuli.basics.Debug
import org.sikuli.basics.Settings
import java.util.*

fun main() {
    Debug.off()
    Settings.ActionLogs = false
    Settings.InfoLogs = false
    Settings.TraceLogs = false
    Settings.ProfileLogs = false
    Settings.UserLogs = false
    AutoBC()
}

class AutoBC {

    init {
        Bot.start()
    }

}