package rabbitescape.ui.swing;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import static rabbitescape.engine.i18n.Translation.t;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.util.Util;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.gameloop.GameLoop;
import rabbitescape.render.gameloop.GeneralPhysics;
import rabbitescape.render.gameloop.Physics;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLaunch implements GameLaunch
{
    /**
     * A loop that just draws the game window when it's behind the
     * intro dialog.
     */
    class MiniGameLoop implements Runnable
    {
        public boolean running = true;

        @Override
        public void run()
        {
            while ( running )
            {
                graphics.draw( physics.frameNumber() );
                try
                {
                    Thread.sleep( 500 );
                }
                catch ( InterruptedException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public final World world;
    private final GeneralPhysics physics;
    public final SwingGraphics graphics;
    private final GameUi jframe;

    private final GameLoop loop;
    private final MainJFrame frame;

    public SwingGameLaunch(
        SwingGameInit init,
        World world,
        LevelWinListener winListener,
        SwingSound sound
    )
    {
        this.world = world;
        this.frame = init.frame;
        this.physics = new GeneralPhysics( world, winListener );

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.graphics = new SwingGraphics(
            world,
            uiPieces.jframe,
            uiPieces.bitmapCache,
            sound
        );

        jframe.setGameLaunch( this );

        sound.setMusic( world.music );

        loop = new GameLoop( new SwingInput(), physics, graphics );
    }

    public void stop()
    {
        loop.pleaseStop();
    }

    @Override
    public void run( String[] args )
    {
        showIntroDialog();
        loop.run();
    }

    /**
     * Must not be called from within event loop.
     */
    private void showDialog( final String title, final Object message )
    {
        runSwingCodeWithGameLoopBehind(
            new Runnable()
            {
                @Override
                public void run()
                {
                    JOptionPane.showMessageDialog(
                        frame,
                        message,
                        title,
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        );
    }

    /**
     * Must be called from within the event loop.
     */
    private boolean askExplodeAll()
    {
        MiniGameLoop bgDraw = new MiniGameLoop();

        new Thread( bgDraw ).start();

        try
        {
            String[] buttons = new String[] { t( "Cancel" ), t( "Explode!" ) };

            int ret = JOptionPane.showOptionDialog(
                frame,
                t( "Do you want to explode your rabbits?" ),
                t( "Explode all rabbits?" ),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                buttons,
                buttons[1]
            );

            return ( ret == 1 );
        }
        finally
        {
            bgDraw.running = false;
        }
    }

    private void runSwingCodeWithGameLoopBehind( Runnable doRun )
    {
        MiniGameLoop bgDraw = new MiniGameLoop();

        new Thread( bgDraw ).start();

        try
        {
            SwingUtilities.invokeAndWait( doRun );
        }
        catch ( InvocationTargetException e )
        {
            throw new RuntimeException( e );
        }
        catch ( InterruptedException e )
        {
            // Continue if interrupted
        }
        finally
        {
            bgDraw.running = false;
        }
    }

    /**
     * Not called from within event loop.
     */
    private void showIntroDialog()
    {
        showDialog(
            world.name,
            Util.concat(
                Util.split( world.description, "\\n" ),
                new String[] {
                    " ",
                    t(
                        "Rabbits: ${num_rabbits}  Must save: ${num_to_save}",
                        statsValues( world )
                    )
                }
            )
        );
    }

    /**
     * Not called from within event loop.
     */
    private void showWonDialog()
    {
        showDialog(
            t( "You won!" ),
            t(
                "Saved: ${num_saved}  Needed: ${num_to_save}",
                statsValues( world )
            )
        );
    }

    /**
     * Not called from within event loop.
     */
    private void showLostDialog()
    {
        showDialog(
            t( "You lost!" ),
            t(
                "Saved: ${num_saved}  Needed: ${num_to_save}",
                statsValues( world )
            )
        );
    }

    private static Map<String, Object> statsValues( World world )
    {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put( "num_rabbits", world.num_rabbits );
        values.put( "num_to_save", world.num_to_save );
        values.put( "num_saved",   world.num_saved );
        return values;
    }

    @Override
    public void showResult()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                showWonDialog();
                break;
            }
            case LOST:
            {
                showLostDialog();
                break;
            }
            default:
            {
                // Maybe the user clicked back - do nothing here
            }
        }

        jframe.exit();
    }

    public void checkExplodeAll()
    {
        world.setPaused( true );

        boolean explode = askExplodeAll();

        world.setPaused( false );

        if ( explode )
        {
            world.changes.explodeAllRabbits();
        }
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        int prev = world.abilities.get( ability );
        int now = physics.addToken( tileX, tileY, ability );
        if ( now != prev )
        {
            graphics.playSound( "place_token" );
        }
        return now;
    }

    public Map<Token.Type, Integer> getAbilities()
    {
        return world.abilities;
    }

    public void addStatsChangedListener( Physics.StatsChangedListener listener )
    {
        physics.addStatsChangedListener( listener );
    }
}
