package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

public class Entrance extends Thing
{
    private int delay;

    private int timeToNextRabbit;
    private int rabbitsLeft;

    public Entrance( int x, int y )
    {
        super( x, y );
        timeToNextRabbit = 0;
    }

    @Override
    public void init( World world )
    {
        this.delay       = world.rabbitDelay;
        this.rabbitsLeft = world.numRabbits;
    }

    @Override
    public void step( World world )
    {
        if ( rabbitsLeft <=0 )
        {
            return;
        }

        if ( timeToNextRabbit == 0 )
        {
            timeToNextRabbit = delay;
            --rabbitsLeft;

            world.addThing( new Rabbit( x, y + 1, RIGHT ) );
        }
        --timeToNextRabbit;
    }
}
