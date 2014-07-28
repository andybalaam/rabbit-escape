package rabbitescape.ui.swing;

import rabbitescape.render.Renderer;
import rabbitescape.render.Sprite;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

import static rabbitescape.engine.i18n.Translation.t;

public class AnimationTester extends JFrame
{
    private final int tileSize;
    boolean running;

    private final Canvas canvas;
    private SwingBitmapScaler scaler;
    private SwingPaint paint;
    private SwingBitmap[] frames;

    private String[] walk = new String[] {
        "rabbit-walk-01",
        "rabbit-walk-02",
        "rabbit-walk-03",
        "rabbit-walk-04",
        "rabbit-walk-05",
        "rabbit-walk-06",
        "rabbit-walk-07",
        "rabbit-walk-08",
        "rabbit-walk-09",
        "rabbit-walk-10"
    };

    private static class InitUi implements Runnable
    {
        public AnimationTester animationTester;

        @Override
        public void run() {
            animationTester = new AnimationTester();
            synchronized ( this )
            {
                notifyAll();
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
            animationTester.loop();
        }
    }

    public AnimationTester()
    {
        this.tileSize = 128;
        int numTilesX = 3;
        int numTilesY = 3;

        setIgnoreRepaint( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        this.canvas = new Canvas();
        canvas.setIgnoreRepaint( true );
        canvas.setPreferredSize(
            new Dimension( tileSize * numTilesX, tileSize * numTilesY) );

        getContentPane().add(canvas, BorderLayout.CENTER);

        loadBitmaps();

        setTitle( t( "Animation Tester" ) );
        pack();
        setVisible( true );

        // Must do this after frame is made visible
        canvas.createBufferStrategy( 2 );
    }

    private void loadBitmaps()
    {
        scaler = new SwingBitmapScaler();
        paint = new SwingPaint();
        SwingBitmapLoader bitmapLoader = new SwingBitmapLoader();

        frames = loadFrames( bitmapLoader, walk );

        frames = new SwingBitmap[] {
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-01.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-02.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-03.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-04.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-05.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-06.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-07.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-08.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-09.png" ),
            bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/rabbit-walk-10.png" )
        };
    }

    private SwingBitmap[] loadFrames(SwingBitmapLoader bitmapLoader, String[] animation)
    {
        SwingBitmap[] ret = new SwingBitmap[animation.length];
        int i = 0;
        for ( String frameName : animation )
        {
            ret[i] = bitmapLoader.load(
                "/rabbitescape/ui/swing/images32/" + frameName + ".png" );
            ++i;
        }
        return ret;
    }

    public static void main( String[] args )
    {
        InitUi initUi = new InitUi();
        SwingUtilities.invokeLater(initUi);
        initUi.loopWhenReady();
        initUi.animationTester.loop();
    }

    private void loop()
    {
        BufferStrategy strategy = canvas.getBufferStrategy();

        Renderer renderer = new Renderer( 0, 0, tileSize );

        int frameNum = 0;
        running = true;
        while( running && this.isVisible() )
        {
            new DrawFrame( strategy, renderer, frameNum ).run();

            pause();

            ++frameNum;
            if ( frameNum == 10 )
            {
                frameNum = 0;
            }
        }
    }

    private class DrawFrame extends BufferedDraw
    {
        private final int frameNum;
        private final Renderer renderer;

        public DrawFrame(
            BufferStrategy strategy, Renderer renderer, int frameNum )
        {
            super( strategy );
            this.renderer = renderer;
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

            Sprite sprite0 = new Sprite(
                frames[frameNum], scaler, 0, 1, 32, 0, 0 );
            Sprite sprite1 = new Sprite(
                frames[frameNum], scaler, 1, 1, 32, 0, 0 );
            Sprite sprite2 = new Sprite(
                frames[frameNum], scaler, 2, 1, 32, 0, 0 );

            renderer.render(
                new SwingCanvas( g ),
                new Sprite[] {
                        sprite0,
                        sprite1,
                        sprite2
                },
                paint
            );
        }
    }

    private void pause()
    {
        try
        {
            Thread.sleep( 100 );
        }
        catch (InterruptedException e)
        {
            // Ignore
        }
    }
}
