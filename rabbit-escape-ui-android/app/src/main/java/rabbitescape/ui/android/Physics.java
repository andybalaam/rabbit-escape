package rabbitescape.ui.android;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    private static final long max_allowed_skips = 10;
    private static final long simulation_time_step_ms = 70;

    public int frame;
    public final World world;
    private final rabbitescape.render.Physics impl;

    public Physics( World world, LevelWinListener winListener )
    {
        this.frame = 0;
        this.world = world;
        this.impl = new rabbitescape.render.Physics( world, winListener );
    }

    public long step( long simulation_time, long frame_start_time )
    {
        for ( int skipped = 0; skipped < max_allowed_skips; ++skipped )
        {
            if ( simulation_time >= frame_start_time )
            {
                break;
            }

            ++frame;

            if ( frame == 10 )
            {
                frame = 0;
                impl.step();
            }

            simulation_time += simulation_time_step_ms;
        }

        return simulation_time;
    }

    public boolean gameRunning()
    {
        return impl.gameRunning();
    }

    public int addToken( int tileX, int tileY, Token.Type ability )
    {
        return impl.addToken( tileX, tileY, ability);
    }
}
