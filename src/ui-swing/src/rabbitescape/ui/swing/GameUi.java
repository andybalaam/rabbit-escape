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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import rabbitescape.engine.Token;
import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.TapTimer;
import rabbitescape.engine.solution.SelectAction;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.gameloop.Physics.StatsChangedListener;

public class GameUi implements StatsChangedListener
{
    private class Listener extends EmptyListener implements MouseWheelListener
    {
        private int startX = -1;
        private int startY = -1;
        private long msTimePress = 0;
        /** Time in ms. Longer press-release intervals are
         * interpreted as drags */
        private final long msClickThreshold =
            ConfigTools.getInt( uiConfig, CFG_CLICK_THRESHOLD_MS );

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
        public void mousePressed( MouseEvent e )
        {
            TapTimer.newTap();
            if ( noScrollRequired() )
            {
                click( e.getPoint() );
                return;
            }
            msTimePress = System.currentTimeMillis();
            startX  = e.getX();
            startY  = e.getY();
        }

        @Override
        public void mouseReleased( MouseEvent e )
        {
            long msDownTime = System.currentTimeMillis() - msTimePress;
            if ( msDownTime < msClickThreshold )
            {
                click( e.getPoint() );
            }
        }

        @Override
        public void mouseClicked( MouseEvent e )
        {
            // use pressed and released calls.
            // if this was used too, would get double event calls.
        }

        @Override
        public void mouseDragged( MouseEvent e )
        {
            long msDownTime = System.currentTimeMillis() - msTimePress;
            if ( msDownTime < msClickThreshold )
            { // Wait and see if this is a click or a drag
                return;
            }

            canvasScrollBarX.setValue(
                canvasScrollBarX.getValue() + startX - e.getX() );

            canvasScrollBarY.setValue(
                canvasScrollBarY.getValue() + startY - e.getY() );

            startX = e.getX();
            startY = e.getY();
        }

        /**
         * @return true if the window is large such that the whole world is
         *         visible.
         */
        public boolean noScrollRequired()
        {
            int xRange = canvasScrollBarX.getMaximum() -
                         canvasScrollBarX.getMinimum();
            int yRange = canvasScrollBarY.getMaximum() -
                         canvasScrollBarY.getMinimum();
            return ( canvasScrollBarX.getVisibleAmount() == xRange )
                && ( canvasScrollBarY.getVisibleAmount() == yRange );
        }

        @Override
        public void mouseWheelMoved( MouseWheelEvent e )
        {
            if ( e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL )
            {
                if ( e.isControlDown() )
                {
                    zoomClicked( e.getUnitsToScroll() < 0 );
                }
                else
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
        }

        private void scrollScrollBarBy( JScrollBar scrollBar, int units )
        {
            scrollBar.setValue(
                  scrollBar.getValue()
                + ( units * scrollBar.getUnitIncrement() / 2 )
            );
        }

        @Override
        public void keyPressed( KeyEvent e )
        {
            switch ( e.getKeyCode() )
            {
                case KeyEvent.VK_LEFT:
                {
                    scrollScrollBarBy( canvasScrollBarX, -1 );
                    break;
                }
                case KeyEvent.VK_RIGHT:
                {
                    scrollScrollBarBy( canvasScrollBarX, 1 );
                    break;
                }
                case KeyEvent.VK_UP:
                {
                    scrollScrollBarBy( canvasScrollBarY, -1 );
                    break;
                }
                case KeyEvent.VK_DOWN:
                {
                    scrollScrollBarBy( canvasScrollBarY, 1 );
                    break;
                }
            }
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
    private SwingGameLaunch gameLaunch;

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
        this.gameLaunch = null;

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
        for ( int index = MIN_AUTO_ZOOM_INDEX + 1;
              index < zoomValues.length;
              ++index )
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
               zoom * gameLaunch.world.size.width > canvas.getWidth()
            || zoom * gameLaunch.world.size.height > canvas.getHeight()
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
        listener = new Listener();
        canvas.addKeyListener( listener );
        canvas.addMouseListener( listener );
        canvas.addMouseWheelListener( listener );
        canvas.addMouseMotionListener( listener );
        frame.addComponentListener( listener );
        frame.addWindowListener( listener );
        frame.addKeyListener( listener );
        gameLaunch.addStatsChangedListener( this.topBar );
        gameLaunch.addStatsChangedListener( this );

        menu.addAbilitiesListener( new GameMenu.AbilityChangedListener()
        {
            @Override
            public void abilityChosen( Token.Type ability )
            {
                chooseAbility( ability );
                gameLaunch.solutionRecorder.append(
                    new SelectAction( ability ) );
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

        MenuTools.clickOnKey( menu.back, "back", KeyEvent.VK_ESCAPE );

        menu.explodeAll.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                explodeAllClicked();
            }
        } );

        MenuTools.clickOnKey( menu.explodeAll, "explode_all", KeyEvent.VK_X );

        menu.zoomIn.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                zoomClicked( true );
            }
        } );

