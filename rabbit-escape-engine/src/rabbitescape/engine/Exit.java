package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;

public class Exit extends Thing
{
    public Exit( int x, int y )
    {
        super( x, y, EXIT );
    }

    @Override
    public void calcNewState( World world )
    {
    }

    @Override
    public void step( World world )
    {
    }
}
