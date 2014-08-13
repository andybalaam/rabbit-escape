package rabbitescape.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.World;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Renderer;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLoop implements GameLoop
{
    private static final int framesPerStep = 10;

    private final World world;
    private boolean running;
    private boolean paused;
    private final int renderingTileSize;

    private final GameJFrame jframe;
    private final BitmapCache<SwingBitmap> bitmapCache;

    public SwingGameLoop( SwingGameInit init, World world )
    {
        this.world = world;
        this.running = true;
        this.paused = false;
        this.renderingTileSize = 32;

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.bitmapCache = uiPieces.bitmapCache;

        jframe.setGameLoop( this );

        jframe.canvas.setPreferredSize(
            new Dimension(
                renderingTileSize * world.size.width,
                renderingTileSize * world.size.height
            )
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
        Renderer renderer = new Renderer( 0, 0, renderingTileSize );

        AnimationCache animationCache = new AnimationCache(
            new AnimationLoader() );

        while( running )
        {
            world.step();
            ChangeDescription changes = world.describeChanges();

            final SpriteAnimator animator = new SpriteAnimator(
                world, changes, imagesTileSize, bitmapCache, animationCache );

            for ( int f = 0; running && f < framesPerStep; ++f )
            {
                new DrawFrame(
                    strategy, jframe.canvas, renderer, animator, f ).run();

                sleep( 50 );

                while ( paused )
                {
                    sleep( 500 );
                }
            }

            if ( world.finished() )
            {
                running = false;
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

        private final java.awt.Canvas canvas;
        private final Renderer renderer;
        private final SpriteAnimator animator;
        private final int frameNum;

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer renderer,
            SpriteAnimator animator,
            int frameNum
        )
        {
            super( strategy );
            this.canvas = canvas;
            this.renderer = renderer;
            this.animator = animator;
            this.frameNum = frameNum;
        }

        @Override
        void draw( Graphics2D g )
        {
            g.setPaint( Color.WHITE );
            g.fillRect(
                0,
                0,
                canvas.getWidth(),
                canvas.getHeight()
            );

            SwingCanvas canvas = new SwingCanvas( g );

            renderer.render(
                canvas,
                animator.getSprites( frameNum ),
                unusedPaint
            );
        }
    }

    @Override
    public void showResult()
    {
        final String msg;
        if ( world.success() )
        {
            msg = "You won!";
        }
        else
        {
            msg = "You lost.";
        }

        new BufferedDraw( jframe.canvas.getBufferStrategy() )
        {
            @Override
            void draw( Graphics2D g )
            {
                g.drawString( msg, 20, 50 );
            }
        }.run();

        try
        {
            Thread.sleep( 2000 );
        }
        catch ( InterruptedException ignored )
        {
        }
    }
}
