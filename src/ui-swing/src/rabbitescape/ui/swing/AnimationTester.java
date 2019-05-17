package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigFile;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.engine.config.ConfigSchema;
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
    public enum Mode {
        RUN,
        STEP,
        FRAME_DUMP
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

    private static final String[][] defaultAnimationNames = new String[][]
    {
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

    private static final String[] defaultBlockNames = new String[]
    {
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
                dropDowns, 
                possibilties, 
                animationNames[ i ][ 0 ]
            );

            JList<String> list1 = addList(
                dropDowns, 
                possibilties, 
                animationNames[ i ][ 1 ]
            );

            JList<String> list2 = addList(
                dropDowns, 
                possibilties, 
                animationNames[ i ][ 2 ]
            );

            JList<String> blocksList = addList(
                dropDowns, 
                allBlocks, 
                blockNames[ i ]
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

            canvas.requestFocus();

            if ( retVal == JOptionPane.CANCEL_OPTION )
            {
                return;
            }

            animationNames[i][0] = noneForNull( list0.getSelectedValue() );
            animationNames[i][1] = noneForNull( list1.getSelectedValue() );
            animationNames[i][2] = noneForNull( list2.getSelectedValue() );
            blockNames[i] = noneForNull( blocksList.getSelectedValue() );

            saveSelectionsToConfig();

            canvas.requestFocus();
        }

        private void saveSelectionsToConfig()
        {
            atConfig.set(
                CFG_AT_ANIMATIONS, 
                animationsToConfigString( animationNames ) 
            );

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
            JPanel parent, 
            String[] possibilities, 
            String animation
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
            case KeyEvent.VK_A:
                backwardStep = true;
                return;
            case KeyEvent.VK_D:
                forwardStep = true;
                return;
            case KeyEvent.VK_H:
                String s =
                    "" + "\n" +
                    "Right arrow  Speed up" + "\n" +
                    "Left arrow   Slow down" + "\n" +
                    "A            In step mode, back one frame" + "\n" +
                    "D            In step mode, forward one frame" + "\n" +
                    "[CTRL]+X     Clear all animations" + "\n" +
                    "H            Print this help" + "\n" +
                    "L            Toggle printing log of frames" + "\n" +
                    "S            Toggle step mode" + "\n" +
                    "Q            Quit" + "\n" +
                    "F5           Dump 30 frames to png" + "\n" +
                    "F6           After creating pngs, animate them" + "\n" +
                    "             Requires ImageMagick" ;
                System.out.println( s );
                return;
            case KeyEvent.VK_L:
                frameLogging = !frameLogging;
                return;
            case KeyEvent.VK_S:
                switch ( runMode )
                {
                case RUN:
                    runMode = Mode.STEP;
                    return;
                case STEP:
                    runMode = Mode.RUN;
                    return;
                default:
                    return;
                }
            case KeyEvent.VK_X:
                if ( e.isControlDown() )
                {
                    for ( int i = 0 ; i < 9 ; i++ )
                    {
                        for ( int j = 0 ; j < 3 ; j++ )
                        {
                            animationNames[i][j] = NONE;
                        }
                    }

                    saveSelectionsToConfig();
                }
                return;
            case KeyEvent.VK_F5:
                runMode = Mode.FRAME_DUMP;
                return;
            case KeyEvent.VK_F6:
                frameDumper.framesToGif();
                return;
            case KeyEvent.VK_Q:
                System.exit( 0 );
                // return should not be necessary.
                // Gets rid of intermittent compiler warning
                return;
            default:
                // Ignore fat fingers
            }

        }

    }

    private Mode runMode = Mode.RUN;
    private FrameCounter firstFrameDumped = null;
    private FrameDumper frameDumper = null ;
    private boolean forwardStep = false;
    private boolean backwardStep = false;
    private boolean frameLogging = false;
    private int msFrameLength = 100 ;
    private final int tileSize;
    private final int numTilesX;
    boolean running;

    private final java.awt.Canvas canvas;
    private final Config atConfig;
    private final SwingPaint paint;
    private final BitmapCache<SwingBitmap> bitmapCache;
    private final AnimationCache animationCache;
    private final String[] allBlocks = new String[]
    {
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

    /** [0-8][0-2] position in 3x3 grid, and triplet of
     * consecutive animations for that position.
     */
    private final String[][] animationNames;

    /**
     * Blocks don't animate, so just [0-8], one for each position.
     */
    private final String[] blockNames;

    private static class InitUi implements Runnable
    {
        public AnimationTester animationTester;

        @Override
        public void run()
        {
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

        /** Name of .rea files
         * (changed to caps it's also the name of the state) */
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

        paint = new SwingPaint( null );

        bitmapCache = new BitmapCache<SwingBitmap>(
            new SwingBitmapLoader(),
            new SwingBitmapScaler(),
            SwingMain.cacheSize()
        );

        Listener listener = new Listener();
        canvas.addMouseListener( listener );
        canvas.addKeyListener( listener );
        addComponentListener( listener );

        setBoundsFromConfig();

        setIcon();

        setTitle( t( "Animation Tester" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );
        canvas.requestFocus();
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

    private class FrameCounter
    {
        private int frameSetNum ;
        private int frameNum ;

        public FrameCounter( FrameCounter f )
        {
            this.frameSetNum = f.frameSetNum;
            this.frameNum = f.frameNum;
        }

        public FrameCounter()
        {
            frameSetNum = 0;
            frameNum = 0;
        }

        @Override
        public int hashCode()
        {
            return 31 * frameSetNum + frameNum;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( null == o )
            {
                return false;
            }
            if ( !(o instanceof FrameCounter ) )
            {
                return false;
            }
            FrameCounter f = (FrameCounter)o;
            return this.frameSetNum == f.frameSetNum &&
                   this.frameNum == f.frameNum ;
        }

        public void inc()
        {
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

        public void dec()
        {
            --frameNum;
            if( frameNum == 0)
            {
                frameNum = 9;
                --frameSetNum;
                if( frameSetNum == 0 )
                {
                    frameSetNum = 2;
                }
            }
        }

        public int getFrameNum()
        {
            return frameNum;
        }

        public int getFrameSetNum()
        {
            return frameSetNum;
        }
    }

    private void loop()
    {
        BufferStrategy strategy = canvas.getBufferStrategy();

        Renderer<SwingBitmap, SwingPaint> renderer =
            new Renderer<SwingBitmap, SwingPaint>( 0, 0, tileSize,
                                                   bitmapCache );

        SoundPlayer soundPlayer =
            new SoundPlayer( SwingSound.create( false ) );

        FrameCounter counter = new FrameCounter();
        running = true;
        while( running && this.isVisible() )
        {
            DrawFrame drawFrame = new DrawFrame(
                strategy, renderer, soundPlayer, counter.getFrameSetNum(),
                counter.getFrameNum() );
            drawFrame.run();

            switch ( runMode )
            {
            case STEP:
                counter = keyStep(counter);
                continue;
            case RUN:
                counter = runStep( counter );
                continue;
            case FRAME_DUMP:
                counter = frameDumpStep( counter, drawFrame );
                continue;
            }
        }
    }

    private FrameCounter keyStep( FrameCounter counter )
    {
        while( true )
        {
            try
            {
                Thread.sleep( 50 );
            }
            catch ( InterruptedException e )
            {
                // Ignore
            }
            if( forwardStep )
            {
                forwardStep = false;
                counter.inc();
                return counter;
            }
            if( backwardStep )
            {
                backwardStep = false;
                counter.dec();
                return counter;
            }
        }
    }

    private FrameCounter runStep( FrameCounter counter )
    {
        pause();
        counter.inc();
        return counter;
    }

    private FrameCounter frameDumpStep( FrameCounter counter,
                                        DrawFrame drawFrame )
    {
        try
        {
            if ( counter.equals( firstFrameDumped ) )
            { // A whole set has been dumped. Revert to normal
                runMode = Mode.RUN;
                firstFrameDumped = null;
                System.out.println();
                counter.inc();
                return counter;
            }
            if ( null == firstFrameDumped )
            {
                frameDumper = new FrameDumper();
                firstFrameDumped = new FrameCounter( counter );
            }
            String frameID =
                String.format( "%02d_%02d", counter.getFrameSetNum(),
                               counter.getFrameNum() );
            frameDumper.dump( canvas, drawFrame, frameID );

            counter.inc();
            return counter;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            System.exit( 1 );
            return null; // The compiler can be dumb.
        }
    }

    private class DrawFrame extends BufferedDraw
    {
        private final int frameSetNum;
        private final int frameNum;
        private final Renderer<SwingBitmap, SwingPaint> renderer;
        private final SoundPlayer soundPlayer;

        public DrawFrame(
            BufferStrategy strategy,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SoundPlayer soundPlayer,
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

            List<Sprite> sprites = new ArrayList<>();
            int i = 0;
            for ( String block : blockNames )
            {
                if ( block == null || block.equals( NONE ) )
                {
                    ++i;
                    continue;
                }

                java.awt.Point loc = int2dim( i );
                sprites.add(
                    new Sprite(
                        block,
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
            for ( String[] animationSet : animationNames )
            {
                String animationName = animationSet[frameSetNum];
                if ( animationName != null && !animationName.equals( NONE ) )
                {
                    Animation animation = animationCache.get( animationName );
                    if ( animation != null )
                    {
                        Frame frame = animation.get( frameNum );

                        java.awt.Point loc = int2dim( i );
                        Sprite sprite = new Sprite(
                            frame.name,
                            frame.soundEffect,
                            loc.x,
                            loc.y,
                            frame.offsetX,
                            frame.offsetY
                        );

                        if( frameLogging && Mode.FRAME_DUMP != runMode  )
                        {
                            System.out.println( frame.name );
                        }

                        sprites.add( sprite );
                    }
                }
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
        ConfigSchema definition = new ConfigSchema();

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

        return new Config(
            definition, new ConfigFile( new RealFileSystem(), CONFIG_PATH ) );
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

    private void setIcon()
    {
        SwingBitmapLoader l = new SwingBitmapLoader();
        int s = l.sizeFor( 128 );
        SwingBitmap bmp = l.load( "rabbit_fall_01", s );
        this.setIconImage(bmp.image);
    }
}
