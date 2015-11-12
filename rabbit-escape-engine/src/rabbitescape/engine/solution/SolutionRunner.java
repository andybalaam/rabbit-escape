package rabbitescape.engine.solution;

import static rabbitescape.engine.util.Util.*;

import java.util.Iterator;

import rabbitescape.engine.World;
import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CantAddTokenOutsideWorld;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.World.DontStepAfterFinish;
import rabbitescape.engine.World.NoSuchAbilityInThisWorld;
import rabbitescape.engine.World.NoneOfThisAbilityLeft;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.Dimension;

public class SolutionRunner
{

    public static void runSolution( Solution solution, World world )
        throws SolutionExceptions.ProblemRunningSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );
        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        Iterator<SolutionTimeStep> it = interpreter.iterator();
        while ( it.hasNext() )
        {
            SolutionTimeStep step  = it.next();
            try
            {
                SolutionAction lastAction = new WaitAction( 1 );
                for ( SolutionAction action : step.actions )
                {
                    performAction( action, sandboxGame );
                    lastAction = action;
                }

                try
                {
                    // TODO: this is messy - interpreter runs for 1 more step than
                    //       the world!
                    if ( it.hasNext() && !( lastAction instanceof UntilAction ) )
                    {
                        sandboxGame.getWorld().step();
                    }
                }
                catch ( DontStepAfterFinish e )
                {
                    throw new SolutionExceptions.RanPastEnd(
                        sandboxGame.getWorld().completionState() );
                }
            }
            catch ( SolutionExceptions.ProblemRunningSolution e )
            {
                e.commandIndex = step.commandIndex;
                e.world = join(
                    "\n",
                    TextWorldManip.renderWorld(
                        sandboxGame.getWorld(), false, false )
                );
                throw e;
            }
        }
    }

    public static void performAction(
        SolutionAction action, final SandboxGame sandboxGame )
    throws SolutionExceptions.UnexpectedState
    {
        // TODO: stop using WaitAction to step in the command line interface -
        // then we can stop catching DontStepAfterFinish.

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
                    // TODO: delete this, and make a SolutionAction interface
                    //       that does not include wait actions, and delete
                    //       related code.
                    for ( int i = 0; i < w.steps; i++ )
                    {
                        sandboxGame.getWorld().step();
                    }
                }
                
                @Override
                public void caseUntilAction( UntilAction u )
                {
                    World w = sandboxGame.getWorld();
                    for ( int i = 0; w.completionState() != u.completionState; i++ )
                    {
                        w.step();
                        if ( i >= UntilAction.maxSteps ) {
                            throw new InvalidAction( "action " + u + " got to " + i + " steps" );
                        }
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
