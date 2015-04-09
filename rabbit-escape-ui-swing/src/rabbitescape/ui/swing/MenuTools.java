package rabbitescape.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.KeyStroke;

public class MenuTools
{
    static void listen(
        final AbstractButton button,
        final String actionName,
        final int key,
        final ActionListener actionListener
    )
    {
        button.addActionListener( actionListener );

        button.getInputMap( JButton.WHEN_IN_FOCUSED_WINDOW )
            .put( KeyStroke.getKeyStroke( key, 0 ), actionName );

        button.getActionMap().put( actionName, new AbstractAction()
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed( ActionEvent e )
            {
                button.setSelected( !button.isSelected() ); // For toggles
                actionListener.actionPerformed( e );
            }
        } );
    }

    public static void clickOnKey(
        final AbstractButton button, String actionName, int key )
    {
        button.getInputMap( JButton.WHEN_IN_FOCUSED_WINDOW )
            .put( KeyStroke.getKeyStroke( key, 0 ), actionName );

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
