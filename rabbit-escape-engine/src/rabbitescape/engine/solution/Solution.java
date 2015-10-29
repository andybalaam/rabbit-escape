package rabbitescape.engine.solution;

import java.util.List;

import rabbitescape.engine.World;
import rabbitescape.engine.World.DontStepAfterFinish;

public class Solution
{
    private int solutionId;
    private List<Instruction> instructions;

    public Solution( List<Instruction> instructions )
    {
        this.instructions = instructions;
    }

    public void checkSolution( World world ) throws InvalidSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );
        
        for ( Instruction instruction : instructions )
        {
            try
            {
                instruction.performOn( sandboxGame );
            }
            catch ( DontStepAfterFinish e )
            {
                throw new InvalidSolution( "Solution " + solutionId
                    + " has steps that continue after the level has finished.",
                    e );
            }
        }
    }
}
