package rabbitescape.engine.solution;

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
                    Instruction thisInstruction = nextInstruction;
                    nextInstruction = rollToNextInstruction();
                    return handleInstruction( thisInstruction );
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
                    instrs = Arrays.asList( s.instructions ).iterator();

                    if ( instrs.hasNext() )
                    {
                        return instrs.next();
                    }
                }

                return null;
            }

            private SolutionTimeStep waitOneTimeStep()
            {
                --waitTimeLeft;
                return new SolutionTimeStep();
            }

            private SolutionTimeStep handleInstruction(
                Instruction instruction )
            {
                if ( instruction instanceof WaitInstruction )
                {
                    waitTimeLeft = ( (WaitInstruction)instruction ).steps;
                    return waitOneTimeStep();
                }
                else
                {
                    throw new RuntimeException( "boo" );
                }
            }
        };
    }

}
