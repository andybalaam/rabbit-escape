package rabbitescape.ui.swing;

import java.util.Map;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.render.GameLaunch;
import rabbitescape.render.LegacyPhysics;
import rabbitescape.ui.swing.SwingGameInit.WhenUiReady;

public class SwingGameLaunch implements GameLaunch
{
    public final World world;
    private final LegacyPhysics physics;
    public final SwingGraphics graphics;
    private final GameUi jframe;

    private final LegacyGameLoop loop;

    public SwingGameLaunch(
        SwingGameInit init, World world, LevelWinListener winListener )
    {
        this.world = world;
        this.physics = new LegacyPhysics( world, winListener );

        // This blocks until the UI is ready:
        WhenUiReady uiPieces = init.waitForUi.waitForUi();

        this.jframe = uiPieces.jframe;
        this.graphics = new SwingGraphics(
            world, uiPieces.jframe, uiPieces.bitmapCache );

        jframe.setGameLaunch( this );

        loop = new LegacyGameLoop( new SwingInput(), physics, graphics );
    }

    public void stop()
    {
        loop.stop();
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

    public void addStatsChangedListener( LegacyPhysics.StatsChangedListener listener )
    {
        physics.addStatsChangedListener( listener );
    }
}
