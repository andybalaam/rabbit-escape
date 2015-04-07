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

import javax.swing.JPanel;
import javax.swing.JScrollBar;

import rabbitescape.engine.Token;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;

public class GameUi
{
    private class Listener extends EmptyListener implements MouseWheelListener
    {
        @Override
        public void windowClosing( WindowEvent e )
        {
            stopGameLoop();
        }

        @Override
        public void componentResized( ComponentEvent e )
        {
            zoomToFit();
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
                int units = e.getUnitsToScroll();

                if (
                       canvasScrollBarY.getVisibleAmount()
                    != canvasScrollBarY.getMaximum()
                )
                {
                    scrollScrollBarBy( canvasScrollBarY, units );
                }
                else
                {
                    scrollScrollBarBy( canvasScrollBarX, units );
                }
            }
        }

        private void scrollScrollBarBy( JScrollBar scrollBar, int units )
        {
            scrollBar.setValue(
                  scrollBar.getValue()
                + ( units * scrollBar.getUnitIncrement() / 2 )
            );
        }
    }

    private static final Color backgroundColor = Color.WHITE;
    private static int[] zoomValues = { 16, 24, 32, 48, 64, 96, 128 };

        // 32x32 is the lowest "reasonable" zoom size
    private static int MIN_AUTO_ZOOM_INDEX = 2;

    private final Container contentPane;
    private final JPanel middlePanel;
    private final Dimension buttonSizeInPixels;
    public Dimension worldSizeInPixels;
    private int worldTileSizeInPixels;
    private final Config uiConfig;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final MainJFrame frame;
    private final MenuUi menuUi;

    public final Canvas canvas;
    private Listener listener;

    private JScrollBar canvasScrollBarX;
    private JScrollBar canvasScrollBarY;
    private GameMenu menu;
    private TopBar topBar;

    private Token.Type chosenAbility;
    private SwingGameLoop gameLoop;

    // Modified in Swing event thread, read in game loop thread
    public int scrollX;
    public int scrollY;
    public int zoomIndex;

    public GameUi(
        Config uiConfig,
        BitmapCache<SwingBitmap> bitmapCache,
        MainJFrame frame,
        MenuUi menuUi
    )
    {
        this.uiConfig = uiConfig;
        this.bitmapCache = bitmapCache;
        this.frame = frame;
        this.menuUi = menuUi;

        this.contentPane = frame.getContentPane();
        this.middlePanel = new JPanel( new BorderLayout() );
        this.chosenAbility = null;
        this.gameLoop = null;

        this.buttonSizeInPixels = new Dimension( 32, 32 );
        this.zoomIndex = 2;
        this.worldTileSizeInPixels = zoomValues[zoomIndex];
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
        contentPane.add( middlePanel, BorderLayout.CENTER );

        Canvas canvas = initCanvas( middlePanel, worldSizeInPixels );

        frame.setBoundsFromConfig();

        frame.setTitle( t( "Rabbit Escape" ) );
        frame.pack();
        frame.setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );

        return canvas;
    }

    private Canvas initCanvas(
        Container contentPane, Dimension worldSizeInPixels )
    {
        Canvas canvas = new Canvas();
        canvas.setIgnoreRepaint( true );

        canvasScrollBarX = new JScrollBar( JScrollBar.HORIZONTAL );
        canvasScrollBarY = new JScrollBar( JScrollBar.VERTICAL );

        JPanel canvasPanel = new JPanel( new BorderLayout() );
        canvasPanel.add( canvas, BorderLayout.CENTER );
        canvasPanel.add( canvasScrollBarX, BorderLayout.SOUTH );
        canvasPanel.add( canvasScrollBarY, BorderLayout.EAST );

        contentPane.add( canvasPanel, BorderLayout.CENTER );

        return canvas;
    }

    private void zoomToFit()
    {
        // Start at MIN_AUTO_ZOOM_INDEX + 1 to enforce at least 32x32
        for ( int index = MIN_AUTO_ZOOM_INDEX + 1; index < zoomValues.length; ++index )
        {
            if ( zoomIndexTooBig( index ) )
            {
                zoomTo( index - 1 );
                return;
            }
        }
        zoomTo( zoomValues.length - 1 );
    }

    private boolean zoomIndexTooBig( int index )
    {
        int zoom = zoomValues[index];

        return (
               zoom * gameLoop.world.size.width > canvas.getWidth()
            || zoom * gameLoop.world.size.height > canvas.getHeight()
        );
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
        canvas.addMouseListener( listener );
        canvas.addMouseWheelListener( listener );
        frame.addComponentListener( listener );
        frame.addWindowListener( listener );
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

        menu.explodeAll.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                explodeAllClicked();
            }
        } );

        menu.zoomIn.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                zoomClicked( true );
            }
        } );

        menu.zoomOut.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                zoomClicked( false );
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
                gameLoop.world.setPaused( menu.pause.isSelected() );
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

    public void setGameLoop( SwingGameLoop gameLoop )
    {
        this.gameLoop = gameLoop;

        this.menu = new GameMenu(
            contentPane,
            bitmapCache,
            buttonSizeInPixels,
            uiConfig,
            backgroundColor,
            gameLoop.getAbilities()
        );

        this.topBar = new TopBar(
            backgroundColor,
            gameLoop.world.num_to_save,
            middlePanel
        );

        frame.pack();

        initListeners();

        zoomToFit();
    }

    public void setWorldSize(
        rabbitescape.engine.util.Dimension worldGridSize,
        int worldTileSizeInPixels
    )
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
        stopGameLoop();

        uninit();

        if ( menuUi != null )
        {
            menuUi.init();
        }
    }

    private void stopGameLoop()
    {
        if ( gameLoop != null )
        {
            gameLoop.stop();
            gameLoop.world.setPaused( false );
        }
    }

    private void uninit()
    {
        frame.getContentPane().removeAll();
        frame.removeComponentListener( listener );
        frame.removeWindowListener( listener );
    }

    private void explodeAllClicked()
    {
        switch ( gameLoop.world.completionState() )
        {
            case RUNNING:
            case PAUSED:
            {
                gameLoop.world.setReadyToExplodeAll( true );
                break;
            }
            case READY_TO_EXPLODE_ALL:
            {
                gameLoop.world.setPaused( false );
                gameLoop.world.changes.explodeAllRabbits();
                gameLoop.world.setReadyToExplodeAll( false );
                break;
            }
            default:
            {
                // Don't do anything if we've finished already
                break;
            }
        }
    }

    private void cancelExplodeAll()
    {
        gameLoop.world.setReadyToExplodeAll( false );
    }

    private void zoomClicked( boolean zoomIn )
    {
        if ( zoomIn )
        {
            if ( zoomIndex < zoomValues.length - 1 )
            {
                zoomTo( zoomIndex + 1 );
            }
        }
        else
        {
            if ( zoomIndex > 0 )
            {
                zoomTo( zoomIndex - 1 );
            }
        }
    }

    private void zoomTo( int zoomIndex )
    {
        this.zoomIndex = zoomIndex;

        int zoom = zoomValues[zoomIndex];

        double scrX = getScrollBarProportion( canvasScrollBarX );
        double scrY = getScrollBarProportion( canvasScrollBarY );

        gameLoop.renderer.tileSize = zoom;
        setWorldSize( gameLoop.world.size, zoom );

        setScrollBarFromProportion( canvasScrollBarX, scrX );
        setScrollBarFromProportion( canvasScrollBarY, scrY );
    }

    private static double getScrollBarProportion( JScrollBar scrollBar )
    {
        return (
              ( scrollBar.getValue() + ( scrollBar.getVisibleAmount() / 2 ) )
            / (double)scrollBar.getMaximum()
        );
    }

    private static void setScrollBarFromProportion(
        JScrollBar scrollBar, double proportion )
    {
        scrollBar.setValue(
            (int)(
                  ( scrollBar.getMaximum() * proportion )
                - ( scrollBar.getVisibleAmount() / 2 )
            )
        );
    }

    private void leaveIntro()
    {
        gameLoop.world.setIntro( false );
    }

    private void setMuted( boolean muted )
    {
        ConfigTools.setBool( uiConfig, CFG_MUTED, muted );
        uiConfig.save();
    }

    private void click( Point pixelPosition )
    {
        switch( gameLoop.world.completionState() )
        {
            case LOST:
            case WON:
            {
                exit();
                return;
            }
            case INTRO:
            {
                leaveIntro();
                return;
            }
            case READY_TO_EXPLODE_ALL:
            {
                cancelExplodeAll();
                return;
            }
            default:
            {
                // Continue to the normal click behaviour
            }
        }

        if ( chosenAbility == null )
        {
            return;
        }

        int gridX = ( pixelPosition.x - gameLoop.renderer.offsetX )
            / worldTileSizeInPixels;

        int gridY = ( pixelPosition.y - gameLoop.renderer.offsetY )
            / worldTileSizeInPixels;

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
