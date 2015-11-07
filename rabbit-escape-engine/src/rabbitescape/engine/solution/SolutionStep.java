package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class SolutionStep
{
    public final Instruction[] instructions;

    public SolutionStep( Instruction... instructions )
    {
        this.instructions = instructions;
    }

    @Override
    public String toString()
    {
        return "SolutionStep( "
            + Util.join( ", ", toStringList( instructions ) )
            + " )";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof SolutionStep ) )
        {
            return false;
        }
        SolutionStep otherSolution = (SolutionStep)other;

        return Arrays.deepEquals( instructions, otherSolution.instructions );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( instructions );
    }
}
