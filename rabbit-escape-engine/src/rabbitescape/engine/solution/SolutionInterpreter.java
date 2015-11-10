package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SolutionInterpreter implements Iterable<SolutionTimeStep>
{
    private final Solution solution;

    public SolutionInterpreter( Solution solution )
    {
        this.solution = solution;
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

            @Override
            public boolean hasNext()
            {
                return (
                       waitTimeLeft > 0
                    || nextAction != null
                );
            }


            @Override
            public SolutionTimeStep next()
            {
                if ( waitTimeLeft > 0 )
                {
                    return waitOneTimeStep();
                }
                else
                {
                    return handleAllActionsInStep();
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
                    SolutionCommand s = commands.next();
                    enteredNextCommand = true;

                    instrs = Arrays.asList( s.actions ).iterator();

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
