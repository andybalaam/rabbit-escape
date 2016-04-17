package rabbitescape.render;

import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.World;
import rabbitescape.engine.util.DupeStringCounter;
import rabbitescape.engine.util.Util;

public class ThingStrings
{
    private final World world;

    public ThingStrings( World world )
    {
        this.world = world;
    }

    public String at( int x, int y )
    {
        List<Thing> things = world.getThingsAt( x, y );
        Rabbit[] rabbits = world.getRabbitsAt( x, y );

        if ( things.size() == 0  && rabbits.length == 0 )
        {
            return null;
        }

        DupeStringCounter dsc = new DupeStringCounter(
            Util.chain( things, Arrays.asList( rabbits ) ) );
        return dsc.join( "\n" );
    }
}
