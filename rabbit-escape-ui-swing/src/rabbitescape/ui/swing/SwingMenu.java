package rabbitescape.ui.swing;

import java.io.PrintStream;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JFrame;

import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.util.RealFileSystem;

public class SwingMenu
{
    private final Stack<Menu> stack;
    private final JFrame jframe;

    public SwingMenu( RealFileSystem fs, PrintStream out, Locale locale )
    {
        stack = new Stack<>();
        stack.push( MenuDefinition.mainMenu );

        jframe = new MenuJFrame( SwingConfigSetup.createConfig() );
    }

    public void run()
    {
        jframe.pack();
        jframe.setVisible( true );
    }
}
