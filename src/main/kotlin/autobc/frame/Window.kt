package autobc.frame

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.time.LocalDateTime
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
        //isAlwaysOnTop = true
        textArea.font = Font(Font.SANS_SERIF, Font.BOLD, 13)
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
        try {
            val keyWord = SimpleAttributeSet()
            StyleConstants.setBold(keyWord, true)
            StyleConstants.setForeground(keyWord, Color.WHITE)
            doc.insertString(doc.length, "[${LocalDateTime.now().toLocalTime()}] : ", keyWord)
            StyleConstants.setForeground(keyWord, color)
            doc.insertString(doc.length, message.plus("\n"), keyWord)
        } catch (ignored: Exception) {
        }
    }

}