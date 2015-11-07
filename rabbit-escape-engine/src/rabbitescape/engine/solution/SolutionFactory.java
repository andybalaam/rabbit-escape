package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util;

public class SolutionFactory
{
    public static final String STAGE_DELIMITER = ";";
    public static final String INSTRUCTION_DELIMITER = "&";
    private static final Pattern WAIT_REGEX = Pattern.compile( "\\d+" );

    private static final Pattern PLACE_TOKEN_REGEX = Pattern.compile(
        "\\((\\d+),(\\d+)\\)" );

    private static final List<String> COMPLETION_STATES =
        Util.toStringList( CompletionState.values() );

    private static final List<String> TOKEN_TYPES =
        Util.toStringList( Type.values() );

    public static Solution create( String solution )
    {
        String[] instructionStages = Util.split( solution, STAGE_DELIMITER );

        List<SolutionStep> steps = new ArrayList<>();
        for ( int i = 0; i < instructionStages.length; i++ )
        {
            steps.addAll( createTimeStep( instructionStages[i] ) );

            // Wait one step after every semicolon (unless the last instruction
            // was a wait instruction).
            if ( steps.size() > 0
                && !(
                    steps.get( steps.size() - 1 ).instructions[0]
                        instanceof WaitInstruction )
                && ( i < instructionStages.length - 1 ) )
            {
                steps.add( new SolutionStep( new WaitInstruction( 1 ) ) );
            }
        }

        // If the last instruction is not a validation step then assume this was
        // a 'normal' winning solution.
        if ( steps.size() > 0
            && !(
                steps.get( steps.size() - 1 ).instructions[0]
                    instanceof ValidationInstruction )
            )
        {
            steps.add(
                new SolutionStep( new TargetState( CompletionState.WON ) ) );
        }

        return new Solution(
            steps.toArray( new SolutionStep[ steps.size() ] ) );
    }

    public static List<SolutionStep> createTimeStep( String timeStepString )
    {
        ArrayList<SolutionStep> ret = new ArrayList<SolutionStep>();

        String[] instructionStrings = Util.split(
            timeStepString, INSTRUCTION_DELIMITER );

        for ( int j = 0; j < instructionStrings.length; j++ )
        {
            if ( !instructionStrings[j].equals( "" ) )
            {
                ret.add( new SolutionStep( makeInstruction( instructionStrings[j] ) ) );
            }
        }

        return ret;
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
