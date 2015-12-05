package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Util;
import rabbitescape.render.*;
import rabbitescape.render.Frame;
import rabbitescape.render.Renderer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.*;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.t;
import static rabbitescape.render.AnimationLoader.*;

public class AnimationTester extends JFrame
{
    private static class SwingBitmapAndOffset
    {
        public final ScaledBitmap<SwingBitmap> bitmap;
        public final int offsetX;
        public final int offsetY;
        public final String soundEffect;
        public final String frameName;

        public SwingBitmapAndOffset(
            ScaledBitmap<SwingBitmap> bitmap,
            int offsetX,
            int offsetY,
            String soundEffect, 
            String frameName
        )
        {
            this.bitmap = bitmap;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.soundEffect = soundEffect;
            this.frameName = frameName;
        }
    }

    private static class SwingAnimation
    {
        private final List<SwingBitmapAndOffset> bitmaps;

        public SwingAnimation(
            BitmapCache<SwingBitmap> bitmapCache, Animation animation )
        {
            this.bitmaps = new ArrayList<>();

            for ( Frame frame : animation )
            {
                ScaledBitmap<SwingBitmap> bmp = bitmapCache.get( frame.name );

                this.bitmaps.add(
                    new SwingBitmapAndOffset(
                        bmp, frame.offsetX, frame.offsetY, frame.soundEffect, frame.name )
                );
            }
        }

        public SwingBitmapAndOffset get( int frameNum )
        {
            return bitmaps.get( frameNum );
        }
    }

    private static final long serialVersionUID = 1L;

    private static final String CONFIG_PATH =
        "~/.rabbitescape/config/animationtester.properties"
            .replace( "~", System.getProperty( "user.home" ) );

    private static final String CFG_AT_WINDOW_LEFT =
        "animationtester.window.left";

    private static final String CFG_AT_WINDOW_TOP =
        "animationtester.window.top";

    private static final String CFG_AT_TILE_SIZE = "animationtester.tile.size";

    private static final String CFG_AT_ANIMATIONS =
        "animationtester.animations";

    private static final String CFG_AT_BLOCKS = "animationtester.blocks";

    private static final String[][] defaultAnimationNames = new String[][] {
        new String[] { NONE, NONE, NONE },
        new String[] { NONE, NONE, NONE },
        new String[] { NONE, NONE, NONE },

        new String[] { "rabbit_walking_right", NONE, NONE },
        new String[] { NONE, "rabbit_bashing_right", "rabbit_walking_right" },
        new String[] { NONE, NONE, NONE },

        new String[] { NONE, NONE, NONE },
        new String[] { NONE, NONE, NONE },
        new String[] { NONE, NONE, NONE }
    };

    private static final String[] defaultBlockNames = new String[] {
        NONE, NONE, NONE,
        "land_rising_left_1", NONE, "bridge_rising_right",
        "land_block_1", "land_block_2", "land_block_3",
    };

    private class Listener extends EmptyListener
    {
        @Override
        public void mouseClicked( MouseEvent mouseEvent )
        {
            int i = screen2index( mouseEvent.getX(), mouseEvent.getY() );

            String[] possibilties = animationCache.listAll();

            JPanel dropDowns = new JPanel();
            JList<String> list0 = addList(
                dropDowns, possibilties, animationNames[ i ][ 0 ]
            );

            JList<String> list1 = addList(
                dropDowns, possibilties, animationNames[ i ][ 1 ]
            );

            JList<String> list2 = addList(
                dropDowns, possibilties, animationNames[ i ][ 2 ]
            );

            JList<String> blocksList = addList(
                dropDowns, allBlocks, blockNames[ i ]
            );

            JScrollPane scrollPane = new JScrollPane( dropDowns );
            scrollPane.setPreferredSize( new Dimension( 800, 500 ) );

            int retVal = JOptionPane.showOptionDialog(
                AnimationTester.this,
                scrollPane,
                "Change animation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null
            );

            if ( retVal == JOptionPane.CANCEL_OPTION )
            {
                return;
            }

            animationNames[i][0] = noneForNull( list0.getSelectedValue() );
            animationNames[i][1] = noneForNull( list1.getSelectedValue() );
            animationNames[i][2] = noneForNull( list2.getSelectedValue() );
            blockNames[i] = noneForNull( blocksList.getSelectedValue() );

            saveSelectionsToConfig();
            loadBitmaps();
        }

