package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Arrays;

import rabbitescape.engine.util.Util;

public class SolutionCommand
{
    public final CommandAction[] actions;

    public SolutionCommand( CommandAction... actions )
    {
        this.actions = actions;
    }

    @Override
    public String toString()
    {
        return "SolutionCommand( "
            + Util.join( ", ", toStringList( actions ) )
            + " )";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof SolutionCommand ) )
        {
            return false;
        }
        SolutionCommand otherSolution = (SolutionCommand)other;

        return Arrays.deepEquals( actions, otherSolution.actions );
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode( actions );
    }

    public CommandAction lastAction()
    {
        if ( actions.length == 0 )
        {
            return null;
        }
        else
        {
            return actions[ actions.length - 1 ];
        }
    }
}
