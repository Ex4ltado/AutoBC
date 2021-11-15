package autobc.pages.login

import autobc.elements.button.Button
import autobc.elements.message.Message
import autobc.frame.Window
import autobc.pages.Page
import autobc.robot.Keyboard
import java.awt.Color

class LoginPage : Page() {

    private val buttonConnectWalletButton = Button("images/Login/button_connect_wallet.png")
    private val buttonConnectMetamask = Button("images/Login/button_fox.png")

    private val buttonAssinarMetamask = Button("images/Login/button_assinar.png")

    private val metamaskMessage = Message("images/Login/metamask_login_message.png")

    private fun connectMetamaskWallet() {
        Window.log("Trying Connect to Metamask Wallet", Color.ORANGE)
        existsElement(metamaskMessage, forever = true, exact = false, timeout = 2.0)
        Keyboard.maximizeShortcut()
        moveMouseToElement(buttonAssinarMetamask, click = true)
        Window.log("Logged to Metamask Wallet", Color.GREEN)
    }

    private fun login() {
        Window.log("Trying Login", Color.ORANGE)
        foreverElementStepAction(arrayOf(buttonConnectWalletButton, buttonConnectMetamask))
        connectMetamaskWallet()
        Window.log("Login Successful", Color.GREEN)
    }

    override fun action() {
        login()
    }

}