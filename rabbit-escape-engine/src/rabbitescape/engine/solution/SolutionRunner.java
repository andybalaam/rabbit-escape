package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.Token.Type;
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
                performInstruction( instruction, sandboxGame );
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

    public static void performInstruction(
        Instruction instruction, final SandboxGame sandboxGame )
    {
        instruction.typeSwitch( new InstructionTypeSwitch()
            {
                @Override
                public void caseWaitInstruction( WaitInstruction w )
                {
                    for ( int i = 0; i < w.steps; i++ )
                    {
                        sandboxGame.getWorld().step();
                    }
                }

                @Override
                public void caseSelectInstruction( SelectInstruction s )
                {
                    sandboxGame.setSelectedType( s.type );
                }

                @Override
                public void caseTargetState( TargetState s )
                {
                    if (
                        sandboxGame.getWorld().completionState()
                            != s.targetState
                    )
                    {
                        throw new InvalidSolution( "Solution " + s.solutionId
                            + " did not cause " + s.targetState
                            + " at instruction " + s.instructionIndex );
                    }
                }

                @Override
                public void casePlaceTokenInstruction( PlaceTokenInstruction p )
                {
                    Type type = sandboxGame.getSelectedType();
                    sandboxGame.getWorld().changes.addToken( p.x, p.y, type );
                }
            }
        );
    }
}
