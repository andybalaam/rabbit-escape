package rabbitescape.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import rabbitescape.engine.ChangeDescription;
import rabbitescape.engine.World;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GameLoop;
import rabbitescape.render.Renderer;

public class SwingGameLoop implements GameLoop
{
    private final int framesPerStep = 10;

    private final GameJFrame jframe;
    private final World world;
    private boolean running;
    private final int renderingTileSize;

    public SwingGameLoop( SwingGameInit init, World world )
    {
        this.world = world;
        this.jframe = init.waitForUi.waitForUi();
        this.running = true;
        this.renderingTileSize = 32;

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

    @Override
    public void run()
    {
        int imagesTileSize = 32;

        BufferStrategy strategy = jframe.canvas.getBufferStrategy();
        Renderer renderer = new Renderer( 0, 0, renderingTileSize );
        BitmapCache<SwingBitmap> bitmapCache =
            new BitmapCache<SwingBitmap>( new SwingBitmapLoader(), 500 );

        while( running )
        {
            world.step();
            ChangeDescription changes = world.describeChanges();

            final SpriteAnimator animator = new SpriteAnimator(
                world, changes, imagesTileSize, bitmapCache );

            for ( int f = 0; running && f < framesPerStep; ++f )
            {
                new DrawFrame(
                    strategy, jframe.canvas, renderer, animator, f ).run();

                pause();
            }

            if ( world.finished() )
            {
                running = false;
            }
        }
    }

    private void pause()
    {
        try
        {
            Thread.sleep( 50 );
        }
        catch ( InterruptedException e )
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
        catch ( InterruptedException e )
        {
        }
    }
}
