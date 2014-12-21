package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.t;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Renderer;
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
    private static final Color overlay = new Color( 0.5f, 0.5f, 0.5f, 0.5f );

    public final World world;
    private final LevelWinListener winListener;
    private final WorldModifier worldModifier;
    private boolean running;
    private boolean paused;
    private final int renderingTileSize;
    private final List<StatsChangedListener> statsListeners;

    private final GameJFrame jframe;
    private final BitmapCache<SwingBitmap> bitmapCache;

    public SwingGameLoop(
        SwingGameInit init, World world, LevelWinListener winListener )
    {
        this.world = world;
        this.winListener = winListener;
        this.worldModifier = new WorldModifier( world );
        this.running = true;
        this.paused = false;
        this.renderingTileSize = 32;
        this.statsListeners = new ArrayList<>();

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.bitmapCache = uiPieces.bitmapCache;

        jframe.setGameLoop( this );

        jframe.setWorldSize(
            new Dimension(
                world.size.width,
                world.size.height
            ),
            renderingTileSize
        );
    }

    public void stop()
    {
        running = false;
    }

    public void setPaused( boolean paused )
    {
        this.paused = paused;
    }

    @Override
    public void run( String[] args )
    {
        int imagesTileSize = 32;

        BufferStrategy strategy = jframe.canvas.getBufferStrategy();
        Renderer renderer = new Renderer(
            -jframe.scrollX, -jframe.scrollY, renderingTileSize );

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
            ChangeDescription changes = world.describeChanges();

            final SpriteAnimator animator = new SpriteAnimator(
                world, changes, imagesTileSize, bitmapCache, animationCache );

            for ( int f = 0; running && f < framesPerStep; ++f )
            {
                renderer.setOffset( -jframe.scrollX, -jframe.scrollY );

                new DrawFrame(
                    strategy,
                    jframe.canvas,
                    renderer,
                    animator,
                    f,
                    world.completionState()
                ).run();

                sleep( 50 );

                while ( paused )
                {
                    sleep( 500 );
                }
            }

            if ( world.completionState() != CompletionState.RUNNING )
            {
                paused = true;
            }
        }
    }

    private void checkWon()
    {
        if ( world.completionState() == CompletionState.WON )
        {
            winListener.won();
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

        private final java.awt.Canvas canvas;
        private final Renderer renderer;
        private final SpriteAnimator animator;
        private final int frameNum;
        private final CompletionState completionState;

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer renderer,
            SpriteAnimator animator,
            int frameNum,
            CompletionState completionState
        )
        {
            super( strategy );
            this.canvas = canvas;
            this.renderer = renderer;
            this.animator = animator;
            this.frameNum = frameNum;
            this.completionState = completionState;
        }

        @Override
        void draw( Graphics2D g )
        {
            fillCanvas( g, Color.WHITE );

            SwingCanvas swingCanvas = new SwingCanvas( g );

            renderer.render(
                swingCanvas,
                animator.getSprites( frameNum ),
                unusedPaint
            );

            if ( completionState != CompletionState.RUNNING )
            {
                drawResult( g );
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

        private void drawResult( Graphics2D g )
        {
            fillCanvas( g, overlay );

            String msg;
            if ( completionState == CompletionState.WON )
            {
                msg = t( "You won!" );
            }
            else
            {
                msg = t( "You lost." );
            }

            writeText( g, msg, 0.5, 0.06 );
            writeText( g, t( "Click to continue." ), 0.7, 0.03 );
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
            !paused
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
