package rabbitescape.ui.android;

import rabbitescape.engine.World;

public class Physics
{
    private final World world;
    public int frame;

    public Physics( World world )
    {
        this.frame = 0;
        this.world = world;
    }

    public void step()
    {
        ++frame;

        if ( frame == 10 )
        {
            frame = 0;
            world.step();
        }
    }

    public boolean finished()
    {
        return world.finished();
    }
}
