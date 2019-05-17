package rabbitescape.ui.swing;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * A listener that does nothing.  Extend to provide behaviour.
 */
public class EmptyListener
    implements
        WindowListener,
        ComponentListener,
        MouseListener,
        MouseMotionListener,
        KeyListener
{
    @Override
    public void componentHidden( ComponentEvent arg0 )
    {
    }

    @Override
    public void componentMoved( ComponentEvent arg0 )
    {
    }

    @Override
    public void componentResized( ComponentEvent arg0 )
    {
    }

    @Override
    public void componentShown( ComponentEvent arg0 )
    {
    }

    @Override
    public void windowActivated( WindowEvent arg0 )
    {
    }

    @Override
    public void windowClosed( WindowEvent arg0 )
    {
    }

    @Override
    public void windowClosing( WindowEvent arg0 )
    {
    }

    @Override
    public void windowDeactivated( WindowEvent arg0 )
    {
    }

    @Override
    public void windowDeiconified( WindowEvent arg0 )
    {
    }

    @Override
    public void windowIconified( WindowEvent arg0 )
    {
    }

    @Override
    public void windowOpened( WindowEvent arg0 )
    {
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
    }

    @Override
    public void mouseDragged( MouseEvent arg0 )
    {
    }

    @Override
    public void mouseMoved( MouseEvent arg0 )
    {
    }

    @Override
    public void keyPressed( KeyEvent arg0 )
    {
    }

    @Override
    public void keyReleased( KeyEvent arg0 )
    {
    }

    @Override
    public void keyTyped( KeyEvent arg0 )
    {
    }
}

