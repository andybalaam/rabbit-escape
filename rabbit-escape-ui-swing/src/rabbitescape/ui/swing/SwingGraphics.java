package rabbitescape.ui.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.util.List;

import rabbitescape.engine.Thing;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util;
import rabbitescape.render.AnimationCache;
import rabbitescape.render.AnimationLoader;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.GraphPaperBackground;
import rabbitescape.render.PolygonBuilder;
import rabbitescape.render.Renderer;
import rabbitescape.render.SoundPlayer;
import rabbitescape.render.Sprite;
import rabbitescape.render.SpriteAnimator;
import rabbitescape.render.Overlay;
import rabbitescape.render.Vertex;
import rabbitescape.render.androidlike.Path;
import rabbitescape.render.gameloop.Graphics;
import rabbitescape.render.gameloop.WaterAnimation;

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

        private static final SwingPaint waterColor =
            new SwingPaint( new Color( 10, 100, 220, 100 ) );

        private final java.awt.Canvas canvas;
        private final Renderer<SwingBitmap, SwingPaint> renderer;
        private final SoundPlayer soundPlayer;
        private final SpriteAnimator animator;
        private final int frameNum;
        private final World world;
        private final WaterAnimation waterAnimation;

        static
        {
            waterColor.setStyle( SwingPaint.Style.FILL );
        }

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SoundPlayer soundPlayer,
            SpriteAnimator animator,
            int frameNum,
            World world,
            WaterAnimation waterAnimation
        )
        {
            super( strategy );
            this.canvas = canvas;
            this.renderer = renderer;
            this.soundPlayer = soundPlayer;
            this.animator = animator;
            this.frameNum = frameNum;
            this.world = world;
            this.waterAnimation = waterAnimation;
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

            drawPolygons( waterAnimation, swingCanvas );

            List<Sprite> sprites = animator.getSprites( frameNum );

            renderer.render(
                swingCanvas,
                sprites,
                white
            );

            if ( world.paused )
            {
                tacticalOverlay( swingCanvas, world );
            }

            if ( null != soundPlayer )
            {
                soundPlayer.play( sprites );
            }
        }

        private void tacticalOverlay( SwingCanvas swingCanvas, World world )
        {
            SwingPaint dull = new SwingPaint( new Color( 70, 70, 70, 200 ) );
            swingCanvas.drawColor( dull );

            Overlay overlay = new Overlay( world );
            SwingPaint textPaint =
                new SwingPaint( new Color( 100, 255, 100, 255 ) );


            int h = swingCanvas.stringHeight();

            for ( Thing t : overlay.items )
            {
                String notation = overlay.at( t.x, t.y );

                String[] lines = Util.split( notation, "\n" );

                for ( int i = 0; i < lines.length ; i++ )
                {
                    int x = renderer.offsetX + t.x * renderer.tileSize;
                    int y = renderer.offsetY + t.y * renderer.tileSize + i * h;
                    x += ( renderer.tileSize - swingCanvas.stringWidth( lines[i] ) ) / 2; // centre
                    y += ( renderer.tileSize - h * lines.length ) / 2 ;
                    swingCanvas.drawText( lines[i],
                        (float)x,
                        (float)y,
                        textPaint);
                }
            }
        }

        synchronized void drawPolygons( WaterAnimation wa,
            SwingCanvas swingCanvas )
        {
            float f = renderer.tileSize / 32f;
            for ( PolygonBuilder pb: wa.getPolygons()  )
            {
                Path p = pb.path( f,
                    new Vertex( renderer.offsetX, renderer.offsetY ) );
                swingCanvas.drawPath( p, waterColor );
            }
        }

    }

    private final World world;

    private final GameUi jframe;
    private final BufferStrategy strategy;
    private final SpriteAnimator animator;
    private final FrameDumper frameDumper;
    private final WaterAnimation waterAnimation;

    public final Renderer<SwingBitmap, SwingPaint> renderer;
    private final SoundPlayer soundPlayer;

    private int prevScrollX;
    private int prevScrollY;
    private CompletionState lastWorldState;

    private int lastFrame;

    public SwingGraphics(
        World world,
        GameUi jframe,
        BitmapCache<SwingBitmap> bitmapCache,
        SwingSound sound,
        FrameDumper frameDumper,
        WaterAnimation waterAnimation
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
        this.waterAnimation = waterAnimation;

        lastFrame = -1;
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
            world,
            waterAnimation
        );

        df.run();

        frameDumper.dump( jframe.canvas, df );

        lastFrame = frame;
    }

    public void redraw()
    {
        // Can't redraw before drawing
        if ( -1 == lastFrame )
        {
            return;
        }
        setRendererOffset( renderer );

        DrawFrame df = new DrawFrame(
            strategy,
            jframe.canvas,
            renderer,
            null, // Do not repeat sounds
            animator,
            lastFrame,
            world,
            waterAnimation
        );

        df.run();
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
