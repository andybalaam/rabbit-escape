package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CompletionState;

public class SolutionFactory
{
    public static final String COMMAND_DELIMITER = ";";
    public static final String ACTION_DELIMITER = "&";
    private static final Pattern WAIT_REGEX = Pattern.compile( "\\d+" );

    private static final Pattern PLACE_TOKEN_REGEX = Pattern.compile(
        "\\((\\d+),(\\d+)\\)" );

    private static final List<String> COMPLETION_STATES =
        toStringList( CompletionState.values() );

    private static final List<String> TOKEN_TYPES =
        toStringList( Type.values() );

    public static Solution parse( String solution )
    {
        String[] stringCommands = split( solution, COMMAND_DELIMITER );

        List<SolutionCommand> commands = new ArrayList<>();

        for ( int i = 0; i < stringCommands.length; i++ )
        {
            commands.add( createCommand( stringCommands[i] ) );
        }

        return new Solution(
            commands.toArray( new SolutionCommand[ commands.size() ] ) );
    }

    public static SolutionCommand createCommand( String commandString )
    {
        ArrayList<SolutionAction> actions = new ArrayList<SolutionAction>();

        String[] actionStrings = split( commandString, ACTION_DELIMITER );

        for ( int j = 0; j < actionStrings.length; j++ )
        {
            if ( !actionStrings[j].equals( "" ) )
            {
                actions.add( makeAction( actionStrings[j] ) );
            }
        }

        return new SolutionCommand(
            actions.toArray( new SolutionAction[ actions.size() ] ) );
    }

    private static SolutionAction makeAction( String actionString )
    {
        try
        {
            return doMakeAction( actionString );
        }
        catch ( NumberFormatException e )
        {
            throw new InvalidAction( e, actionString );
        }
    }

    private static SolutionAction doMakeAction( String actionString )
    throws NumberFormatException, InvalidAction
    {
        if ( COMPLETION_STATES.contains( actionString ) )
        {
            return new AssertStateAction(
                CompletionState.valueOf( actionString ) );
        }
        else if ( WAIT_REGEX.matcher( actionString ).matches() )
        {
            return new WaitAction( Integer.valueOf( actionString ) );
        }
        else if ( TOKEN_TYPES.contains( actionString ) )
        {
            return new SelectAction( Type.valueOf( actionString ) );
        }
        else
        {
            Matcher m = PLACE_TOKEN_REGEX.matcher( actionString );
            if ( m.matches() )
            {
                return new PlaceTokenAction(
                    Integer.valueOf( m.group( 1 ) ),
                    Integer.valueOf( m.group( 2 ) ) );
            }
        }
        throw new InvalidAction( actionString );
    }

}
