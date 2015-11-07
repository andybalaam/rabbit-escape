package rabbitescape.ui.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.*;
import rabbitescape.engine.err.ExceptionTranslation;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.solution.Instruction;
import rabbitescape.engine.solution.SandboxGame;
import rabbitescape.engine.solution.Solution;
import rabbitescape.engine.solution.SolutionFactory;
import rabbitescape.engine.solution.SolutionRunner;
import rabbitescape.engine.solution.WaitInstruction;

public class InputHandler
{
    public static abstract class CommandCreationFailure
        extends RabbitEscapeException
    {
        public CommandCreationFailure()
        {
        }

        public CommandCreationFailure( Throwable cause )
        {
            super( cause );
        }

        private static final long serialVersionUID = 1L;
    }

    public static class WrongNumberOfParts extends CommandCreationFailure
    {
        public final String command;
        public final int numberOfParts;

        public WrongNumberOfParts( String command, int numberOfParts )
        {
            this.command = command;
            this.numberOfParts = numberOfParts;
        }

        private static final long serialVersionUID = 1L;
    }

    public static class NonnumericCoordinate extends CommandCreationFailure
    {
        public final String command;
        public final String coordinateName;
        public final String coordinateValue;

        public NonnumericCoordinate(
            String command,
            String coordinateName,
            String coordinateValue,
            NumberFormatException cause
        )
        {
            super( cause );
            this.command = command;
            this.coordinateName = coordinateName;
            this.coordinateValue = coordinateValue;
        }

        private static final long serialVersionUID = 1L;
    }

    public static class CoordinateOutsideWorld extends CommandCreationFailure
    {
        public final String command;
        public final String coordinateName;
        public final int coordinateValue;
        public final int max;

        public CoordinateOutsideWorld(
            String command,
            String coordinateName,
            int coordinateValue,
            int max
        )
        {
            this.command = command;
            this.coordinateName = coordinateName;
            this.coordinateValue = coordinateValue;
            this.max = max;
        }

        private static final long serialVersionUID = 1L;
    }

    public static class UnknownCommandName extends CommandCreationFailure
    {
        public final String command;
        public final String commandName;

        public UnknownCommandName( String command, String commandName )
        {
            this.command = command;
            this.commandName = commandName;
        }

        private static final long serialVersionUID = 1L;
    }

    private final SandboxGame sandboxGame;
    private final Terminal terminal;
    private final List<Instruction> solution;

    public InputHandler( SandboxGame sandboxGame, Terminal terminal )
    {
        this.sandboxGame = sandboxGame;
        this.terminal = terminal;
        this.solution = new ArrayList<>();
    }

    public boolean handle()
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
            List<Instruction> instructions = SolutionFactory.createTimeStep(
                input, 1, 0 );

            Instruction lastInstruction = instructions
                .get( instructions.size() - 1 );
            if ( !( lastInstruction instanceof WaitInstruction ) )
            {
                WaitInstruction waitInstruction = new WaitInstruction( 1 );
                instructions.add( waitInstruction );
            }

            for ( Instruction instr : instructions )
            {
                SolutionRunner.performInstruction( instr, sandboxGame );
            }

            append( instructions );
        }
        catch ( RabbitEscapeException e )
        {
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }

        return true;
    }

    private void append( List<Instruction> instructions )
    {
        if ( !solution.isEmpty() && instructions.size() == 1 )
        {
            Instruction lastInstruction = solution.get( solution.size() - 1 );
            Instruction combinedInstruction = tryToSimplify(
                lastInstruction, instructions.get( 0 ) );
            if ( combinedInstruction != null )
            {
                solution.set( solution.size() - 1,
                    combinedInstruction );
            }
            else
            {
                solution.addAll( instructions );
            }
        }
        else
        {
            solution.addAll( instructions );
        }
    }

    /**
     * Try to combine two instructions. If this is not possible then return
     * null.
     */
    private Instruction tryToSimplify(
        Instruction instruction1,
        Instruction instruction2 )
    {
        if ( instruction1 instanceof WaitInstruction
            && instruction2 instanceof WaitInstruction )
        {
            WaitInstruction wait1 = (WaitInstruction)instruction1;
            WaitInstruction wait2 = (WaitInstruction)instruction2;
            return new WaitInstruction( wait1.steps + wait2.steps );
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
        Solution s = new Solution( 0, solution );
        return s.relFormat();
    }
}
