package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.World;
import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CantAddTokenOutsideWorld;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.util.Dimension;

public class SolutionRunner
{
    public static void runSolution( Solution solution, World world )
        throws SolutionExceptions.ProblemRunningSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );

        for (
            IdxObj<SolutionCommand> command : enumerate1( solution.commands ) )
        {
            try
            {
                for ( SolutionAction action : command.object.actions )
                {
                    performAction( action, sandboxGame );
                }
            }
            catch ( SolutionExceptions.ProblemRunningSolution e )
            {
                e.commandIndex = command.index;
                throw e;
            }
        }
    }

    public static void performAction(
        SolutionAction action, final SandboxGame sandboxGame )
    throws SolutionExceptions.UnexpectedState
    {
        try
        {
            doPerformAction( action, sandboxGame );
        }
        catch ( DontStepAfterFinish e )
        {
            throw new SolutionExceptions.RanPastEnd(
                sandboxGame.getWorld().completionState() );
        }
        catch ( NoneOfThisAbilityLeft e )
        {
            throw new SolutionExceptions.UsedRunOutAbility( e.ability );
        }
        catch ( NoSuchAbilityInThisWorld e )
        {
            throw new SolutionExceptions.UsedMissingAbility( e.ability );
        }
        catch ( CantAddTokenOutsideWorld e )
        {
            Dimension worldSize = sandboxGame.getWorld().size;
            throw new SolutionExceptions.PlacedTokenOutsideWorld(
                e.x, e.y, worldSize.width, worldSize.height );
        }
    }

    private static void doPerformAction(
        SolutionAction action, final SandboxGame sandboxGame )
    throws SolutionExceptions.UnexpectedState
    {
        action.typeSwitch( new ActionTypeSwitch()
            {
                @Override
                public void caseWaitAction( WaitAction w )
                {
                    for ( int i = 0; i < w.steps; i++ )
                    {
                        sandboxGame.getWorld().step();
                    }
                }

                @Override
                public void caseSelectAction( SelectAction s )
                {
                    // TODO: check whether this ability exists, and throw if
                    //       not.
                    sandboxGame.setSelectedType( s.type );
                }

                @Override
                public void caseAssertStateAction( AssertStateAction s )
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
                public void casePlaceTokenAction( PlaceTokenAction p )
                {
                    Type type = sandboxGame.getSelectedType();
                    World world = sandboxGame.getWorld();

                    Integer previousNum = world.abilities.get( type );
                    // (Note: previousNum may be null, so can't be int.)

                    world.changes.addToken( p.x, p.y, type );

                    if ( world.abilities.get( type ) == previousNum )
                    {
                        throw new SolutionExceptions.FailedToPlaceToken(
                            p.x, p.y, type );
                    }
                }
            }
        );
    }
}
