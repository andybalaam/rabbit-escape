package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.Direction.*;

import java.util.HashMap;
import java.util.Map;

public class Entrance extends Thing
{
    private int[] delay = new int[1];

    private int timeToNextRabbit;

    private int rabbitEntranceCount = 0;

    private World world;

    public Entrance( int x, int y )
    {
        super( x, y, ENTRANCE );
        delay[0] = -1;
        timeToNextRabbit = 0;
        rabbitEntranceCount = 0;
    }

    @Override
    public void calcNewState( World world )
    {
        if ( delay[0] == -1 )
        {
            delay = world.rabbit_delay;
        }
    }

    @Override
    public void step( World world )
    {
        this.world = world;
        if ( world.num_waiting <= 0 )
        {
            return;
        }

        if ( timeToNextRabbit == 0 )
        {
            birthRabbit( world );
        }
        --timeToNextRabbit;
    }

    private void birthRabbit( World world )
    {
        int delayIndex;

        delayIndex = rabbitEntranceCount >= delay.length
                     ? delay.length-1 : rabbitEntranceCount ;

        rabbitEntranceCount++ ;

        timeToNextRabbit = delay[delayIndex];

        Rabbit r = new Rabbit( x, y + 1, RIGHT, Rabbit.Type.RABBIT );

        world.changes.enterRabbit( r );

        world.rabbitIndex( r );
    }

    @Override
    public Map<String, String> saveState( boolean runtimeMeta )
    {
        Map<String, String> ret = new HashMap<String, String>();
        BehaviourState.addToStateIfGtZero(
            ret, "Entrance.timeToNextRabbit", timeToNextRabbit
        );
        return ret;
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
        timeToNextRabbit = BehaviourState.restoreFromState(
            state, "Entrance.timeToNextRabbit", timeToNextRabbit
        );
    }

    @Override
    public String overlayText()
    {
        return null == world
                       ? "Entrance"
                       : "Entrance\n" + world.num_waiting + " to come";
    }
}
