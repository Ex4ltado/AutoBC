package autobc.util

import java.awt.Desktop
import java.net.URI

object DesktopUtil {

    fun openBrowserUrl(url: String) {
        Desktop.getDesktop().browse(URI(url))
    }
}