package rabbitescape.ui.android;

import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    public int frame;
    public final World world;
    private final LevelWinListener winListener;
    private final rabbitescape.render.Physics.WorldModifier worldModifier;

    public Physics( World world, LevelWinListener winListener )
    {
        this.frame = 0;
        this.world = world;
        this.winListener = winListener;
        this.worldModifier = new rabbitescape.render.Physics.WorldModifier( world );
    }

    public void step()
    {
        ++frame;

        if ( frame == 10 )
        {
            frame = 0;
            worldModifier.step();
            checkWon();
        }
    }

    private void checkWon()
    {
        switch ( world.completionState() )
        {
            case WON:
            {
                winListener.won();
                break;
            }
            case LOST:
            {
                winListener.lost();
                break;
            }
            default:
            {
                break;
            }
        }
    }

    public boolean finished()
    {
        return world.completionState() != World.CompletionState.RUNNING;
    }


    public int addToken( Token.Type ability, int tileX, int tileY )
    {
        if ( world.abilities.get( ability ) > 0 )
        {
            worldModifier.addToken( tileX, tileY, ability );
        }

        return world.abilities.get( ability );
    }
}
