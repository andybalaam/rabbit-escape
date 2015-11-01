package rabbitescape.ui.text;

import java.io.IOException;
import java.util.List;

import static rabbitescape.engine.i18n.Translation.*;
import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.World;
import rabbitescape.engine.err.ExceptionTranslation;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.solution.Instruction;
import rabbitescape.engine.solution.SandboxGame;
import rabbitescape.engine.solution.SolutionFactory;

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

    public InputHandler( SandboxGame sandboxGame, Terminal terminal )
    {
        this.sandboxGame = sandboxGame;
        this.terminal = terminal;
    }

    public boolean handle()
    {
        String input = input();

        if ( input.equals( "" ) )
        {
            return true;
        }

        try
        {
            List<Instruction> instructions =
                SolutionFactory.createTimeStep( input, 1, 0 );

            for ( Instruction instr : instructions )
            {
                instr.performOn( sandboxGame );
            }
        }
        catch ( RabbitEscapeException e )
        {
            return fail( ExceptionTranslation.translate( e, terminal.locale ) );
        }

        return true;
    }

    private boolean fail( String message )
    {
        terminal.out.println( message );
        return false;
    }

    private String input()
    {
        terminal.out.println(
            t( "Press return or type 'ITEM x y' to place an item." ) );

        terminal.out.println(
            t( "ITEM = ${items}", newMap( "items", "bash" ) ) );

        try
        {
            return terminal.in.readLine();
        }
        catch (IOException e)
        {
            return "";
        }
    }
}
