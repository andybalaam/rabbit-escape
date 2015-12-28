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
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.Dimension;
import rabbitescape.engine.util.Util;

public class SolutionRunner
{
    /**
     * @return true if the supplied solution solved the level
     */
    public static boolean runSolution( Solution solution, World world, boolean quiet )
        throws SolutionExceptions.ProblemRunningSolution
    {
        SandboxGame sandboxGame = new SandboxGame( world );
        SolutionInterpreter interpreter = new SolutionInterpreter( solution );

        return runSolutionInSandbox( interpreter, sandboxGame, quiet );
    }

    public static void runPartialSolution(
        Solution solution, final SandboxGame sandboxGame )
    {
        SolutionInterpreter interpreter = new SolutionInterpreter(
            solution, false );

        runSolutionInSandbox( interpreter, sandboxGame, true );
    }

    private static boolean runSolutionInSandbox(
        SolutionInterpreter interpreter,
        SandboxGame sandboxGame,
        boolean quiet
    )
    {
        SolutionTimeStep step = interpreter.next(
            sandboxGame.getWorld().completionState() );
        while ( step != null )
        {
            try
            {
                SolutionTimeStep nextStep = interpreter.next(
                    sandboxGame.getWorld().completionState() );
                
                if ( !quiet )
                {
                    printStep( sandboxGame.getWorld() );
                }

                runTimeStep( sandboxGame, step, nextStep );

                step = nextStep;
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

        return sandboxGame.getWorld().completionState().equals(
            CompletionState.WON );
    }

    private static void printStep(World w)
    {
        System.out.println( "Waiting:"+w.num_waiting );
        System.out.println( "  Saved:"+w.num_saved );
        System.out.println
        ( 
            Util.join( "\n", 
                TextWorldManip.renderWorld( 
                    w, false, true ) 
            )
        );
    }
    
    private static void runTimeStep(
        SandboxGame sandboxGame,
        SolutionTimeStep step,
        SolutionTimeStep nextStep
    )
    {
        for ( TimeStepAction action : step.actions )
        {
            performAction( action, sandboxGame );
        }

        try
        {
            if ( shouldStepWorld( nextStep, sandboxGame ) )
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

    /**
     * If we have no next step, or the world is finished and the step is just
     * an assertion step, return false.  Otherwise, true.
     */
    private static boolean shouldStepWorld(
        SolutionTimeStep step, SandboxGame game )
    {
        // TODO: yuck: why do we need an if at all, and why do we have to
        //       tolerate assertions that happen after the world has ended
        //       as well as those that happen as it ends?: it should be one
        //       or the other.

        if ( step == null )
        {
            return false;
        }

        if (
            game.getWorld().completionState() != CompletionState.RUNNING
            && (
                   step.actions.length == 1
                && step.actions[0] instanceof AssertStateAction
            )
        )
        {
            return false;
        }

        return true;
    }

    private static void performAction(
        TimeStepAction action, final SandboxGame sandboxGame )
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
        TimeStepAction action, final SandboxGame sandboxGame )
    throws SolutionExceptions.UnexpectedState
    {
        action.typeSwitch( new TimeStepActionTypeSwitch()
            {
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
