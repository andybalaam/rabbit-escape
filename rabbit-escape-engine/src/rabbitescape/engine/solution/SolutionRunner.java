package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;

public class SolutionRunner
{
    public static void runSolution( Solution solution, World world )
        throws InvalidSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );

        for ( Instruction instruction : solution.instructions )
        {
            try
            {
                instruction.performOn( sandboxGame );
            }
            catch ( DontStepAfterFinish e )
            {
                throw new InvalidSolution( "Solution " + solution.solutionId
                    + " has steps that continue after the level has finished.",
                    e );
            }
            catch ( NoneOfThisAbilityLeft e )
            {
                throw new InvalidSolution( "Solution " + solution.solutionId
                    + " attempts to use more " + sandboxGame.getSelectedType()
                    + " tokens than are available.",
                    e );
            }
            catch ( NoSuchAbilityInThisWorld e )
            {
                throw new InvalidSolution(
                    "Solution " + solution.solutionId
                        + " attempts to the ability '"
                        + sandboxGame.getSelectedType()
                        + "' which is not available in this world",
                    e );
            }
        }
    }

}
