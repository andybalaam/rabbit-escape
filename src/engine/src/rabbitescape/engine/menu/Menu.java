package rabbitescape.engine.menu;

import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.util.Util;

public class Menu
{
    public final String intro;
    public final MenuItem[] items;

    public Menu( String intro, MenuItem[] items )
    {
        this.intro = intro;
        this.items = filterNotNull( items );
    }

    private MenuItem[] filterNotNull( MenuItem[] incomingItems )
    {
        Util.Function<MenuItem, Boolean> notNull =
            new Util.Function<MenuItem, Boolean>()
        {
            @Override
            public Boolean apply( MenuItem item )
            {
                return ( item != null );
            }
        };

        List<MenuItem> filtered = Util.list(
            Util.filter( notNull, Arrays.asList( incomingItems ) )
        );

        return filtered.toArray( new MenuItem[ filtered.size() ] );
    }

    /**
     * Overridden by subclasses that need to refresh their items when
     * something changes.
     */
    public void refresh()
    {
    }
}
