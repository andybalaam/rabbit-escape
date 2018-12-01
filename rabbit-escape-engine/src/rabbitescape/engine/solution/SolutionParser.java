package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static rabbitescape.engine.util.Util.*;

import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util.Function;

public class SolutionParser
{
    public static final String COMMAND_DELIMITER = ";";
    public static final String ACTION_DELIMITER = "&";
    private static final Pattern WAIT_REGEX = Pattern.compile( "\\d+" );
    private static final Pattern UNTIL_REGEX =
        Pattern.compile( "until:([A-Z]+)" );

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
            commands.add( parseCommand( stringCommands[i] ) );
        }

        return new Solution(
            commands.toArray( new SolutionCommand[ commands.size() ] ) );
    }

    public static SolutionCommand parseCommand( String commandString )
    {
        ArrayList<CommandAction> actions = new ArrayList<CommandAction>();

        String[] actionStrings = split( commandString, ACTION_DELIMITER );

        for ( int j = 0; j < actionStrings.length; j++ )
        {
            if ( !actionStrings[j].equals( "" ) )
            {
                actions.add( makeAction( actionStrings[j] ) );
            }
        }

        return new SolutionCommand(
            actions.toArray( new CommandAction[ actions.size() ] ) );
    }

    public static String serialise( Solution solution )
    {
        return join( ";", map( serialiseCommand(), solution.commands ) );
    }

    // ---

    private static CommandAction makeAction( String actionString )
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

    private static CommandAction doMakeAction( String actionString )
    throws NumberFormatException, InvalidAction
    {
        Matcher untilMatcher = UNTIL_REGEX.matcher( actionString );
        if ( COMPLETION_STATES.contains( actionString ) )
        {
            return new AssertStateAction(
                CompletionState.valueOf( actionString ) );
        }
        else if ( untilMatcher.matches() )
        {
            CompletionState state =
                CompletionState.valueOf( untilMatcher.group( 1 ) );
            return new UntilAction( state );
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

    private static Function<SolutionCommand, String> serialiseCommand()
    {
        return new Function<SolutionCommand, String>()
        {
            @Override
            public String apply( SolutionCommand command )
            {
                return join( "&", map( serialiseAction(), command.actions ) );
            }
        };
    }

    private static Function<CommandAction, String> serialiseAction()
    {
        return new Function<CommandAction, String>()
        {
            @Override
            public String apply( CommandAction action )
            {
                ActionSerialiser s = new ActionSerialiser();
                action.typeSwitch( s );
                return s.ret;
            }
        };
    }

    private static class ActionSerialiser implements CommandActionTypeSwitch
    {
        public String ret = null;

        @Override
        public void caseWaitAction( WaitAction waitAction )
        {
            if ( waitAction.steps == 1 )
            {
                ret = "";
            }
            else
            {
                ret = String.valueOf( waitAction.steps );
            }
        }

        @Override
        public void caseSelectAction( SelectAction selectAction )
        {
            ret = selectAction.type.name();
        }

        @Override
        public void caseAssertStateAction( AssertStateAction targetStateAction )
        {
            ret = targetStateAction.targetState.name();
        }

        @Override
        public void casePlaceTokenAction( PlaceTokenAction placeTokenAction )
        {
            ret = "(" + placeTokenAction.x + "," + placeTokenAction.y + ")";
        }

        @Override
        public void caseUntilAction( UntilAction untilAction )
        {
            ret = "until:" + untilAction.targetState.name();
        }
    }
}
