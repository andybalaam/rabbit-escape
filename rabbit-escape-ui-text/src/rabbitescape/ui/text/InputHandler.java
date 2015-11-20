package rabbitescape.ui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.*;
import rabbitescape.engine.err.ExceptionTranslation;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.solution.CommandAction;
import rabbitescape.engine.solution.SandboxGame;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionExceptions;
import rabbitescape.engine.solution.SolutionParser;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.solution.WaitAction;
import rabbitescape.engine.solution.SolutionCommand;

public class InputHandler
{
    private final SandboxGame sandboxGame;
    private final Terminal terminal;
    private final List<SolutionCommand> solution;

    public InputHandler( SandboxGame sandboxGame, Terminal terminal )
    {
        this.sandboxGame = sandboxGame;
        this.terminal = terminal;
        this.solution = new ArrayList<>();
    }

    public boolean handle( int commandIndex )
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
            SolutionCommand command = SolutionParser.parseCommand( input );

            if ( command.actions.length == 0 )
            {
                return fail( t( "Unexpected problem: no Action" ) );
            }

            SolutionRunner.runSingleCommand( command, sandboxGame );
            // TODO: it's weird we have to do the last time step
            //       outside of runSingleCommand
            sandboxGame.getWorld().step();

            append( command );
        }
        catch ( SolutionExceptions.ProblemRunningSolution e )
        {
            e.commandIndex = commandIndex;
            e.solutionId = 1;
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }
        catch ( RabbitEscapeException e )
        {
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }

        return true;
    }

    private void append( SolutionCommand newStep )
    {
        if ( !solution.isEmpty() )
        {
            SolutionCommand lastExistingStep = solution.get( solution.size() - 1 );

            SolutionCommand combinedStep = tryToSimplify(
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
     * Try to combine two commands. If this is not possible then return null.
     */
    private SolutionCommand tryToSimplify(
        SolutionCommand existingCmd, SolutionCommand newCmd )
    {
        CommandAction action1 = existingCmd.actions[0];
        CommandAction action2 = newCmd.actions[0];

        if (
               action1 instanceof WaitAction
            && action2 instanceof WaitAction
        )
        {
            WaitAction wait1 = (WaitAction)action1;
            WaitAction wait2 = (WaitAction)action2;
            return new SolutionCommand(
                new WaitAction( wait1.steps + wait2.steps ) );
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
        return SolutionParser.serialise(
            new Solution(
                solution.toArray( new SolutionCommand[ solution.size() ] )
            )
        );
    }
}
