package rabbitescape.ui.swing;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GameJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private class Listener implements WindowListener
    {
        @Override
        public void windowActivated( WindowEvent e )
        {
        }

        @Override
        public void windowClosed( WindowEvent e )
        {
        }

        @Override
        public void windowClosing( WindowEvent e )
        {
            if ( gameLoop != null )
            {
                gameLoop.stop();
            }

            dispose();
        }

        @Override
        public void windowDeactivated( WindowEvent e )
        {
        }

        @Override
        public void windowDeiconified( WindowEvent e )
        {
        }

        @Override
        public void windowIconified( WindowEvent e )
        {
        }

        @Override
        public void windowOpened( WindowEvent e )
        {
        }
    }

    private SwingGameLoop gameLoop;

    public GameJFrame()
    {
        gameLoop = null;

        addWindowListener( new Listener() );

        JLabel label = new JLabel( "Hello World" );
        getContentPane().add( label );

        pack();
        setVisible(true);
    }

    public void setGameLoop( SwingGameLoop gameLoop )
    {
        this.gameLoop = gameLoop;
    }
}
