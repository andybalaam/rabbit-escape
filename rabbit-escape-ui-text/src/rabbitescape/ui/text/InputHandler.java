package rabbitescape.ui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.err.ExceptionTranslation;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.solution.Instruction;
import rabbitescape.engine.solution.SandboxGame;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.solution.SolutionFactory;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.solution.WaitInstruction;
import rabbitescape.engine.solution.SolutionStep;

public class InputHandler
{
    private final SandboxGame sandboxGame;
    private final Terminal terminal;
    private final List<SolutionStep> solution;

    public InputHandler( SandboxGame sandboxGame, Terminal terminal )
    {
        this.sandboxGame = sandboxGame;
        this.terminal = terminal;
        this.solution = new ArrayList<>();
    }

    public boolean handle( int instructionIndex )
    {
        String input = input();

        if ( input.equals( "" ) )
        {
            input = "1";
        }
        else if ( input.equals( "help" ) )
        {
            return help();
        }
        else if ( input.equals( "exit" ) )
        {
            sandboxGame.getWorld().changes.explodeAllRabbits();
            sandboxGame.getWorld().step();
            return true;
        }

        try
        {
            SolutionStep step = SolutionFactory.createStep( input );

            if ( step.instructions.length == 0 )
            {
                return fail( t( "Unexpected problem: no Instruction" ) );
            }

            Instruction lastInstruction = step.instructions[
                step.instructions.length - 1 ];

            if ( !( lastInstruction instanceof WaitInstruction ) )
            {
                WaitInstruction waitInstruction = new WaitInstruction( 1 );
                step = new SolutionStep(
                    concat(
                        step.instructions,
                        new Instruction[] { waitInstruction }
                    )
                );
            }

            for ( Instruction instruction : step.instructions )
            {
                SolutionRunner.performInstruction( instruction, sandboxGame );
            }

            append( step );
        }
        catch ( SolutionExceptions.ProblemRunningSolution e )
        {
            e.stepIndex = instructionIndex;
            e.solutionId = 1;
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }
        catch ( RabbitEscapeException e )
        {
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }

        return true;
    }

    private void append( SolutionStep newStep )
    {
        if ( !solution.isEmpty() )
        {
            SolutionStep lastExistingStep = solution.get( solution.size() - 1 );

            SolutionStep combinedStep = tryToSimplify(
                lastExistingStep, newStep );

            if ( combinedStep != null )
            {
                solution.set( solution.size() - 1, combinedStep );
            }
            else
            {
                solution.add( newStep );
            }
        }
        else
        {
            solution.add( newStep );
        }
    }

    /**
     * Try to combine two steps. If this is not possible then return
     * null.
     */
    private SolutionStep tryToSimplify(
        SolutionStep existingStep, SolutionStep newStep )
    {
        Instruction instruction1 = existingStep.instructions[0];
        Instruction instruction2 = newStep.instructions[0];

        if ( instruction1 instanceof WaitInstruction
            && instruction2 instanceof WaitInstruction )
        {
            WaitInstruction wait1 = (WaitInstruction)instruction1;
            WaitInstruction wait2 = (WaitInstruction)instruction2;
            return new SolutionStep(
                new WaitInstruction( wait1.steps + wait2.steps ) );
        }
        return null;
    }

    private boolean help()
    {
        terminal.out.println( t(
            "\n" +
            "Press return to move forward a time step.\n" +
            "Type 'exit' to stop.\n" +
            "Type an ability name (e.g. 'bash') to switch to that ability.\n" +
            "Type '(x,y)' (e.g '(2,3)') to place a token.\n" +
            "Type a number (e.g. '5') to skip that many steps.\n"
        ) );

        return false;
    }

    private boolean fail( String message )
    {
        terminal.out.println( message );
        return false;
    }

    private String input()
    {
        terminal.out.println(
            t( "Type 'help' then press return for help." ) );

        try
        {
            terminal.out.print( "> " );
            return terminal.in.readLine();
        }
        catch ( IOException e )
        {
            return "";
        }
    }

    public String solution()
    {
        Solution s = new Solution(
            solution.toArray( new SolutionStep[ solution.size() ] ) );

        return s.relFormat();
    }
}
