package rabbitescape.engine.solution;

import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.util.Util;

public class Solution
{
    public final List<Instruction> instructions;

    public Solution( List<Instruction> instructions )
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
        return "Solution( " + Util.join( ", ", instructions ) + ")";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof Solution ) )
        {
            return false;
        }
        Solution otherSolution = (Solution)other;

        return Arrays.deepEquals(
            instructionsArray(), otherSolution.instructionsArray() );
    }

    private Instruction[] instructionsArray()
    {
        return instructions.toArray( new Instruction[ instructions.size() ] );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( instructionsArray() );
    }
}
