package rabbitescape.engine.solution;

import rabbitescape.engine.World;

public class WaitAction implements SolutionAction
{
    public final int steps;

    public WaitAction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            return String.valueOf( steps ) + SolutionParser.COMMAND_DELIMITER;
        }
        else if ( steps == 1 )
        {
            return SolutionParser.COMMAND_DELIMITER;
        }
        else if ( steps > 1 )
        {
            return
                  SolutionParser.COMMAND_DELIMITER
                + String.valueOf( steps - 1 )
                + SolutionParser.COMMAND_DELIMITER;
        }
        else
        {
            throw new IllegalArgumentException(
                "Waiting for non-positive number of steps: " + steps );
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
    public void perform( SandboxGame sbg, World w )
    {
        for ( int i = 0; i < steps; i++ )
        {
            w.step();
        }
    }

}
