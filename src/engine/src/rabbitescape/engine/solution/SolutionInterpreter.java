package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.solution.SolutionExceptions.UnexpectedState;

public class SolutionInterpreter
{
    private static final int maxUntils = 1000;

    private final Iterator<SolutionCommand> commandIt;
    private int commandIndex;

    private final WonAssertCreator wonAssert;
    private WaitNextable wait;
    private CompletionState untilState;
    private SolutionCommand command;
    private int untilCount;
    public boolean emptySteps = false;

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
        this.untilState = null;
        this.command = null;
        this.untilCount = -1;
    }

    public static SolutionInterpreter getNothingPlaying()
    {
        SolutionInterpreter nothingPlaying =
            new SolutionInterpreter( SolutionParser.parse( "1" ) );
        nothingPlaying.emptySteps = true;
        return nothingPlaying;
    }

    public SolutionTimeStep next( CompletionState worldState )
    {
        if ( emptySteps )
        {
            return new SolutionTimeStep(
                ++commandIndex, new TimeStepAction[] {} );
        }
        if ( wait != null )
        {
            SolutionTimeStep ret = wait.next();
            if ( ret  != null )
            {
                return ret;
            }
        }

        if ( untilState != null )
        {
            if ( worldState != CompletionState.RUNNING )
            {
                SolutionTimeStep ret = new SolutionTimeStep(
                    commandIndex, new AssertStateAction( untilState ) );
                untilState = null;
                untilCount = -1;
                wonAssert.done = true;
                return ret;
            }
            else if ( untilCount > maxUntils )
            {
                throw new SolutionExceptions.UntilActionNeverEnded(
                    untilState );
            }
            else
            {
                ++untilCount;
                return new SolutionTimeStep( commandIndex );
            }
        }

        return nextTimeStep( worldState );
    }

    private SolutionTimeStep nextTimeStep( final CompletionState worldState )
    {
        wait = null;

        if ( ! commandIt.hasNext() )
        {
            ++commandIndex;
            return wonAssert.create( commandIndex, command );
        }

        command = commandIt.next();
        ++commandIndex;

        // Work around need for final variable
        class StepsToWait { int value = 0; };
        final StepsToWait stepsToWait = new StepsToWait();

        final List<TimeStepAction> tsActions = new ArrayList<TimeStepAction>();

        for ( CommandAction action : command.actions )
        {
            action.typeSwitch( new CommandActionTypeSwitch()
            {
                @Override
                public void caseWaitAction( WaitAction waitAction )
                {
                    stepsToWait.value  += waitAction.steps;
                }

                @Override
                public void caseUntilAction( UntilAction untilAction )
                {
                    untilState = untilAction.targetState;
                    untilCount = 0;

                    if ( worldState != CompletionState.RUNNING )
                    {
                        tsActions.add( new AssertStateAction( untilState ) );
                        wonAssert.done = true;
                        untilState = null;
                    }
                }

                @Override
                public void caseSelectAction( SelectAction action )
                {
                    tsActions.add( action );
                }

                @Override
                public void casePlaceTokenAction( PlaceTokenAction action )
                {
                    tsActions.add( action );
                }

                @Override
                public void caseAssertStateAction( AssertStateAction action )
                    throws UnexpectedState
                {
                    tsActions.add( action );
                }
                // Curse you, Java, for making me this way
            } );
        }

        if ( stepsToWait.value > 1 )
        {
            wait = new WaitNextable( commandIndex, stepsToWait.value - 1 );
        }

        return new SolutionTimeStep(
            commandIndex,
            tsActions.toArray( new TimeStepAction[ tsActions.size() ] )
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
        public boolean done;

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
