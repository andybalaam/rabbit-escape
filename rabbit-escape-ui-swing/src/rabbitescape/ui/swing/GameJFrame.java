package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.ui.swing.SwingConfigSetup.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

public class GameJFrame extends JFrame
{
    private static final long serialVersionUID = 1L;

    private class Listener extends EmptyListener implements MouseWheelListener
    {
        @Override
        public void windowClosing( WindowEvent e )
        {
            exit();
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
            adjustScrollBars();
        }

        @Override
        public void mouseClicked( MouseEvent e )
        {
            click( e.getPoint() );
        }

        @Override
        public void mouseWheelMoved( MouseWheelEvent e )
        {
            if ( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL )
            {
                canvasScrollBarY.setValue(
                    canvasScrollBarY.getValue() + (
                          e.getUnitsToScroll()
                        * canvasScrollBarY.getUnitIncrement()
                    )
                );
            }
        }
    }

    private static final Color backgroundColor = Color.WHITE;

    private final Container contentPane;
    private final JPanel middlePanel;
    private final Dimension buttonSizeInPixels;
    private Dimension worldSizeInPixels;
    private int worldTileSizeInPixels;
    private final Config uiConfig;
    private final BitmapCache<SwingBitmap> bitmapCache;
    public final Canvas canvas;
    private JScrollBar canvasScrollBarX;
    private JScrollBar canvasScrollBarY;
    private GameMenu menu;
    private TopBar topBar;

    private Token.Type chosenAbility;
    private SwingGameLoop gameLoop;

    // Modified in Swing event thread, read in game loop thread
    public int scrollX;
    public int scrollY;

    public GameJFrame( Config uiConfig, BitmapCache<SwingBitmap> bitmapCache )
    {
        this.contentPane = getContentPane();
        this.middlePanel = new JPanel( new BorderLayout() );
        this.uiConfig = uiConfig;
        this.bitmapCache = bitmapCache;
        this.chosenAbility = null;
        this.gameLoop = null;

        this.buttonSizeInPixels = new Dimension( 32, 32 );
        this.worldTileSizeInPixels = 32;
        this.worldSizeInPixels = new Dimension( 400, 200 ); // Temporary guess

        this.scrollX = 0;
        this.scrollY = 0;

        this.canvas = initUi();
        adjustScrollBars();
        this.menu = null;
        this.topBar = null;
    }

    private Canvas initUi()
    {
        setIgnoreRepaint( true );

        contentPane.add( middlePanel, BorderLayout.CENTER );

        Canvas canvas = initCanvas( middlePanel, worldSizeInPixels );

        setBoundsFromConfig();

        setTitle( t( "Rabbit Escape" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );

        return canvas;
    }

    private Canvas initCanvas(
        Container contentPane, Dimension worldSizeInPixels )
    {
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize( worldSizeInPixels );

        canvasScrollBarX = new JScrollBar( JScrollBar.HORIZONTAL );
        canvasScrollBarY = new JScrollBar( JScrollBar.VERTICAL );

        JPanel canvasPanel = new JPanel( new BorderLayout() );
        canvasPanel.add( canvas, BorderLayout.CENTER );
        canvasPanel.add( canvasScrollBarX, BorderLayout.SOUTH );
        canvasPanel.add( canvasScrollBarY, BorderLayout.EAST );

        contentPane.add( canvasPanel, BorderLayout.CENTER );

        return canvas;
    }

    private void adjustScrollBars()
    {
        canvasScrollBarX.setMaximum( worldSizeInPixels.width );
        canvasScrollBarX.setVisibleAmount( canvas.getWidth() );
        canvasScrollBarX.setBlockIncrement( (int)( canvas.getWidth() * 0.9 ) );
        canvasScrollBarX.setUnitIncrement( worldTileSizeInPixels );

        canvasScrollBarY.setMaximum( worldSizeInPixels.height );
        canvasScrollBarY.setVisibleAmount( canvas.getHeight() );
        canvasScrollBarY.setBlockIncrement( (int)( canvas.getHeight() * 0.9 ) );
        canvasScrollBarY.setUnitIncrement( worldTileSizeInPixels );
    }

    private void initListeners()
    {
        Listener listener = new Listener();
        addWindowListener( listener );
        addComponentListener( listener );
        canvas.addMouseListener( listener );
        canvas.addMouseWheelListener( listener );
        gameLoop.addStatsChangedListener( this.topBar );

        menu.addAbilitiesListener( new GameMenu.AbilityChangedListener()
        {
            @Override
            public void abilityChosen( Token.Type ability )
            {
                chooseAbility( ability );
            }
        } );

        menu.back.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                exit();
            }
        } );

        menu.mute.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                setMuted( menu.mute.isSelected() );
            }
        } );

        menu.pause.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                setPaused( menu.pause.isSelected() );
            }
        } );

        canvasScrollBarX.addAdjustmentListener(
            new AdjustmentListener()
            {
                @Override
                public void adjustmentValueChanged( AdjustmentEvent e )
                {
                    scrollX = e.getValue();
                }
            }
        );

        canvasScrollBarY.addAdjustmentListener(
            new AdjustmentListener()
            {
                @Override
                public void adjustmentValueChanged( AdjustmentEvent e )
                {
                    scrollY = e.getValue();
                }
            }
        );
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

        this.menu = new GameMenu(
            contentPane,
            bitmapCache,
            buttonSizeInPixels,
            worldSizeInPixels,
            uiConfig,
            backgroundColor,
            gameLoop.getAbilities()
        );

        this.topBar = new TopBar(
            backgroundColor,
            gameLoop.world.num_to_save,
            middlePanel
        );

        pack();

        initListeners();
    }

    public void setWorldSize(
        Dimension worldGridSize, int worldTileSizeInPixels )
    {
        this.worldSizeInPixels = new Dimension(
            worldGridSize.width * worldTileSizeInPixels,
            worldGridSize.height * worldTileSizeInPixels
        );

        this.worldTileSizeInPixels = worldTileSizeInPixels;

        canvas.setPreferredSize( worldSizeInPixels );
        adjustScrollBars();
    }

    private void exit()
    {
        if ( gameLoop != null )
        {
            gameLoop.stop();
            gameLoop.setPaused( false );
        }

        dispose();
    }

    private void setMuted( boolean muted )
    {
        ConfigTools.setBool( uiConfig, CFG_MUTED, muted );
        uiConfig.save();
    }

    private void setPaused( boolean paused )
    {
        gameLoop.setPaused( paused );
    }

    private void click( Point pixelPosition )
    {
        if ( gameLoop.world.completionState() != World.CompletionState.RUNNING )
        {
            exit();
        }

        if ( chosenAbility == null )
        {
            return;
        }

        int gridX = ( pixelPosition.x + scrollX ) / worldTileSizeInPixels;
        int gridY = ( pixelPosition.y + scrollY ) / worldTileSizeInPixels;

        int numLeft = gameLoop.addToken( chosenAbility, gridX, gridY );

        if ( numLeft == 0 )
        {
            menu.abilities.get( chosenAbility ).setEnabled( false );
        }

        updateChosenAbility();
    }

    protected void chooseAbility( Token.Type ability )
    {
        chosenAbility = ability;

        updateChosenAbility();
    }

    private void updateChosenAbility()
    {
        topBar.abilityChanged(
            chosenAbility, gameLoop.world.abilities.get( chosenAbility ) );
    }
}
