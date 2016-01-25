package rabbitescape.ui.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.List;

import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.Renderer;
import rabbitescape.render.SoundPlayer;
import rabbitescape.render.Sprite;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.render.gameloop.Graphics;

public class SwingGraphics implements Graphics
{
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
        private final SoundPlayer soundPlayer;
        private final SpriteAnimator animator;
        private final int frameNum;
        private final World world;

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SoundPlayer soundPlayer,
            SpriteAnimator animator,
            int frameNum,
            World world
        )
        {
            super( strategy );
            this.canvas = canvas;
            this.renderer = renderer;
            this.soundPlayer = soundPlayer;
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

            List<Sprite> sprites = animator.getSprites( frameNum );

            renderer.render(
                swingCanvas,
                sprites,
                white
            );

            soundPlayer.play( sprites );
        }
    }

    private final World world;

    private final GameUi jframe;
    private final BufferStrategy strategy;
    private final SpriteAnimator animator;
    private final FrameDumper frameDumper;

    public final Renderer<SwingBitmap, SwingPaint> renderer;
    private final SoundPlayer soundPlayer;

    private int prevScrollX;
    private int prevScrollY;
    private CompletionState lastWorldState;

    public SwingGraphics(
        World world,
        GameUi jframe,
        BitmapCache<SwingBitmap> bitmapCache,
        SwingSound sound,
        FrameDumper frameDumper
    )
    {
        this.world = world;
        this.jframe = jframe;
        this.strategy = jframe.canvas.getBufferStrategy();
        this.animator = new SpriteAnimator(
            world, new AnimationCache( new AnimationLoader() ) );

        this.renderer = new Renderer<SwingBitmap, SwingPaint>(
            0, 0, -1, bitmapCache );

        this.soundPlayer = new SoundPlayer( sound );

        this.prevScrollX = -1;
        this.prevScrollY = -1;
        this.lastWorldState = null;
        this.frameDumper = frameDumper;
    }

    @Override
    public void draw( int frame )
    {
        setRendererOffset( renderer );

        DrawFrame df = new DrawFrame(
            strategy,
            jframe.canvas,
            renderer,
            soundPlayer,
            animator,
            frame,
            world
        );

        df.run();

        frameDumper.dump( jframe.canvas, df );
    }

    public void setTileSize( int timeSize )
    {
        renderer.tileSize = timeSize;
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

    @Override
    public void rememberScrollPos()
    {
        prevScrollX = jframe.scrollX;
        prevScrollY = jframe.scrollY;
    }

    @Override
    public void drawIfScrolled( int frame )
    {
        if (
               prevScrollX != jframe.scrollX
            || prevScrollY != jframe.scrollY
            || lastWorldState != world.completionState()
        )
        {
            lastWorldState = world.completionState();

            draw( frame );
            prevScrollX = jframe.scrollX;
            prevScrollY = jframe.scrollY;
        }
    }

    public void playSound( String soundEffect )
    {
        soundPlayer.sound.playSound( soundEffect );
    }

    public void setMuted( boolean muted )
    {
        soundPlayer.sound.mute( muted );
    }

    @Override
    public void dispose()
    {
    }
}
