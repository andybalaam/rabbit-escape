package rabbitescape.ui.swing2

import java.awt.EventQueue

fun createAndShowMainWindow() {
    val mainWindow = MainWindow()
    mainWindow.isVisible = true
}

fun main(args: Array<String>) {
    EventQueue.invokeLater(::createAndShowMainWindow)
}