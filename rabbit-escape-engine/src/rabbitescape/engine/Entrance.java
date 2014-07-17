package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

public class Entrance extends Thing
{
    private int delay;

    private int timeToNextRabbit;

    public Entrance( int x, int y )
    {
        super( x, y );
        timeToNextRabbit = 0;
    }

    @Override
    public void init( World world )
    {
        this.delay = world.rabbitDelay;
    }

    @Override
    public void step( World world )
    {
        if ( world.rabbitsStillToEnter <= 0 )
        {
            return;
        }

        if ( timeToNextRabbit == 0 )
        {
            timeToNextRabbit = delay;
            --world.rabbitsStillToEnter;

            world.addRabbit( new Rabbit( x, y + 1, RIGHT ) );
        }
        --timeToNextRabbit;
    }
}
