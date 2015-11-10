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
            Iterator<SolutionStep> steps = Arrays.asList(
                solution.steps ).iterator();

            Iterator<Instruction> instrs = null;

            int waitTimeLeft = 0;

            boolean enteredNextStep = false;

            Instruction nextInstruction = rollToNextInstruction();

            @Override
            public boolean hasNext()
            {
                return (
                       waitTimeLeft > 0
                    || nextInstruction != null
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
                    return handleAllInstructionsInStep();
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            private Instruction rollToNextInstruction()
            {
                if ( instrs != null && instrs.hasNext() )
                {
                    return instrs.next();
                }

                while ( steps.hasNext() )
                {
                    SolutionStep s = steps.next();
                    enteredNextStep = true;

                    instrs = Arrays.asList( s.instructions ).iterator();

                    if ( instrs.hasNext() )
                    {
                        return instrs.next();
                    }
                    else
                    {
                        // No instructions in that step - this means wait 1
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

            private SolutionTimeStep handleAllInstructionsInStep()
            {
                ArrayList<Instruction> instructions =
                    new ArrayList<Instruction>();

                boolean alreadyWaited = false;
                enteredNextStep = false;
                while( nextInstruction != null && !enteredNextStep )
                {
                    if ( nextInstruction instanceof WaitInstruction )
                    {
                        WaitInstruction wait = (WaitInstruction)nextInstruction;
                        waitTimeLeft += wait.steps;
                        if ( !alreadyWaited )
                        {
                            --waitTimeLeft;
                            alreadyWaited = true;
                        }
                    }
                    else
                    {
                        instructions.add( nextInstruction );
                    }
                    nextInstruction = rollToNextInstruction();
                }

                // TODO: annotate time step with the solution step number
                // TODO: rename solution step to a clearer name
                return new SolutionTimeStep(
                    instructions.toArray(
                        new Instruction[ instructions.size() ] )
                );
            }
        };
    }

}
