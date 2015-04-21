package rabbitescape.ui.swing;

import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.gameloop.GameLoop;
import rabbitescape.render.gameloop.GeneralPhysics;
import rabbitescape.render.gameloop.Physics;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLaunch implements GameLaunch
{
    public final World world;
    private final GeneralPhysics physics;
    public final SwingGraphics graphics;
    private final GameUi jframe;

    private final GameLoop loop;

    public SwingGameLaunch(
        SwingGameInit init, World world, LevelWinListener winListener )
    {
        this.world = world;
        this.physics = new GeneralPhysics( world, winListener );

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.graphics = new SwingGraphics(
            world, uiPieces.jframe, uiPieces.bitmapCache );

        jframe.setGameLaunch( this );

        loop = new GameLoop( new SwingInput(), physics, graphics );
    }

    public void stop()
    {
        loop.pleaseStop();
    }

    @Override
    public void run( String[] args )
    {
        loop.run();
    }

    @Override
    public void showResult()
    {
        // Nothing to do here - we showed the result while we were still running
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        return physics.addToken( tileX, tileY, ability );
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
