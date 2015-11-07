package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class Solution
{
    public final SolutionStep[] steps;

    public Solution( SolutionStep... steps )
    {
        this.steps = steps;
    }

    public String relFormat()
    {
        StringBuilder sb = new StringBuilder();
        Instruction previousInstruction = null;
        for ( SolutionStep step : steps )
        {
            boolean firstInStep = true;
            for ( Instruction instruction : step.instructions )
            {
                if (previousInstruction != null)
                {
                    firstInStep = (previousInstruction instanceof WaitInstruction);
                }
                sb.append( instruction.relFormat( firstInStep ) );
                previousInstruction = instruction;
            }
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return "Solution( "
            + Util.join( ", ", toStringList( steps ) )
            + " )";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof Solution ) )
        {
            return false;
        }
        Solution otherSolution = (Solution)other;

        return Arrays.deepEquals( steps, otherSolution.steps );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( steps );
    }
}
