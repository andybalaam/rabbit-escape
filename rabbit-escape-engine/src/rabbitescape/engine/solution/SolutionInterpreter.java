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

            Iterator<Instruction> instrs = null;

            int waitTimeLeft = 0;

            boolean enteredNextCommand = false;

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

                while ( commands.hasNext() )
                {
                    SolutionCommand s = commands.next();
                    enteredNextCommand = true;

                    instrs = Arrays.asList( s.instructions ).iterator();

                    if ( instrs.hasNext() )
                    {
                        return instrs.next();
                    }
                    else
                    {
                        // No instructions in that command - this means wait 1
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
                enteredNextCommand = false;
                while( nextInstruction != null && !enteredNextCommand )
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

                // TODO: annotate time step with the solution command number
                return new SolutionTimeStep(
                    instructions.toArray(
                        new Instruction[ instructions.size() ] )
                );
            }
        };
    }

}
