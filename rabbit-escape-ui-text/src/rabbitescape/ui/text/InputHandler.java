package rabbitescape.ui.text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static rabbitescape.engine.util.Translation.t;
import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.err.ExceptionTranslation;
import rabbitescape.engine.err.RabbitEscapeException;

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

    private static class Command
    {
        public final String item;
        public final int x;
        public final int y;

        public Command( String input, World world )
            throws CommandCreationFailure
        {
            String[] vals = input.split( " +" );
            if ( vals.length != 3 )
            {
                throw new WrongNumberOfParts( input, vals.length );
            }

            item = vals[0];
            x = parseCoordinate( input, "x", vals[1] );
            y = parseCoordinate( input, "y", vals[2] );

            checkCoordinateInRange( x, input, "x", world.size.getWidth() );
            checkCoordinateInRange( y, input, "y", world.size.getHeight() );
        }

        private void checkCoordinateInRange(
            int value, String input, String coordinateName, double max )
            throws CoordinateOutsideWorld
        {
            if ( value < 0 || value >= max )
            {
                throw new CoordinateOutsideWorld(
                    input, coordinateName, value, (int)max );
            }
        }

        private int parseCoordinate(
            String input, String coordinateName, String coordinateValue )
            throws NonnumericCoordinate
        {
            try
            {
                return Integer.valueOf( coordinateValue );
            }
            catch( java.lang.NumberFormatException e )
            {
                throw new NonnumericCoordinate(
                    input, coordinateName, coordinateValue, e );
            }
        }
    }

    private final World world;
    private final Terminal terminal;

    public InputHandler( World world, Terminal terminal )
    {
        this.world = world;
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
            Command command = new Command( input, world );
            if ( command.item.equals( "bash" ) )
            {
                world.addToken( command.x, command.y, Token.Type.bash );
            }
            else
            {
                throw new UnknownCommandName( input, command.item );
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
            return new BufferedReader(
                new InputStreamReader( terminal.in )
            ).readLine();
        }
        catch (IOException e)
        {
            return "";
        }
    }
}
