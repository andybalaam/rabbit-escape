package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class Solution
{
    public final SolutionCommand[] commands;

    public Solution( SolutionCommand... commands )
    {
        this.commands = commands;
    }

    public String relFormat()
    {
        StringBuilder sb = new StringBuilder();
        Instruction previousInstruction = null;
        for ( SolutionCommand command : commands )
        {
            boolean firstInCommand = true;
            for ( Instruction instruction : command.instructions )
            {
                if (previousInstruction != null)
                {
                    firstInCommand = (previousInstruction instanceof WaitInstruction);
                }
                sb.append( instruction.relFormat( firstInCommand ) );
                previousInstruction = instruction;
            }
        }
        return sb.toString();
    }

    @Override
    public String toString()
    {
        return "Solution( "
            + Util.join( ", ", toStringList( commands ) )
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

        return Arrays.deepEquals( commands, otherSolution.commands );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( commands );
    }
}
