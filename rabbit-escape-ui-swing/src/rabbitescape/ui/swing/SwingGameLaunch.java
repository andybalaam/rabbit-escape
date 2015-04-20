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
import rabbitescape.render.GameLaunch;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.Physics;
import rabbitescape.render.Renderer;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLaunch implements GameLaunch
{
    public static interface StatsChangedListener
    {
        void changed( int waiting, int out, int saved );
    }

    private static final int framesPerStep = 10;
    private static final Color overlay = new Color( 0.7f, 0.7f, 0.7f, 0.8f );

    public final World world;
    private boolean running;
    private final List<StatsChangedListener> statsListeners;
    private final Physics physics;

    private final GameUi jframe;
    private final BitmapCache<SwingBitmap> bitmapCache;
    public final Renderer<SwingBitmap, SwingPaint> renderer;

    public SwingGameLaunch(
        SwingGameInit init, World world, LevelWinListener winListener )
    {
        this.world = world;
        this.running = true;
        this.statsListeners = new ArrayList<>();
        this.physics = new Physics( world, winListener );

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.bitmapCache = uiPieces.bitmapCache;

        this.renderer = new Renderer<SwingBitmap, SwingPaint>( 0, 0, -1 );

        jframe.setGameLaunch( this );
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
                physics.step();
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
        private static final SwingPaint white =
            new SwingPaint( Color.WHITE );

        private static final SwingPaint graphPaperMajor =
            new SwingPaint( new Color( 205, 212, 220 ) );

        private static final SwingPaint graphPaperMinor =
            new SwingPaint( new Color( 235, 243, 255 ) );

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
            SwingCanvas swingCanvas = new SwingCanvas(
                g, canvas.getWidth(), canvas.getHeight() );

            GraphPaperBackground.drawBackground(
                world,
                renderer,
                swingCanvas,
                white,
                graphPaperMajor,
                graphPaperMinor
            );

            renderer.render(
                swingCanvas,
                animator.getSprites( frameNum ),
                white
            );

            drawResult( g );
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

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        return physics.addToken( tileX, tileY, ability );
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
