package rabbitescape.engine.solution;

public class PlaceTokenAction implements SolutionAction
{
    public final int x;
    public final int y;

    public PlaceTokenAction( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            return "(" + x + "," + y + ")";
        }
        return SolutionParser.ACTION_DELIMITER + "(" + x + "," + y + ")";
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public String toString()
    {
        return "PlaceTokenAction( " + x + ", " + y + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof PlaceTokenAction ) )
        {
            return false;
        }
        PlaceTokenAction other = (PlaceTokenAction)otherObj;

        return ( x == other.x && y == other.y );
    }

    @Override
    public int hashCode()
    {
        return x + 31 * ( x + y );
    }

    @Override
    public void typeSwitch( ActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.casePlaceTokenAction( this );
    }
}
