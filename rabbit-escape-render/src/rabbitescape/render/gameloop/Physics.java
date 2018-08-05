package rabbitescape.render.gameloop;

import rabbitescape.engine.World;

public interface Physics
{
    public static interface StatsChangedListener
    {
        void changed( int waiting, int out, int saved );
    }

    long step( long simulation_time, long frame_start_time );
    int frameNumber();
    boolean gameRunning();
    void dispose();
    World world();
    void init();
}
