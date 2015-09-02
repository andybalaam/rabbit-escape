package rabbitescape.ui.swing;

import java.util.Map;

import javax.swing.JOptionPane;

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
        world.setIntro( false );
        loop.run();
    }

    private void showIntroDialog()
    {
        MiniGameLoop bgDraw = new MiniGameLoop();

        new Thread( bgDraw ).start();
        try
        {
            introDialog();
        }
        finally
        {
            bgDraw.running = false;
        }
    }

    private void introDialog()
    {
        JOptionPane.showMessageDialog(
            frame,
            Util.concat(
                Util.split( world.description, "\\n" ),
                new String[] {
                    " ",
                    t(
                        "Rabbits: ${num_rabbits}  Must save: ${num_to_save}",
                        Util.newMap(
                            "num_rabbits", String.valueOf( world.num_rabbits ),
                            "num_to_save", String.valueOf( world.num_to_save )
                        )
                    )
                }
            ),
            world.name,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    @Override
    public void showResult()
    {
        // Nothing to do here - we showed the result while we were still running
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
