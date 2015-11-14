package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import rabbitescape.engine.World.CompletionState;

public class SolutionInterpreter
{
    private final Iterator<SolutionCommand> commandIt;
    private int commandIndex;

    private final WonAssertCreator wonAssert;
    private WaitNextable wait;
    private SolutionCommand command;

    /**
     * appendWon defaults to true if you omit it.
     */
    public SolutionInterpreter( Solution solution )
    {
        this( solution, true );
    }

    /**
     * @param solution
     * @param appendWon add a "WON" assertion at the end, if the last command
     *        is not already a single state assertion.
     */
    public SolutionInterpreter( Solution solution, boolean appendWon )
    {
        this.commandIt = Arrays.asList( solution.commands ).iterator();
        this.commandIndex = 0;

        this.wonAssert = new WonAssertCreator( appendWon );
        this.wait = null;
        this.command = null;
    }

    public SolutionTimeStep next()
    {
        if ( wait != null )
        {
            SolutionTimeStep ret = wait.next();
            if ( ret  != null )
            {
                return ret;
            }
        }

        return nextTimeStep();
    }

    private SolutionTimeStep nextTimeStep()
    {
        wait = null;

        if ( ! commandIt.hasNext() )
        {
            ++commandIndex;
            return wonAssert.create( commandIndex, command );
        }

        command = commandIt.next();
        ++commandIndex;

        int stepsToWait = 0;

        List<SolutionAction> tsActions = new ArrayList<SolutionAction>();

        for ( SolutionAction action : command.actions )
        {
            if ( action instanceof WaitAction )
            {
                WaitAction waitAction = (WaitAction)action;
                stepsToWait  += waitAction.steps;
            }
            else
            {
                tsActions.add( action );
            }
        }

        if ( stepsToWait > 1 )
        {
            wait = new WaitNextable( commandIndex, stepsToWait - 1 );
        }

        return new SolutionTimeStep(
            commandIndex,
            tsActions.toArray( new SolutionAction[ tsActions.size() ] )
        );
    }

    private static class WaitNextable
    {
        private final int commandIndex;
        private int stepsLeft;

        public WaitNextable( int commandIndex, int steps )
        {
            this.commandIndex = commandIndex;
            this.stepsLeft = steps;
        }

        public SolutionTimeStep next()
        {
            if ( stepsLeft > 0 )
            {
                --stepsLeft;
                return new SolutionTimeStep( commandIndex );
            }
            return null;
        }
    }

    private static class WonAssertCreator
    {
        private final boolean appendWon;
        private boolean done;

        public WonAssertCreator( boolean appendWon )
        {
            this.appendWon = appendWon;
            this.done = false;
        }

        public SolutionTimeStep create(
            int commandIndex, SolutionCommand lastCommand )
        {
            if ( done || !appendWon )
            {
                return null;
            }

            if (
                   lastCommand != null
                && lastCommand.actions.length == 1
                && lastCommand.actions[0] instanceof AssertStateAction
            )
            {
                return null;
            }

            done = true;

            return new SolutionTimeStep(
                commandIndex, new AssertStateAction( CompletionState.WON ) );
        }
    }
}
