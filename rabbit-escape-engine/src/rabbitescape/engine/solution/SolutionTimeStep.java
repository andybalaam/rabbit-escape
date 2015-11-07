package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class SolutionTimeStep
{
    public final Instruction[] instructions;

    public SolutionTimeStep( Instruction... instructions )
    {
        this.instructions = instructions;
    }

    @Override
    public String toString()
    {
        return "SolutionTimeStep( "
            + Util.join( ", ", toStringList( instructions ) )
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

        return Arrays.deepEquals( instructions, otherSolution.instructions );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( instructions );
    }

}