        private void saveSelectionsToConfig()
        {
            atConfig.set(
                CFG_AT_ANIMATIONS, animationsToConfigString( animationNames ) );

            atConfig.set( CFG_AT_BLOCKS, blocksToConfigString( blockNames ) );

            atConfig.save();
        }

        private String noneForNull( String value )
        {
            if ( value == null )
            {
                return NONE;
            }
            else
            {
                return value;
            }
        }

        private JList<String> addList(
            JPanel parent, String[] possibilities, String animation
        )
        {
            JList<String> list = new JList<>( possibilities );

            parent.add( list );

            list.setSelectedValue( animation, true );

            return list;
        }

        @Override
        public void componentMoved( ComponentEvent arg0 )
        {
            ConfigTools.setInt( atConfig, CFG_AT_WINDOW_LEFT, getX() );
            ConfigTools.setInt( atConfig, CFG_AT_WINDOW_TOP,  getY() );
            atConfig.save();
        }

        @Override
        public void keyPressed( KeyEvent e )
        {
            switch( e.getKeyCode() )
            {
            case KeyEvent.VK_RIGHT: // Speed up
                if( msFrameLength > 50 )
                {
                    msFrameLength -= 50;
                }
                return;
            case KeyEvent.VK_LEFT: // Slow
                msFrameLength += 50;
                return;
            case KeyEvent.VK_H:
                System.out.println("");
                System.out.println("Right arrow  Speed up");
                System.out.println("Left arrow   Slow down");
                System.out.println("H            Print this help");
                System.out.println("L            Toggle printing log of frames");
                System.out.println("Q            Quit");
                return;
            case KeyEvent.VK_L:
                frameLogging = !frameLogging;
                return;
            case KeyEvent.VK_Q:
                System.exit( 0 );
            default:
                // Ignore fat fingers
            }
            
        }

    }

    private boolean frameLogging = false;
    private int msFrameLength = 100 ;
    private final int tileSize;
    private final int numTilesX;
    boolean running;

    private final java.awt.Canvas canvas;
    private final Config atConfig;
    private SwingBitmapScaler scaler;
    private SwingPaint paint;
    /** [0-8][0-2] position in 3x3 grid, and triplet of consecutive animations for 
     * that position.
     */
    private SwingAnimation[][] frames;
    private List<ScaledBitmap<SwingBitmap>> blockBitmaps;
    private final AnimationCache animationCache;
    private final String[] allBlocks = new String[] {
        NONE,
        "land_block_1",
        "land_block_2",
        "land_block_3",
        "land_block_4",
        "land_rising_right_1",
        "land_rising_right_2",
        "land_rising_right_3",
        "land_rising_right_4",
        "land_rising_left_1",
        "land_rising_left_2",
        "land_rising_left_3",
        "land_rising_left_4",
        "bridge_rising_right",
        "bridge_rising_left",
    };

    private final String[][] animationNames;
    private final String[] blockNames;

    private static class InitUi implements Runnable
    {
        public AnimationTester animationTester;

        @Override
        public void run() {
            try
            {
                animationTester = new AnimationTester( createConfig() );
                synchronized ( this )
                {
                    notifyAll();
                }
            }
            catch ( Throwable t )
            {
                synchronized ( this )
                {
                    notifyAll();
                }
                t.printStackTrace();
                System.exit( 3 );
            }
        }

        public synchronized void loopWhenReady()
        {
            try
            {
                wait();
            }
            catch ( InterruptedException e )
            {
                // Will come when notified
            }

            if ( animationTester != null )
            {
                animationTester.loop();
            }
        }
    }

