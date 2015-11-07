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
            Iterator<Instruction> it = Arrays.asList(
                solution.instructions ).iterator();

            int waitTimeLeft = 0;

            @Override
            public boolean hasNext()
            {
                return
                (
                       waitTimeLeft > 0
                    || it.hasNext()
                );
            }

            @Override
            public SolutionTimeStep next()
            {
                if ( waitTimeLeft > 0 )
                {
                    return waitThisTimeStep();
                }
                else
                {
                    Instruction instruction = it.next();
                    if ( instruction instanceof WaitInstruction )
                    {
                        waitTimeLeft = ( (WaitInstruction)instruction ).steps;
                        return waitThisTimeStep();
                    }
                    else
                    {
                        throw new RuntimeException( "boo" );
                    }
                }
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException();
            }

            private SolutionTimeStep waitThisTimeStep()
            {
                --waitTimeLeft;
                return new SolutionTimeStep();
            }
        };
    }

}
