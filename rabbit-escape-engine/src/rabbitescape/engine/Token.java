package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

public class Token extends Thing
{
    public static enum Type
    {
        bash,
    }

    public final Type type;

    public Token( int x, int y, Type type )
    {
        super( x, y );
        this.type = type;
    }

    @Override
    public void init( World world )
    {
        calcNewState( world );
    }

    @Override
    public void step( World world )
    {
        if ( state == TOKEN_FALLING )
        {
            ++y;
        }

        calcNewState( world );
    }

    private void calcNewState( World world )
    {
        if ( world.flatBlockAt( x, y + 1 ) )
        {
            state = NOT_CHANGING;
        }
        else
        {
            state = TOKEN_FALLING;
        }
    }
}
