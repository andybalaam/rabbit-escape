package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.Util.Function;

public class Solution
{
    public final SolutionCommand[] commands;

    public Solution( SolutionCommand... commands )
    {
        this.commands = commands;
    }

    public String relFormat()
    {
        return join( ";", map( relFormatCommand(), commands ) );
    }

    private Function<SolutionCommand, String> relFormatCommand()
    {
        return new Function<SolutionCommand, String>()
        {
            @Override
            public String apply( SolutionCommand command )
            {
                return join( "&", map( relFormatAction(), command.actions ) );
            }
        };
    }

    protected Function<CommandAction, String> relFormatAction()
    {
        return new Function<CommandAction, String>()
        {
            @Override
            public String apply( CommandAction action )
            {
                return action.relFormat( true );
            }
        };
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
