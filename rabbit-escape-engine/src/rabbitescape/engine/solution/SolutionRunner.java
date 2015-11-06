package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.solution.SolutionExceptions;

public class SolutionRunner
{
    public static void runSolution( Solution solution, World world )
        throws SolutionExceptions.ProblemRunningSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );

        int i = 1;
        for ( Instruction instruction : solution.instructions )
        {
            try
            {
                performInstruction( instruction, sandboxGame );
            }
            catch ( SolutionExceptions.ProblemRunningSolution e )
            {
                e.instructionIndex = i;
                throw e;
            }
            catch ( DontStepAfterFinish e )
            {
                throw new SolutionExceptions.RanPastEnd(
                    i, sandboxGame.getWorld().completionState() );
            }
            catch ( NoneOfThisAbilityLeft e )
            {
                throw new SolutionExceptions.UsedRunOutAbility( i, e.ability );
            }
            catch ( NoSuchAbilityInThisWorld e )
            {
                throw new SolutionExceptions.UsedMissingAbility( i, e.ability );
            }
            ++i;
        }
    }

    public static void performInstruction(
        Instruction instruction, final SandboxGame sandboxGame )
    throws SolutionExceptions.UnexpectedState
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
                    throws SolutionExceptions.UnexpectedState
                {
                    if (
                        sandboxGame.getWorld().completionState()
                            != s.targetState
                    )
                    {
                        if ( s.targetState == CompletionState.WON )
                        {
                            throw new SolutionExceptions.DidNotWin(
                                sandboxGame.getWorld().completionState() );
                        }
                        else
                        {
                            throw new SolutionExceptions.UnexpectedState(
                                s.targetState,
                                sandboxGame.getWorld().completionState()
                            );
                        }
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
