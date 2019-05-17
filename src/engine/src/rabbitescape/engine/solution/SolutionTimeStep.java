package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class SolutionTimeStep
{
    public final int commandIndex;
    public final TimeStepAction[] actions;

    public SolutionTimeStep( int commandIndex, TimeStepAction... actions )
    {
        this.commandIndex = commandIndex;
        this.actions = actions;
    }

    @Override
    public String toString()
    {
        return "SolutionTimeStep( "
            + commandIndex + ( actions.length > 0 ? ", " : "" )
            + Util.join( ", ", toStringList( actions ) )
            + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof SolutionTimeStep ) )
        {
            return false;
        }
        SolutionTimeStep other = (SolutionTimeStep)otherObj;

        return (
               commandIndex == other.commandIndex
            && Arrays.deepEquals( actions, other.actions )
        );
    }

    @Override
    public int hashCode()
    {
        return ( 31 * commandIndex ) + Arrays.deepHashCode( actions );
    }

}
