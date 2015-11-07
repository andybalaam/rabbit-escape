package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class Solution
{
    public final Instruction[] instructions;

    public Solution( Instruction... instructions )
    {
        this.instructions = instructions;
    }

    public String relFormat()
    {
        StringBuilder sb = new StringBuilder();
        boolean firstInStep = true;
        Instruction previousInstruction = null;
        for ( Instruction instruction : instructions )
        {
            if (previousInstruction != null)
            {
                firstInStep = (previousInstruction instanceof WaitInstruction);
            }
            sb.append( instruction.relFormat( firstInStep ) );
            previousInstruction = instruction;
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return "Solution( "
            + Util.join( ", ", toStringList( instructions ) )
            + ")";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof Solution ) )
        {
            return false;
        }
        Solution otherSolution = (Solution)other;

        return Arrays.deepEquals( instructions, otherSolution.instructions );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( instructions );
    }
}
