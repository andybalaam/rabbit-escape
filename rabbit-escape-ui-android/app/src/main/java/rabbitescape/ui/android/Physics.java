package rabbitescape.ui.android;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    public int frame;
    public final World world;
    private final rabbitescape.render.Physics impl;

    public Physics( World world, LevelWinListener winListener )
    {
        this.frame = 0;
        this.world = world;
        this.impl = new rabbitescape.render.Physics( world, winListener );
    }

    public void step()
    {
        ++frame;

        if ( frame == 10 )
        {
            frame = 0;
            impl.step();
        }
    }

    public boolean finished()
    {
        return world.completionState() != World.CompletionState.RUNNING;
    }


    public int addToken( Token.Type ability, int tileX, int tileY )
    {
        return impl.addToken( ability, tileX, tileY );
    }
}
