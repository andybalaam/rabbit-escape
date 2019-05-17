package rabbitescape.render;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.WaterRegion;
import rabbitescape.engine.World;
import rabbitescape.engine.util.DupeStringCounter;
import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.Util.Function;

public class Overlay
{
    private final World world;
    public final List<Thing> items;

    public Overlay( World world )
    {
        this.world = world;

        items = Util.list( Util.chain(
            world.things,
            world.rabbits,
            world.waterTable.getListCopy()
        ) );
    }

    public String at( int x, int y )
    {
        List<WaterRegion> waterRegions = waterRegionsAt( x, y );
        List<Thing> things = world.getThingsAt( x, y );
        Rabbit[] rabbits = world.getRabbitsAt( x, y );

        if ( waterRegions.size() == 0 &&
             things.size() == 0  &&
             rabbits.length == 0 )
        {
            return "";
        }

        Iterable<Thing> thingsHere =
            Util.chain( waterRegions, things, Arrays.asList( rabbits ) );

        Function<Thing,String> textF = new Function<Thing,String>()
        {
            @Override
            public String apply( Thing t )
            {
                return t.overlayText();
            }
        };

        Iterable<String> strings = Util.map(  textF, thingsHere );

        DupeStringCounter dsc = new DupeStringCounter( strings );
        return dsc.join( "\n" );
    }

    private List<WaterRegion> waterRegionsAt( int x, int y )
    {
        if ( x < 0 || y < 0 ||
             x >= world.size.width || y >= world.size.height )
        {
            return Collections.emptyList();
        }
        else
        {
            return world.waterTable.getItemsAt( x, y );
        }
    }
}
