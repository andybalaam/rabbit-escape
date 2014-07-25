package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.ui.swing.SwingGameInit.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;

public class GameJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private class Listener implements WindowListener, ComponentListener
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

        @Override
        public void componentHidden( ComponentEvent e )
        {
        }

        @Override
        public void componentMoved( ComponentEvent e )
        {
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_LEFT, getX() );
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_TOP,  getY() );
            uiConfig.save();
        }

        @Override
        public void componentResized( ComponentEvent e )
        {
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_WIDTH,  getWidth() );
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_HEIGHT, getHeight() );
            uiConfig.save();
        }

        @Override
        public void componentShown( ComponentEvent e )
        {
        }
    }

    public Canvas canvas;

    private final Config uiConfig;
    private SwingGameLoop gameLoop;

    public GameJFrame( Config uiConfig )
    {
        this.uiConfig = uiConfig;
        this.gameLoop = null;
        this.canvas = initUi();
        initListeners();
    }

    private Canvas initUi()
    {
        setIgnoreRepaint( true );

        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize( new Dimension( 400, 200 ) );

        JScrollPane scrollPane = new JScrollPane( canvas );
        getContentPane().add( scrollPane, BorderLayout.CENTER );

        setBoundsFromConfig();

        setTitle( t( "Rabbit Escape" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );

        return canvas;
    }

    private void initListeners()
    {
        Listener listener = new Listener();
        addWindowListener( listener );
        addComponentListener( listener );
    }

    private void setBoundsFromConfig()
    {
        int x      = ConfigTools.getInt( uiConfig, CFG_GAME_WINDOW_LEFT );
        int y      = ConfigTools.getInt( uiConfig, CFG_GAME_WINDOW_TOP );
        int width  = ConfigTools.getInt( uiConfig, CFG_GAME_WINDOW_WIDTH );
        int height = ConfigTools.getInt( uiConfig, CFG_GAME_WINDOW_HEIGHT );

        if ( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            setLocation( x, y );
        }

        if ( width != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            setPreferredSize( new Dimension( width, height ) );
        }
    }

    public void setGameLoop( SwingGameLoop gameLoop )
    {
        this.gameLoop = gameLoop;
    }
}
