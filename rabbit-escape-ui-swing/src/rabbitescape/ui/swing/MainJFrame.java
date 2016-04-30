package rabbitescape.ui.swing;

import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;

public class MainJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private class Listener extends EmptyListener
    {
        private final MainJFrame frame;

        public Listener( MainJFrame frame )
        {
            this.frame = frame;
        }

        @Override
        public void windowClosing( WindowEvent e )
        {
            exit();
        }

        @Override
        public void componentMoved( ComponentEvent e )
        {
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_LEFT, frame.getX() );
            ConfigTools.setInt( uiConfig, CFG_GAME_WINDOW_TOP,  frame.getY() );
            uiConfig.save();
        }

        @Override
        public void componentResized( ComponentEvent e )
        {
            if ( !frame.isActive() )
            { // awt.EDT fires off a couple of events before the frame is active
              // the second changes the frame's size slightly. protect against this.
                return;
            }
            ConfigTools.setInt(
                uiConfig, CFG_GAME_WINDOW_WIDTH,  frame.getWidth() );

            ConfigTools.setInt(
                uiConfig, CFG_GAME_WINDOW_HEIGHT, frame.getHeight() );

            uiConfig.save();

            if ( null != swingGraphics )
            {
                swingGraphics.redraw();
            }
        }
    }

    private final Config uiConfig;
    private final SwingSound sound;
    private SwingGraphics swingGraphics = null;

    public MainJFrame( Config uiConfig, SwingSound sound )
    {
        this.uiConfig = uiConfig;
        this.sound = sound;

        Listener listener = new Listener( this );
        addWindowListener( listener );
        addComponentListener( listener );

        setBoundsFromConfig();

        setIcon();
    }

    public void setGraphics( SwingGraphics swingGraphics )
    {
        this.swingGraphics = swingGraphics;
    }

    private void setIcon()
    {
        SwingBitmapLoader l = new SwingBitmapLoader();
        int s = l.sizeFor( 128 );
        SwingBitmap bmp = l.load( "ic_launcher", s );
        this.setIconImage(bmp.image);
    }

    public void setBoundsFromConfig()
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

    public void exit()
    {
        sound.dispose();
        dispose();
        System.exit( 0 );
    }
}
