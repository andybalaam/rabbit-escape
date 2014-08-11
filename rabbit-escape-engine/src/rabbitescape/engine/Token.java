package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

public class Token extends Thing
{
    public static enum Type
    {
        bash,
        dig,
    }

    public final Type type;

    public Token( int x, int y, Type type )
    {
        super( x, y, TOKEN_STILL );
        this.type = type;
    }

    @Override
    public void calcNewState( World world )
    {
        if ( world.flatBlockAt( x, y + 1 ) )
        {
            state = TOKEN_STILL;
        }
        else
        {
            state = TOKEN_FALLING;
        }
    }

    @Override
    public void step( World world )
    {
        if ( state == TOKEN_FALLING )
        {
            ++y;
        }
    }
}