    public AnimationTester( Config atConfig )
    {
        this.atConfig = atConfig;
        this.tileSize = ConfigTools.getInt( atConfig, CFG_AT_TILE_SIZE );
        this.numTilesX = 3;
        this.animationCache = new AnimationCache( new AnimationLoader() );

        /** Name of .rea files (changed to caps it's also the name of the state) */
        this.animationNames = animationsFromConfig(
            atConfig.get( CFG_AT_ANIMATIONS ) );

        this.blockNames = blocksFromConfig(
            atConfig.get( CFG_AT_BLOCKS ) );

        int numTilesY = 3;

        setIgnoreRepaint( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        this.canvas = new java.awt.Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize(
            new java.awt.Dimension(
                tileSize * numTilesX, tileSize * numTilesY
            )
        );

        getContentPane().add( canvas, java.awt.BorderLayout.CENTER );

        loadBitmaps();

        Listener listener = new Listener();
        canvas.addMouseListener( listener );
        addKeyListener( listener );
        addComponentListener( listener );

        setBoundsFromConfig();

        setTitle( t( "Animation Tester" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );
    }

    private void setBoundsFromConfig()
    {
        int x = ConfigTools.getInt( atConfig, CFG_AT_WINDOW_LEFT );
        int y = ConfigTools.getInt( atConfig, CFG_AT_WINDOW_TOP );

        if ( x != Integer.MIN_VALUE && y != Integer.MIN_VALUE )
        {
            setLocation( x, y );
        }
    }

    private void loadBitmaps()
    {
        scaler = new SwingBitmapScaler();
        paint = new SwingPaint( null );
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<SwingBitmap>(
            bitmapLoader, scaler, 500 );

        frames = loadAllFrames( bitmapCache, animationNames );
        blockBitmaps = loadAllBlockBitmaps( bitmapCache, blockNames );
    }

    private SwingAnimation[][] loadAllFrames(
        BitmapCache<SwingBitmap> bitmapCache, String[][] animationNames )
    {
        SwingAnimation[][] ret =
            new SwingAnimation[animationNames.length][];

        int i = 0;
        for ( String[] animationTriplet : animationNames )
        {
            ret[i] = new SwingAnimation[animationTriplet.length];
            int j = 0;
            for ( String animationName : animationTriplet )
            {
                if ( !animationName.equals( NONE ) )
                {
                    Animation animation = animationCache.get( animationName );
                    if ( animation == null )
                    {
                        ++j;
                        continue;
                    }

                    ret[i][j] = new SwingAnimation(
                        bitmapCache,
                        animation
                    );
                }
                ++j;
            }
            ++i;
        }
        return ret;
    }


    private List<ScaledBitmap<SwingBitmap>> loadAllBlockBitmaps(
        BitmapCache<SwingBitmap> bitmapCache, String[] blockNames )
    {
        List<ScaledBitmap<SwingBitmap>> ret =
            new ArrayList<ScaledBitmap<SwingBitmap>>( blockNames.length );

        for ( String blockName : blockNames )
        {
            if ( blockName.equals( NONE ) )
            {
                ret.add( null );
            }
            else
            {
                try
                {
                    ret.add( bitmapCache.get( blockName ) );
                }
                catch ( FailedToLoadImage e )
                {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static void main( String[] args )
    {
        try
        {
            InitUi initUi = new InitUi();
            SwingUtilities.invokeLater( initUi );
            initUi.loopWhenReady();
        }
        catch( Throwable t )
        {
            t.printStackTrace();
            System.exit( 2 );
        }
    }

    private void loop()
    {
        BufferStrategy strategy = canvas.getBufferStrategy();

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 0, 0, tileSize );

        SoundPlayer<SwingBitmap> soundPlayer =
            new SoundPlayer<SwingBitmap>( new SwingSound( false ) );

        int frameSetNum = 0;
        int frameNum = 0;
        running = true;
        while( running && this.isVisible() )
        {
            new DrawFrame(
                strategy, renderer, soundPlayer, frameSetNum, frameNum ).run();

            pause();

            ++frameNum;
            if ( frameNum == 10 )
            {
                frameNum = 0;
                ++frameSetNum;
                if ( frameSetNum == 3 )
                {
                    frameSetNum = 0;
                }
            }
        }
    }

    private class DrawFrame extends BufferedDraw
    {
        private final int frameSetNum;
        private final int frameNum;
        private final Renderer<SwingBitmap, SwingPaint> renderer;
        private final SoundPlayer<SwingBitmap> soundPlayer;

        public DrawFrame(
            BufferStrategy strategy,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SoundPlayer<SwingBitmap> soundPlayer,
            int frameSetNum,
            int frameNum
        )
        {
            super( strategy );
            this.renderer = renderer;
            this.soundPlayer = soundPlayer;
            this.frameSetNum = frameSetNum;
            this.frameNum = frameNum;
        }

        @Override
        void draw( java.awt.Graphics2D g )
        {
            g.setPaint( java.awt.Color.WHITE );
            g.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
            );

            List<Sprite<SwingBitmap>> sprites = new ArrayList<>();
            int i = 0;
            for ( ScaledBitmap<SwingBitmap> bmp : blockBitmaps )
            {
                if ( bmp == null )
                {
                    ++i;
                    continue;
                }

                java.awt.Point loc = int2dim( i );
                sprites.add(
                    new Sprite<SwingBitmap>(
                        bmp,
                        null,
                        loc.x,
                        loc.y,
                        0,
                        0
                    )
                );
                ++i;
            }

            i = 0;
            for ( SwingAnimation[] bmpSets : frames )
            {
                SwingAnimation bmps = bmpSets[frameSetNum];
                if ( bmps == null )
                {
                    ++i;
                    continue;
                }
                java.awt.Point loc = int2dim( i );
                SwingBitmapAndOffset bmp = bmps.get( frameNum );
                Sprite<SwingBitmap> sprite = new Sprite<SwingBitmap>(
                    bmp.bitmap,
                    bmp.soundEffect,
                    loc.x,
                    loc.y,
                    bmp.offsetX,
                    bmp.offsetY
                );
                
                if( frameLogging ){
                    System.out.println(bmp.frameName);
                }

                sprites.add( sprite );
                ++i;
            }

            soundPlayer.play( sprites );

            renderer.render(
                new SwingCanvas( g, canvas.getWidth(), canvas.getHeight() ),
                sprites,
                paint
            );
        }
    }

    private java.awt.Point int2dim( int i )
    {
        return new java.awt.Point( i % 3, i / 3 );
    }

    private int screen2index( int x, int y )
    {
        return ( numTilesX * ( y / tileSize ) ) + ( x / tileSize );
    }

    private void pause()
    {
        try
        {
            Thread.sleep( msFrameLength );
        }
        catch ( InterruptedException e )
        {
            // Ignore
        }
    }

    private static Config createConfig()
    {
        Config.Definition definition = new Config.Definition();

        definition.set(
            CFG_AT_WINDOW_LEFT,
            String.valueOf( Integer.MIN_VALUE ),
            "The x position of the animation tester window on the screen"
        );

        definition.set(
            CFG_AT_WINDOW_TOP,
            String.valueOf( Integer.MIN_VALUE ),
            "The y position of the animation tester window on the screen"
        );

        definition.set(
            CFG_AT_TILE_SIZE,
            String.valueOf( 128 ),
            "The on-screen size of tiles in the animation tester in pixels"
        );

        definition.set(
            CFG_AT_ANIMATIONS,
            "",
            "The animations selected to play in the animation tester"
            + " (3 per tile)"
        );

        definition.set(
            CFG_AT_BLOCKS,
            "",
            "The blocks selected to play in the animation tester"
        );

        return new Config( definition, new RealFileSystem(), CONFIG_PATH );
    }

    private static String animationsToConfigString( String[][] animations )
    {
        StringBuilder ret = new StringBuilder();

        for ( String[] arr : animations )
        {
            for ( String anim : arr )
            {
                ret.append( anim );
                ret.append( ' ' );
            }
        }

        return ret.toString();
    }

    private static String blocksToConfigString( String[] blocks )
    {
        return join( " ", blocks );
    }

    private static String[][] animationsFromConfig( String cfgEntry )
    {
        if ( Util.isEmpty( cfgEntry ) )
        {
            return defaultAnimationNames;
        }

        String[] items = cfgEntry.split( " " );

        if ( items.length % 3 != 0 )
        {
            return defaultAnimationNames;
        }

        String[][] ret = new String[items.length / 3][];

        for ( int i = 0; i < items.length / 3; ++i )
        {
            ret[i] = new String[3];
            ret[i][0] = items[ i * 3 ];
            ret[i][1] = items[ i * 3 + 1 ];
            ret[i][2] = items[ i * 3 + 2 ];
        }

        return ret;
    }

    private static String[] blocksFromConfig( String cfgEntry )
    {
        if ( Util.isEmpty( cfgEntry ) )
        {
            return defaultBlockNames;
        }

        return cfgEntry.split( " " );
    }
}
