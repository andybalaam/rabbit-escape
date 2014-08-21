package rabbitescape.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.AboutText;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;

public class MenuJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    public static class UnknownMenuItemType extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final String name;
        public final MenuItem.Type type;

        public UnknownMenuItemType( MenuItem item )
        {
            this.name = item.name;
            this.type = item.type;
        }
    }

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

    private class ButtonListener implements ActionListener
    {
        private final MenuItem item;

        public ButtonListener( MenuItem item )
        {
            this.item = item;
        }

        @Override
        public void actionPerformed( ActionEvent event )
        {
            switch ( item.type )
            {
                case MENU:
                {
                    SwingUtilities.invokeLater( new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            stack.add( item.menu );
                            placeMenu();
                        }
                    } );
                    return;
                }
                case ABOUT:
                {
                    about();
                    return;
                }
                case LEVEL:
                {
                    level( (LevelMenuItem)item );
                    return;
                }
                case QUIT:
                {
                    exit();
                    return;
                }
                case DEMO:
                {
                    return;
                }
                default:
                {
                    throw new UnknownMenuItemType( item );
                }
            }
        }
    }

    private static final Color backgroundColor = Color.WHITE;

    private final RealFileSystem fs;
    private final PrintStream out;
    private final Locale locale;
    private final BitmapCache<SwingBitmap> bitmapCache;

    private final Stack<Menu> stack;
    private final Config uiConfig;
    private final JPanel menuPanel;
    private final SideMenu sidemenu;

    public MenuJFrame(
        RealFileSystem fs,
        PrintStream out,
        Locale locale,
        BitmapCache<SwingBitmap> bitmapCache,
        Config uiConfig
    )
    {
        this.fs = fs;
        this.out = out;
        this.locale = locale;
        this.bitmapCache = bitmapCache;
        this.stack = new Stack<>();
        this.uiConfig = uiConfig;
        this.menuPanel = new JPanel( new GridLayout( 0, 1, 4, 4 ) );

        stack.push( MenuDefinition.mainMenu );

        Container contentPane = getContentPane();

        setBoundsFromConfig();

        Listener listener = new Listener();
        addWindowListener( listener );
        addComponentListener( listener );

        contentPane.setLayout( new BorderLayout( 4, 4 ) );

        sidemenu = new SideMenu(
            contentPane,
            bitmapCache,
            new Dimension( 32, 32 ),
            uiConfig,
            backgroundColor
        );

        JScrollPane scrollPane = new JScrollPane( menuPanel  );
        contentPane.add( scrollPane, BorderLayout.CENTER );
        contentPane.setBackground( backgroundColor );
        scrollPane.setBackground( backgroundColor );
        menuPanel.setBackground( backgroundColor );

        placeMenu();

        initListeners();

        setTitle( t( "Rabbit Escape" ) );
        pack();
        setVisible( true );
    }

    public void placeMenu()
    {
        Menu menu = stack.lastElement();

        menuPanel.removeAll();

        JLabel label = new JLabel( t( menu.intro ) );
        label.setHorizontalAlignment( SwingConstants.CENTER );
        label.setForeground( Color.RED );
        menuPanel.add( label );

        for ( MenuItem item : menu.items )
        {
            JButton button = new JButton( t( item.name, item.nameParams ) );
            button.setBackground( backgroundColor );
            button.addActionListener( new ButtonListener( item ) );
            button.setVisible( true );
            menuPanel.add( button );
        }

        repaint();
        pack();  // TODO: shouldn't need to do this
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

    private void level( final LevelMenuItem item )
    {
        new SwingWorker<Void, Void>()
        {
            @Override
            protected Void doInBackground() throws Exception
            {
                new SwingSingleGameMain(
                    fs, out, locale, bitmapCache, uiConfig ).launchGame(
                        new String[] { item.fileName } );

                return null;
            }
        }.execute();
    }

    private void about()
    {
        JTextPane text = new JTextPane();
        text.setText( t( AboutText.text ) );
        text.setBackground( null );

        JOptionPane.showMessageDialog(
            MenuJFrame.this,
            text,
            t( "About Rabbit Escape" ),
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void exit()
    {
        dispose();
    }

    private void back()
    {
        stack.pop();

        if ( stack.empty() )
        {
            exit();
        }
        else
        {
            SwingUtilities.invokeLater(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        placeMenu();
                    }
                }
            );
        }
    }

    private void setMuted( boolean muted )
    {
        ConfigTools.setBool( uiConfig, CFG_MUTED, muted );
        uiConfig.save();
    }

    private void initListeners()
    {
        Listener listener = new Listener();
        addWindowListener( listener );
        addComponentListener( listener );

        sidemenu.mute.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                setMuted( sidemenu.mute.isSelected() );
            }
        } );

        sidemenu.back.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                back();
            }
        } );

        sidemenu.exit.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                exit();
            }
        } );
    }
}