        MenuTools.clickOnKey(
            menu.zoomIn,
            "zoom_in",
            KeyStroke.getKeyStroke( KeyEvent.VK_EQUALS, 0 ),
            KeyStroke.getKeyStroke( KeyEvent.VK_EQUALS,
                java.awt.event.InputEvent.SHIFT_DOWN_MASK ),
            KeyStroke.getKeyStroke( KeyEvent.VK_EQUALS,
                java.awt.event.InputEvent.CTRL_DOWN_MASK ),
            KeyStroke.getKeyStroke(
                KeyEvent.VK_EQUALS,
                  java.awt.event.InputEvent.CTRL_DOWN_MASK
                | java.awt.event.InputEvent.SHIFT_DOWN_MASK
            )
        );

        menu.zoomOut.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                zoomClicked( false );
            }
        } );

        MenuTools.clickOnKey(
            menu.zoomOut,
            "zoom_out",
            KeyStroke.getKeyStroke( KeyEvent.VK_MINUS, 0 ),
            KeyStroke.getKeyStroke( KeyEvent.VK_MINUS,
                java.awt.event.InputEvent.CTRL_DOWN_MASK )
        );

        menu.mute.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                setMuted( menu.mute.isSelected() );
            }
        } );

        MenuTools.clickOnKey( menu.mute, "mute", KeyEvent.VK_M );

        menu.pause.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                gameLaunch.world.setPaused( menu.pause.isSelected() );
            }
        } );

        MenuTools.clickOnKey( menu.pause, "pause", KeyEvent.VK_P );

        menu.speed.addActionListener( new ActionListener()
        {
            @Override
            public void actionPerformed( ActionEvent evt )
            {
                gameLaunch.toggleSpeed();
            }
        } );

        MenuTools.clickOnKey( menu.speed, "speed up", KeyEvent.VK_S );

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

    public void setGameLaunch( SwingGameLaunch gameLaunch )
    {
        this.gameLaunch = gameLaunch;

        this.menu = new GameMenu(
            contentPane,
            bitmapCache,
            buttonSizeInPixels,
            uiConfig,
            backgroundColor,
            gameLaunch.getAbilities()
        );

        this.topBar = new TopBar(
            backgroundColor,
            gameLaunch.world.num_to_save,
            middlePanel,
            gameLaunch.world.name
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

    public void exit()
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
        if ( gameLaunch != null )
        {
            gameLaunch.stop();
            gameLaunch.world.setPaused( false );
        }
    }

    private void uninit()
    {
        frame.getContentPane().removeAll();
        frame.removeComponentListener( listener );
        frame.removeWindowListener( listener );
        frame.removeKeyListener( listener );
    }

    private void explodeAllClicked()
    {
        switch ( gameLaunch.world.completionState() )
        {
            case RUNNING:
            case PAUSED:
            {
                gameLaunch.checkExplodeAll();
                break;
            }
            default:
            {
                // Don't do anything if we've finished already
                break;
            }
        }
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

        gameLaunch.graphics.setTileSize( zoom );
        setWorldSize( gameLaunch.world.size, zoom );

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

    public boolean getMuted()
    {
        return ConfigTools.getBool( uiConfig, CFG_MUTED );
    }

    private void setMuted( boolean muted )
    {
        ConfigTools.setBool( uiConfig, CFG_MUTED, muted );
        uiConfig.save();

        gameLaunch.graphics.setMuted( muted );
    }

    private Point pixelToCell( Point pixelPosition )
    {
        return new Point(
            ( pixelPosition.x - gameLaunch.graphics.renderer.offsetX )
              / worldTileSizeInPixels,
            ( pixelPosition.y - gameLaunch.graphics.renderer.offsetY )
              / worldTileSizeInPixels
        );
    }

    private void click( Point pixelPosition )
    {
        Point p = pixelToCell( pixelPosition );

        addToken( p.x , p.y );
    }

    protected void addToken(int tileX, int tileY )
    {
        if ( chosenAbility == null )
        {
            return;
        }

        int numLeft = gameLaunch.addToken( tileX, tileY, chosenAbility );
        menu.abilities.get( chosenAbility ).setNumLeft( numLeft );

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
            chosenAbility, gameLaunch.world.abilities.get( chosenAbility ) );
    }

    @Override
    public void changed( int waiting, int out, int saved )
    {
        switch ( gameLaunch.world.completionState() )
        {
            case WON:
            case LOST:
            {
                gameLaunch.stop();
                break;
            }
            default:
            {
                break;
            }
        }
    }
}
