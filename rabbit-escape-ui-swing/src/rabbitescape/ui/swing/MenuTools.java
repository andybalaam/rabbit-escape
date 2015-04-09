package rabbitescape.ui.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class MenuTools
{
    public static void clickOnKey(
        final AbstractButton button, String actionName, int key )
    {
        clickOnKey( button, actionName, KeyStroke.getKeyStroke( key, 0 ) );
    }

    public static void clickOnKey(
        final AbstractButton button,
        String actionName,
        KeyStroke... keyStrokes
    )
    {
        InputMap inputMap = button.getInputMap(
            JButton.WHEN_IN_FOCUSED_WINDOW );

        for ( KeyStroke keyStroke : keyStrokes )
        {
            inputMap.put( keyStroke, actionName );
        }

        button.getActionMap().put( actionName, new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed( ActionEvent e )
            {
                button.doClick();
            }
        } );
    }
}
