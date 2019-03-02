package rabbitescape.ui.swing2

import java.awt.Container
import java.awt.LayoutManager

interface ModeImpl {
    val componentPainter: ComponentPainter
    fun createLayout(contentPane: Container): LayoutManager
}
