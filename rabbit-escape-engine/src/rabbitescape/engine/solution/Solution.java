package rabbitescape.engine.solution;

import java.util.Arrays;
import java.util.List;

import rabbitescape.engine.World;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.util.Util;

public class Solution
{
    private final int solutionId;
    private final List<Instruction> instructions;

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

    @Override
    public String toString()
    {
        return "Solution( "
            + solutionId
            + ", [ " + Util.join( ", ", instructions )
            + " ] )";
    }

    @Override
    public boolean equals( Object other )
    {
        if ( ! ( other instanceof Solution ) )
        {
            return false;
        }
        Solution otherSolution = (Solution)other;

        return (
            solutionId == otherSolution.solutionId
            &&
            Arrays.deepEquals(
                instructionsArray(), otherSolution.instructionsArray() )
        );
    }

    private Instruction[] instructionsArray()
    {
        return instructions.toArray( new Instruction[ instructions.size() ] );
    }

    @Override
    public int hashCode()
    {
        return solutionId + Arrays.deepHashCode( instructionsArray() );
    }
}
