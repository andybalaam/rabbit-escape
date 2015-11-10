package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class SolutionTimeStep
{
    public final SolutionAction[] actions;

    public SolutionTimeStep( SolutionAction... actions )
    {
        this.actions = actions;
    }

    @Override
    public String toString()
    {
        return "SolutionTimeStep( "
            + Util.join( ", ", toStringList( actions ) )
            + " )";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof SolutionTimeStep ) )
        {
            return false;
        }
        SolutionTimeStep otherSolution = (SolutionTimeStep)other;

        return Arrays.deepEquals( actions, otherSolution.actions );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( actions );
    }

}