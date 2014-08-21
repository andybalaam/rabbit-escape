package rabbitescape.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;

public class MenuJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private class Listener extends EmptyListener
    {
        @Override
        public void windowClosing( WindowEvent e )
        {
            exit();
        }

        @Override
        public void componentMoved( ComponentEvent e )
        {
            ConfigTools.setInt( uiConfig, CFG_MENU_WINDOW_LEFT, getX() );
            ConfigTools.setInt( uiConfig, CFG_MENU_WINDOW_TOP,  getY() );
            uiConfig.save();
        }

        @Override
        public void componentResized( ComponentEvent e )
        {
            ConfigTools.setInt( uiConfig, CFG_MENU_WINDOW_WIDTH,  getWidth() );
            ConfigTools.setInt( uiConfig, CFG_MENU_WINDOW_HEIGHT, getHeight() );
            uiConfig.save();
        }
    }

    private final Config uiConfig;

    public MenuJFrame( Config uiConfig )
    {
        this.uiConfig = uiConfig;

        Container contentPane = getContentPane();

        setBoundsFromConfig();

        Listener listener = new Listener();
        addWindowListener( listener );
        addComponentListener( listener );

        JPanel menuPanel = new JPanel( new GridLayout( 4, 1, 5, 5 ) );
        menuPanel.add( new JButton( "foo" ) );
        menuPanel.add( new JButton( "bar" ) );
        menuPanel.add( new JButton( "baz" ) );
        menuPanel.add( new JButton( "quux" ) );
        JScrollPane scrollPane = new JScrollPane( menuPanel  );
        contentPane.add( scrollPane, BorderLayout.CENTER );

        setTitle( t( "Rabbit Escape" ) );
        pack();
        setVisible( true );
    }

    private void setBoundsFromConfig()
    {
        int x      = ConfigTools.getInt( uiConfig, CFG_MENU_WINDOW_LEFT );
        int y      = ConfigTools.getInt( uiConfig, CFG_MENU_WINDOW_TOP );
        int width  = ConfigTools.getInt( uiConfig, CFG_MENU_WINDOW_WIDTH );
        int height = ConfigTools.getInt( uiConfig, CFG_MENU_WINDOW_HEIGHT );

        if ( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            setLocation( x, y );
        }

        if ( width != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            setPreferredSize( new Dimension( width, height ) );
        }
    }

    private void exit()
    {
        dispose();
    }
}
