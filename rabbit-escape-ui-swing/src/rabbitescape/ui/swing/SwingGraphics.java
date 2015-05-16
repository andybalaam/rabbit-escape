package rabbitescape.ui.swing;

import static rabbitescape.engine.i18n.Translation.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.World;
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
        private static final Color overlay = new Color( 0.7f, 0.7f, 0.7f, 0.8f );

        private static final SwingPaint white =
            new SwingPaint( Color.WHITE );

        private static final SwingPaint graphPaperMajor =
            new SwingPaint( new Color( 205, 212, 220 ) );

        private static final SwingPaint graphPaperMinor =
            new SwingPaint( new Color( 235, 243, 255 ) );

        private final java.awt.Canvas canvas;
        private final Renderer<SwingBitmap, SwingPaint> renderer;
        private final SoundPlayer<SwingBitmap> soundPlayer;
        private final SpriteAnimator<SwingBitmap> animator;
        private final int frameNum;
        private final World world;

        public DrawFrame(
            BufferStrategy strategy,
            java.awt.Canvas canvas,
            Renderer<SwingBitmap, SwingPaint> renderer,
            SoundPlayer<SwingBitmap> soundPlayer,
            SpriteAnimator<SwingBitmap> animator,
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

            List<Sprite<SwingBitmap>> sprites = animator.getSprites( frameNum );

            renderer.render(
                swingCanvas,
                sprites,
                white
            );

            soundPlayer.play( sprites );

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

    private final World world;
    private final GameUi jframe;
    private final BufferStrategy strategy;
    private final AnimationCache animationCache;
    private final SpriteAnimator<SwingBitmap> animator;

    public final Renderer<SwingBitmap, SwingPaint> renderer;
    private final SoundPlayer<SwingBitmap> soundPlayer;

    private int prevScrollX;
    private int prevScrollY;

    public SwingGraphics(
        World world,
        GameUi jframe,
        BitmapCache<SwingBitmap> bitmapCache,
        boolean muted
    )
    {
        this.world = world;
        this.jframe = jframe;
        this.strategy = jframe.canvas.getBufferStrategy();
        this.animationCache = new AnimationCache( new AnimationLoader() );
        this.animator = new SpriteAnimator<SwingBitmap>(
            world, bitmapCache, animationCache );

        this.renderer = new Renderer<SwingBitmap, SwingPaint>( 0, 0, -1 );
        this.soundPlayer = new SoundPlayer<>( new SwingSound( muted ) );
    }

    @Override
    public void draw( int frame )
    {
        setRendererOffset( renderer );

        new DrawFrame(
            strategy,
            jframe.canvas,
            renderer,
            soundPlayer,
            animator,
            frame,
            world
        ).run();
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
        if ( prevScrollX != jframe.scrollX || prevScrollY != jframe.scrollY )
        {
            draw( frame );
            prevScrollX = jframe.scrollX;
            prevScrollY = jframe.scrollY;
        }
    }

    public void playSound( String soundEffect )
    {
        soundPlayer.sound.play( soundEffect );
    }

    public void setMuted( boolean muted )
    {
        soundPlayer.sound.mute( muted );
    }

    @Override
    public void dispose()
    {
        soundPlayer.sound.dispose();
    }
}
