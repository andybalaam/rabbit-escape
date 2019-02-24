package rabbitescape.ui.swing;

import rabbitescape.engine.Thing;
import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util;
import rabbitescape.render.*;
import rabbitescape.render.androidlike.Path;
import rabbitescape.render.androidlike.Rect;
import rabbitescape.render.androidlike.Sound;
import rabbitescape.render.gameloop.Graphics;
import rabbitescape.render.gameloop.WaterAnimation;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.List;

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

        private static final int
            waterR = 130, waterG = 167, waterB = 221;

        private static final SwingPaint waterColor =
            new SwingPaint( new Color( waterR, waterG, waterB, 255 ) );

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

            drawWater( waterAnimation, swingCanvas );

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
            int ts = renderer.tileSize;

            for ( Thing t : overlay.items )
            {
                String notation = overlay.at( t.x, t.y );

                String[] lines = Util.split( notation, "\n" );

                for ( int i = 0; i < lines.length ; i++ )
                {
                    int x = renderer.offsetX + t.x * ts;
                    int y = renderer.offsetY + t.y * ts + i * h;
                    // centre
                    x += ( ts - swingCanvas.stringWidth( lines[i] ) ) / 2;
                    y += ( ts - h * lines.length ) / 2 ;
                    swingCanvas.drawText( lines[i],
                        x,
                        y,
                        textPaint);
                }
            }
        }

        /**
         * Polygons representing water that is not falling,
         * and particle effects for falling water.
         */
        public void drawWater( WaterAnimation wa, SwingCanvas swingCanvas )
        {
            float f = renderer.tileSize / 32f;
            Vertex offset = new Vertex( renderer.offsetX, renderer.offsetY );

            int ts = renderer.tileSize;
            // shade whole background square cells with any water
            for ( WaterRegionRenderer wrr : wa.lookupRenderer)
            {
                Rect rect = new Rect(
                    ts * wrr.getX() + renderer.offsetX,
                    ts * wrr.getY() + renderer.offsetY,
                    ts * wrr.getX() + ts + renderer.offsetX,
                    ts * wrr.getY() + ts + renderer.offsetY
                );
                Color bsColor =
                    new Color( waterR, waterG, waterB, wrr.backShadeAlpha() );
                SwingPaint backShade =  new SwingPaint( bsColor );
                backShade.setStyle( SwingPaint.Style.FILL );
                swingCanvas.drawRect( rect, backShade );
            }

            // draw polygons to represent pooled water
            for ( PolygonBuilder pb : wa.calculatePolygons() )
            {
                Path p = pb.path( f, offset );
                swingCanvas.drawPath( p, waterColor );
            }

            // draw particles to represent falling water
            for ( WaterRegionRenderer wrr : wa.lookupRenderer )
            {
                for ( WaterParticle wp : wrr.particles )
                {
                    Path p = wp.polygon().path( f, offset );
                    SwingPaint fadeShade =  new SwingPaint(
                        new Color( waterR, waterG, waterB, wp.alpha ) );
                    fadeShade.setStyle ( SwingPaint.Style.FILL );
                    swingCanvas.drawPath( p, fadeShade);
                }
            }
        }

    }

    public final World world;

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
    private volatile boolean drawing;

    public SwingGraphics(
        World world,
        GameUi jframe,
        BitmapCache<SwingBitmap> bitmapCache,
        Sound sound,
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

        this.lastFrame = -1;
        this.drawing = false;
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
        // We may be invoked from multiple threads - give up if we're already
        // drawing.
        if ( drawing )
        {
            return;
        }
        drawing = true;

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

        drawing = false;
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
