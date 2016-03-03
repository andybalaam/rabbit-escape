package rabbitescape.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class LookupItems2D <T extends LookupItem2D>
{

    private final Vector<T> items;
    public final Position position;

    public LookupItems2D( final Position position )
    {
        items = new Vector<T>( 1, 5 ) ;
        this.position = position;
    }

    public void add( T item )
    {
        items.add( item );
    }

    public void remove( T item )
    {
        items.remove( item );
    }

    public T getItem( int index )
    {
        if ( items.size() > index )
        {
            return items.get( index );
        }
        return null;
    }

    /** Get a list of all the items at the current location. */
    public List<T> getItems()
    {
        return new ArrayList<>( items );
    }
}
