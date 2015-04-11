package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.t;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Renderer;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLoop implements GameLoop
{
    public static interface StatsChangedListener
    {
        void changed( int waiting, int out, int saved );
    }

    /**
     * Everything that modifies the world goes through here, with
     * synchronization.
     */
    private static class WorldModifier
    {
        private final World world;

        public WorldModifier( World world )
        {
            this.world = world;
        }

        public synchronized void step()
        {
            world.step();
        }

        public synchronized void addToken( int x, int y, Token.Type type )
        {
            world.changes.addToken( x, y, type );
        }
    }

    private static final int framesPerStep = 10;
    private static final Color overlay = new Color( 0.7f, 0.7f, 0.7f, 0.8f );

    public final World world;
    private final LevelWinListener winListener;
    private final WorldModifier worldModifier;
    private boolean running;
    private final List<StatsChangedListener> statsListeners;

    private final GameUi jframe;
    private final BitmapCache<SwingBitmap> bitmapCache;
    public final Renderer<SwingBitmap, SwingPaint> renderer;

    public SwingGameLoop(
        SwingGameInit init, World world, LevelWinListener winListener )
    {
        this.world = world;
        this.winListener = winListener;
        this.worldModifier = new WorldModifier( world );
        this.running = true;
        this.statsListeners = new ArrayList<>();

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.bitmapCache = uiPieces.bitmapCache;

        this.renderer = new Renderer<SwingBitmap, SwingPaint>( 0, 0, -1 );

        jframe.setGameLoop( this );
    }

    public void stop()
    {
        running = false;
    }

    @Override
    public void run( String[] args )
    {
        BufferStrategy strategy = jframe.canvas.getBufferStrategy();

        AnimationCache animationCache = new AnimationCache(
            new AnimationLoader() );

        while( running )
        {
            if ( world.completionState() == CompletionState.RUNNING )
            {
                worldModifier.step();
                checkWon();
                notifyStatsListeners();
            }

            final SpriteAnimator<SwingBitmap> animator =
                new SpriteAnimator<SwingBitmap>(
                    world, bitmapCache, animationCache );

            int f = 0;
            while ( running && f < framesPerStep )
            {
                sleep( 50 );

                setRendererOffset( renderer );

                new DrawFrame(
                    strategy,
                    jframe.canvas,
                    renderer,
                    animator,
                    f,
                    world
                ).run();


                if ( world.completionState() == CompletionState.RUNNING )
                {
                    // If not paused, go on to the next frame
                    ++f;
                }
                else
                {
                    // Slow the frame rate when paused
                    sleep( 500 );
                }
            }
        }
    }

    private void setRendererOffset( Renderer<SwingBitmap, SwingPaint> renderer )
    {
        renderer.setOffset(
            calcOffset(
                jframe.scrollX,
                jframe.canvas.getWidth(),
                jframe.worldSizeInPixels.width
            ),
            calcOffset(
                jframe.scrollY,
                jframe.canvas.getHeight(),
                jframe.worldSizeInPixels.height
            )
        );
    }

    private int calcOffset( int scroll, int canvasSize, int worldSize )
    {
        if ( worldSize < canvasSize )
        {
            return ( ( canvasSize - worldSize ) / 2 );
        }
        else
        {
            return -scroll;
        }
    }

    private void checkWon()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                winListener.won();
                break;
            }
            case LOST:
            {
                winListener.lost();
                break;
            }
            default:
            {
                break;
            }
        }
    }

    private void sleep( long millis )
    {
        try
        {
            Thread.sleep( millis );
        }
        catch ( InterruptedException ignored )
        {
        }
    }

    private static class DrawFrame extends BufferedDraw
    {
        private static final SwingPaint unusedPaint = new SwingPaint();

        private static final Color graphPaperMajor = new Color( 205, 212, 220 );
        private static final Color graphPaperMinor = new Color( 235, 243, 255 );

        private final java.awt.Canvas canvas;
        private final Renderer<SwingBitmap, SwingPaint> renderer;
        private final SpriteAnimator<SwingBitmap> animator;
        private final int frameNum;
        private final World world;

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SpriteAnimator<SwingBitmap> animator,
            int frameNum,
            World world
        )
        {
            super( strategy );
            this.canvas = canvas;
            this.renderer = renderer;
            this.animator = animator;
            this.frameNum = frameNum;
            this.world = world;
        }

        @Override
        void draw( Graphics2D g )
        {
            drawBackground( g );

            SwingCanvas swingCanvas = new SwingCanvas(
                g, canvas.getWidth(), canvas.getHeight() );

            renderer.render(
                swingCanvas,
                animator.getSprites( frameNum ),
                unusedPaint
            );

            drawResult( g );
        }

        private void drawBackground( Graphics2D g )
        {
            fillCanvas( g, Color.WHITE );

            int minTile = -2;
            int maxTileX = world.size.width + 2;
            int maxTileY = world.size.height + 2;
            int minX = renderer.offsetX + ( minTile  * renderer.tileSize );
            int maxX = renderer.offsetX + ( maxTileX * renderer.tileSize );
            int minY = renderer.offsetY + ( minTile  * renderer.tileSize );
            int maxY = renderer.offsetY + ( maxTileY * renderer.tileSize );
            double inc = renderer.tileSize / 4.0;

            g.setPaint( graphPaperMinor );
            for( int x = minX; x < maxX; x += renderer.tileSize )
            {
                for ( int sub = 1; sub < 4; ++sub )
                {
                    int dx = (int)( x + ( sub * inc ) );
                    g.drawLine( dx, minY, dx, maxY );
                }
            }
            for( int y = minY; y < maxY; y += renderer.tileSize )
            {
                g.setPaint( graphPaperMinor );
                for ( int sub = 1; sub < 4; ++sub )
                {
                    int dy = (int)( y + ( sub * inc ) );
                    g.drawLine( minX, dy, maxX, dy );
                }
            }

            g.setPaint( graphPaperMajor );
            for( int x = minX; x <= maxX; x += renderer.tileSize )
            {
                g.drawLine( x, minY, x, maxY );
            }
            for( int y = minY; y <= maxY; y += renderer.tileSize )
            {
                g.drawLine( minX, y, maxX, y );
            }
        }

        private void fillCanvas( Graphics2D g, Color paint )
        {
            g.setPaint( paint );
            g.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
            );
        }

        private static class OverlayMessage
        {
            public String heading;
            public String text1;
            public String text2;
            public String text3;
        }

        private void drawResult( Graphics2D g )
        {
            OverlayMessage message = messageForState();
            if ( message.heading == null )
            {
                return;
            }

            fillCanvas( g, overlay );

            writeText( g, message.heading, 0.5, 0.06 );
            writeText( g, message.text1,   0.7, 0.03 );

            if ( message.text2 != null )
            {
                writeText( g, message.text2, 0.76, 0.03 );
            }

            if ( message.text3 != null )
            {
                writeText( g, message.text3, 0.9, 0.025 );
            }
        }

        private OverlayMessage messageForState()
        {
            OverlayMessage message = new OverlayMessage();
            switch( world.completionState() )
            {
                case RUNNING:
                case PAUSED:
                {
                    // No overlay in these cases
                    break;
                }
                case INTRO:
                {
                    introMessage( message, world );
                    break;
                }
                case WON:
                {
                    message.heading = t( "You won!" );
                    message.text1   = t( "Click the screen to continue." );
                    message.text3 = t(
                        "Saved: ${num_saved}  Needed: ${num_to_save}",
                        statsValues( world )
                    );
                    break;
                }
                case READY_TO_EXPLODE_ALL:
                {
                    message.heading = t( "Explode all rabbits?" );

                    message.text1   = t(
                        "Click the explode button again to explode all rabbits,"
                    );

                    message.text2   = t( "or click the screen to cancel." );

                    break;
                }
                case LOST:
                {
                    message.heading = t( "You lost." );
                    message.text1   = t( "Click the screen to continue." );
                    message.text3 = t(
                        "Saved: ${num_saved}  Needed: ${num_to_save}",
                        statsValues( world )
                    );

                    break;
                }
                default:
                {
                    throw new AssertionError(
                        "Unknown completion state: "
                        + world.completionState()
                    );
                }
            }
            return message;
        }

        private void introMessage( OverlayMessage message, World world )
        {
            message.heading = t( world.name );

            message.text3 = t(
                "Rabbits: ${num_rabbits}  Must save: ${num_to_save}",
                statsValues( world )
            );

            if ( world.description.isEmpty() )
            {
                message.text1 = t( "Click the screen to continue." );
            }
            else
            {
                String[] lines = world.description.split( "\\\\n", 2 );
                message.text1 = lines[0];
                if ( lines.length == 2 )
                {
                    message.text2 = lines[1];
                }
            }
        }

        private Map<String, Object> statsValues( World world )
        {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put( "num_rabbits", world.num_rabbits );
            values.put( "num_to_save", world.num_to_save );
            values.put( "num_saved",   world.num_saved );
            return values;
        }

        private void writeText(
            Graphics2D g, String msg, double pos, double size )
        {
            g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            g.setPaint( Color.BLACK );

            int fontSize = (int)( canvas.getWidth() * size );

            Font f = new Font( Font.SANS_SERIF, Font.PLAIN, fontSize );
            g.setFont( f );
            FontMetrics metrics = g.getFontMetrics( f );

            g.drawString(
                msg,
                ( canvas.getWidth()  - metrics.stringWidth( msg ) ) / 2,
                (int)( canvas.getHeight() * pos )
            );
        }

    }

    @Override
    public void showResult()
    {
        // Nothing to do here - we showed the result while we were still running
    }

    public int addToken( Token.Type ability, int gridX, int gridY )
    {
        if (
               world.completionState() == CompletionState.RUNNING
            && world.abilities.get( ability ) > 0
            && gridX >= 0
            && gridY >= 0
            && gridX < world.size.width
            && gridY < world.size.height
        )
        {
            worldModifier.addToken( gridX, gridY, ability );
        }

        return world.abilities.get( ability );
    }

    public Map<Token.Type, Integer> getAbilities()
    {
        return world.abilities;
    }

    public void addStatsChangedListener( StatsChangedListener listener )
    {
        statsListeners.add( listener );
    }

    private void notifyStatsListeners()
    {
        for ( StatsChangedListener listener : statsListeners )
        {
            listener.changed(
                world.num_waiting,
                world.numRabbitsOut(),
                world.num_saved
            );
        }
    }
}
