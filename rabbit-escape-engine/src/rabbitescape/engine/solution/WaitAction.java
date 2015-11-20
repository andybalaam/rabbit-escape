package rabbitescape.engine.solution;

public class WaitAction implements CommandAction
{
    public final int steps;

    public WaitAction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( steps == 1 )
        {
            return "";
        }
        else
        {
            return String.valueOf( steps );
        }
    }

    @Override
    public String toString()
    {
        return "WaitAction( " + steps + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof WaitAction ) )
        {
            return false;
        }
        WaitAction other = (WaitAction)otherObj;

        return ( steps == other.steps );
    }

    @Override
    public int hashCode()
    {
        return steps;
    }

    @Override
    public void typeSwitch( CommandActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.caseWaitAction( this );
    }
}
