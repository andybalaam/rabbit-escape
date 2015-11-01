package rabbitescape.engine.solution;

import java.util.List;

import rabbitescape.engine.World;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;

public class Solution
{
    private int solutionId;
    private List<Instruction> instructions;

    public Solution( int solutionId, List<Instruction> instructions )
    {
        this.solutionId = solutionId;
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
            catch ( NoneOfThisAbilityLeft e )
            {
                throw new InvalidSolution( "Solution " + solutionId
                    + " attempts to use more " + sandboxGame.getSelectedType()
                    + " tokens than are available.",
                    e );
            }
            catch ( NoSuchAbilityInThisWorld e )
            {
                throw new InvalidSolution(
                    "Solution " + solutionId
                        + " attempts to the ability '"
                        + sandboxGame.getSelectedType()
                        + "' which is not available in this world",
                    e );
            }
        }
    }
}
