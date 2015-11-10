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
    public static final String INSTRUCTION_DELIMITER = "&";
    private static final Pattern WAIT_REGEX = Pattern.compile( "\\d+" );

    private static final Pattern PLACE_TOKEN_REGEX = Pattern.compile(
        "\\((\\d+),(\\d+)\\)" );

    private static final List<String> COMPLETION_STATES =
        toStringList( CompletionState.values() );

    private static final List<String> TOKEN_TYPES =
        toStringList( Type.values() );

    public static Solution create( String solutionString )
    {
        return expand( parse( solutionString ) );
    }

    private static Solution expand( Solution solution )
    {
        List<SolutionCommand> expandedCommands = new ArrayList<SolutionCommand>();

        for ( IdxObj<SolutionCommand> command : enumerate( solution.commands ) )
        {
            // Wait one step after every semicolon (unless the last instruction
            // was a wait instruction).

            SolutionCommand newCommand = command.object;
            Instruction last = newCommand.lastInstruction();

            if (
                   ! ( last instanceof WaitInstruction )
                && (
                       ( command.index < solution.commands.length - 1 )
                    || ! ( last instanceof TargetState )
                )
            )
            {
                newCommand = new SolutionCommand(
                    concat(
                        command.object.instructions,
                        new Instruction[] { new WaitInstruction( 1 ) }
                    )
                );
            }

            expandedCommands.add( newCommand );
        }

        // If the last instruction is not a validation then assume this was
        // a 'normal' winning solution.
        if ( expandedCommands.size() > 0
            && !(
                expandedCommands.get(
                    expandedCommands.size() - 1 ).lastInstruction()
                instanceof ValidationInstruction
            )
        )
        {
            expandedCommands.add(
                new SolutionCommand( new TargetState( CompletionState.WON ) ) );
        }

        return new Solution(
            expandedCommands.toArray(
                new SolutionCommand[ expandedCommands.size() ] )
        );
    }

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
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();

        String[] instructionStrings = split(
            commandString, INSTRUCTION_DELIMITER );

        for ( int j = 0; j < instructionStrings.length; j++ )
        {
            if ( !instructionStrings[j].equals( "" ) )
            {
                instructions.add( makeInstruction( instructionStrings[j] ) );
            }
        }

        return new SolutionCommand(
            instructions.toArray( new Instruction[ instructions.size() ] ) );
    }

    private static Instruction makeInstruction( String instructionString )
    {
        try
        {
            return doMakeInstruction( instructionString );
        }
        catch ( NumberFormatException e )
        {
            throw new InvalidInstruction( e, instructionString );
        }
    }

    private static Instruction doMakeInstruction( String instructionString )
    throws NumberFormatException, InvalidInstruction
    {
        if ( COMPLETION_STATES.contains( instructionString ) )
        {
            return new TargetState(
                CompletionState.valueOf( instructionString ) );
        }
        else if ( WAIT_REGEX.matcher( instructionString ).matches() )
        {
            return new WaitInstruction( Integer.valueOf( instructionString ) );
        }
        else if ( TOKEN_TYPES.contains( instructionString ) )
        {
            return new SelectInstruction( Type.valueOf( instructionString ) );
        }
        else
        {
            Matcher m = PLACE_TOKEN_REGEX.matcher( instructionString );
            if ( m.matches() )
            {
                return new PlaceTokenInstruction(
                    Integer.valueOf( m.group( 1 ) ),
                    Integer.valueOf( m.group( 2 ) ) );
            }
        }
        throw new InvalidInstruction( instructionString );
    }

}
