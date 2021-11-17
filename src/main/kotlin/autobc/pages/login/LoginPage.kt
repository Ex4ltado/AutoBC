package autobc.pages.login

import autobc.bot.Bot
import autobc.elements.button.Button
import autobc.elements.message.Message
import autobc.frame.Window
import autobc.pages.Page
import autobc.robot.Keyboard
import java.awt.Color

class LoginPage : Page() {

    private val buttonConnectWalletButton = Button("images/Login/button_connect_wallet.png")
    private val buttonConnectMetamask = Button("images/Login/button_fox.png")

    private val buttonAssignMetamask = Button("images/Login/button_assign.png")

    private val metamaskMessage = Message("images/Login/metamask_login_message.png")

    private fun connectMetamaskWallet() {
        Window.log("Trying Connect to Metamask Wallet", Color.ORANGE)
        existsElement(metamaskMessage, forever = true, exact = false, timeout = 2.0)
        if (!Bot.isOnLinux()) {
            Keyboard.maximizeShortcut()
        }
        moveMouseToElement(buttonAssignMetamask, click = true)
        Window.log("Logged to Metamask Wallet", Color.GREEN)
    }

    private fun login() {
        Window.log("Trying Login", Color.ORANGE)
        foreverElementStepAction(arrayOf(buttonConnectWalletButton, buttonConnectMetamask))
        connectMetamaskWallet()
        Window.log("Login Successful", Color.GREEN)
        Bot.isDisconnected = false
    }

    override fun action() {
        login()
    }

}