package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.ui.swing.SwingGameInit.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

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

    private static final Color backgroundColor = Color.WHITE;

    private final Config uiConfig;

    private final BitmapCache<SwingBitmap> bitmapCache;
    public final Canvas canvas;

    private SwingGameLoop gameLoop;

    public GameJFrame( Config uiConfig, BitmapCache<SwingBitmap> bitmapCache )
    {
        this.uiConfig = uiConfig;
        this.gameLoop = null;
        this.bitmapCache = bitmapCache;

        this.canvas = initUi();
        initListeners();
    }

    private Canvas initUi()
    {
        setIgnoreRepaint( true );

        Dimension buttonSizeInPixels = new Dimension( 32, 32 );
        Dimension worldSizeInPixels = new Dimension( 400, 200 );

        Container contentPane = getContentPane();
        Canvas canvas = initCanvas( contentPane, worldSizeInPixels );
        initButtons( contentPane, buttonSizeInPixels, worldSizeInPixels );

        setBoundsFromConfig();

        setTitle( t( "Rabbit Escape" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );

        return canvas;
    }

    private void initButtons(
        Container contentPane,
        Dimension buttonSizeInPixels,
        Dimension worldSizeInPixels
    )
    {
        LayoutManager layout = new FlowLayout( FlowLayout.CENTER, 4, 4 );
        JPanel panel = new JPanel( layout );
        panel.setBackground( backgroundColor );

        panel.setPreferredSize(
            new Dimension(
                buttonSizeInPixels.width + 8,
                worldSizeInPixels.height
            )
        );

        addToggleButton(
            panel,
            buttonSizeInPixels,
            "menu-unmuted",
            "menu-muted",
            ConfigTools.getBool( uiConfig, CFG_MUTED )
        );

        addToggleButton(
            panel, buttonSizeInPixels, "menu-pause", "menu-unpause", false );

        addSpacer( panel );

        addAbilitiesButtons( buttonSizeInPixels, panel );

        addSpacer( panel );

        addButton( panel, buttonSizeInPixels, "menu-exit" );

        JScrollPane scrollPane = new JScrollPane( panel );
        contentPane.add( scrollPane, BorderLayout.WEST );
    }

    private
        void
        addAbilitiesButtons( Dimension buttonSizeInPixels, JPanel panel )
    {
        ButtonGroup abilitiesGroup = new ButtonGroup();

        for ( String ability : new String[] { "bash", "dig" } )
        {
            String iconName = "ability-" + ability;

            JToggleButton button = addToggleButton(
                panel, buttonSizeInPixels, iconName, null, false );

            //abilityButtons.add( button, ability );
            abilitiesGroup.add( button );
        }
    }

    private void addSpacer( JPanel panel )
    {
        JPanel spacer = new JPanel();
        spacer.setBackground( backgroundColor );

        panel.add( spacer );
    }

    private JToggleButton addToggleButton(
        JPanel panel,
        Dimension buttonSizeInPixels,
        String unSelectedImage,
        String selectedImage,
        boolean selected
    )
    {
        JToggleButton button = new JToggleButton( getIcon( unSelectedImage ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );
        button.setSelected( selected );

        if ( selectedImage != null )
        {
            button.setSelectedIcon( getIcon( selectedImage ) );
        }

        panel.add( button );

        return button;
    }

    private JButton addButton(
        JPanel panel,
        Dimension buttonSizeInPixels,
        String image
    )
    {
        JButton button = new JButton( getIcon( image ) );

        button.setBackground( backgroundColor );
        button.setBorderPainted( false );

        panel.add( button );

        return button;
    }

    private ImageIcon getIcon( String name )
    {
        return new ImageIcon(
            bitmapCache.get(
                "/rabbitescape/ui/swing/images32/" + name + ".png" ).image
        );
    }

    private Canvas initCanvas(
        Container contentPane, Dimension worldSizeInPixels )
    {
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize( worldSizeInPixels );

        JScrollPane scrollPane = new JScrollPane( canvas );
        contentPane.add( scrollPane, BorderLayout.CENTER );

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
