package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

public class Entrance extends Thing
{
    public Entrance( int x, int y )
    {
        super( x, y );
    }

    @Override
    public void init( World world )
    {
    }

    @Override
    public void step( World world )
    {
        world.addThing( new Rabbit( x, y + 1, RIGHT ) );
    }
}
