package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import rabbitescape.engine.World.CompletionState;

public class SolutionInterpreter implements Iterable<SolutionTimeStep>
{
    private final Solution solution;
    private final boolean appendWon;

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
        this.solution = solution;
        this.appendWon = appendWon;
    }

    @Override
    public Iterator<SolutionTimeStep> iterator()
    {
        return new Iterator<SolutionTimeStep>()
        {
            Iterator<SolutionCommand> commands = Arrays.asList(
                solution.commands ).iterator();

            Iterator<SolutionAction> instrs = null;

            int waitTimeLeft = 0;

            boolean enteredNextCommand = false;

            SolutionAction nextAction = rollToNextAction();

            SolutionCommand lastCommand = null;

            private boolean alreadyAppendedLastAssertion = false;

            @Override
            public boolean hasNext()
            {
                return (
                       waitTimeLeft > 0
                    || nextAction != null
                    || ( appendWon && willAppendLastAssertion() )
                );
            }

            private boolean willAppendLastAssertion()
            {
                if ( alreadyAppendedLastAssertion  )
                {
                    return false;
                }
                else
                {
                    return !( isSingleStateAssertion( lastCommand ) );
                }
            }

            private boolean isSingleStateAssertion( SolutionCommand command )
            {
                if ( command == null )
                {
                    return false;
                }
                else
                {
                    return (
                           ( command.actions.length == 1 )
                        && ( command.actions[0] instanceof ValidationAction )
                    );
                }
            }

            @Override
            public SolutionTimeStep next()
            {
                if ( waitTimeLeft > 0 )
                {
                    return waitOneTimeStep();
                }
                else if ( nextAction != null )
                {
                    return handleAllActionsInStep();
                }
                else
                {
                    alreadyAppendedLastAssertion = true;
                    return new SolutionTimeStep(
                        new AssertStateAction( CompletionState.WON ) );
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            private SolutionAction rollToNextAction()
            {
                if ( instrs != null && instrs.hasNext() )
                {
                    return instrs.next();
                }

                while ( commands.hasNext() )
                {
                    lastCommand = commands.next();
                    enteredNextCommand = true;

                    instrs = Arrays.asList( lastCommand.actions ).iterator();

                    if ( instrs.hasNext() )
                    {
                        return instrs.next();
                    }
                    else
                    {
                        // No action in that command - this means wait 1
                        ++waitTimeLeft;
                    }
                }

                return null;
            }

            private SolutionTimeStep waitOneTimeStep()
            {
                --waitTimeLeft;
                return new SolutionTimeStep();
            }

            private SolutionTimeStep handleAllActionsInStep()
            {
                ArrayList<SolutionAction> actions =
                    new ArrayList<SolutionAction>();

                boolean alreadyWaited = false;
                enteredNextCommand = false;
                while( nextAction != null && !enteredNextCommand )
                {
                    if ( nextAction instanceof WaitAction )
                    {
                        WaitAction wait = (WaitAction)nextAction;
                        waitTimeLeft += wait.steps;
                        if ( !alreadyWaited )
                        {
                            --waitTimeLeft;
                            alreadyWaited = true;
                        }
                    }
                    else
                    {
                        actions.add( nextAction );
                    }
                    nextAction = rollToNextAction();
                }

                // TODO: annotate time step with the solution command number
                return new SolutionTimeStep(
                    actions.toArray(
                        new SolutionAction[ actions.size() ] )
                );
            }
        };
    }

}
