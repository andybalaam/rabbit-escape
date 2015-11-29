package rabbitescape.engine.util;

import java.util.List;
import java.util.Vector;

public class LookupTable2D <T extends LookupItem2D>
{
    // Arrays of generics not allowed, use Vector instead
    private Vector<Vector<LookupItems2D<T>>> table;
    
    public LookupTable2D( List<T> list, Dimension size )
    {
        table = new Vector<Vector<LookupItems2D<T>>>(size.width);
        for ( int x = 0; x < size.width; x++ )
        {
            table.add( new Vector<LookupItems2D<T>>(size.height) );
            for ( int y = 0; y < size.height; y++ )
            {
                table.get( x ).add( new LookupItems2D<T>( new Position( x, y ) ) );
            }
        }
        for ( T item: list){
            Position position = item.getPosition();
            table.get(position.x).get(position.y).add( item );
        }
    }
    
    /**
     * @return The oldest item at this position.
     */
    public T getItemAt( int x, int y )
    {
        return table.get(x).get(y).getItem( 0 );
    }
}
