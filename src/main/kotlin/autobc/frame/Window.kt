package autobc.frame

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

object Window : JFrame("") {

    private val textArea = JTextPane()
    private val scrollPane = JScrollPane(textArea)

    fun create() {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(300, 300)
        preferredSize = Dimension(300, 300)
        isAlwaysOnTop = true
        textArea.font = Font(Font.SANS_SERIF, Font.BOLD, 14)
        textArea.isEditable = false
        textArea.background = Color.BLACK
        scrollPane.autoscrolls = true
        add(scrollPane)
        setLocation(0, 0)
        pack()
        isVisible = true
    }

    fun log(message: String, color: Color = Color.WHITE) {
        val doc: StyledDocument = textArea.styledDocument

        val keyWord = SimpleAttributeSet()
        StyleConstants.setForeground(keyWord, color)
        StyleConstants.setBold(keyWord, true)

        try {
            doc.insertString(doc.length, message.plus("\n"), keyWord)
        } catch (ignored: Exception) {
        }

        pack()
    }

}