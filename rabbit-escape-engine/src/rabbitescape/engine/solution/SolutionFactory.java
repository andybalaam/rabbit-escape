package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.Token.Type;
import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util;

public class SolutionFactory
{
    private static final String STAGE_DELIMITER = ";";
    private static final String INSTRUCTION_DELIMITER = "&";
    private static final String WAIT_REGEX = "\\d+";
    private static final String PLACE_TOKEN_REGEX = "\\((\\d+),(\\d+)\\)";

    private static final List<String> COMPLETION_STATES = new ArrayList<>();
    static
    {
        // Initialise the completion state strings.
        CompletionState[] completionStates = CompletionState.values();
        for ( int i = 0; i < completionStates.length; i++ )
        {
            COMPLETION_STATES.add( completionStates[i].toString() );
        }
    }
    private static final List<String> TOKEN_TYPES = new ArrayList<>();
    static
    {
        // Initialise the completion state strings.
        Type[] tokenTypes = Type.values();
        for ( int i = 0; i < tokenTypes.length; i++ )
        {
            TOKEN_TYPES.add( tokenTypes[i].toString() );
        }
    }

    public static Solution create( String solution, int solutionId )
    {
        String[] instructionStages = Util.split( solution, STAGE_DELIMITER );

        List<Instruction> instructions = new ArrayList<>();
        for ( int i = 0; i < instructionStages.length; i++ )
        {
            String[] instructionStrings = Util.split( instructionStages[i],
                INSTRUCTION_DELIMITER );
            for ( int j = 0; j < instructionStrings.length; j++ )
            {
                if ( !instructionStrings[j].equals( "" ) )
                {
                    Instruction instruction = makeInstruction(
                        instructionStrings[j],
                        solutionId, i );
                    instructions.add( instruction );
                }
            }
            // Wait one step after every semicolon (unless the last instruction
            // was a wait instruction).
            if ( instructions.size() > 0
                && !( instructions.get( instructions.size() - 1 ) instanceof WaitInstruction )
                && ( i < instructionStages.length - 1 ) )
            {
                instructions.add( new WaitInstruction( 1 ) );
            }
        }

        // If the last instruction is not a validation step then assume this was
        // a 'normal' winning solution.
        if ( instructions.size() > 0
            && !( instructions.get( instructions.size() - 1 ) instanceof ValidationInstruction ) )
        {
            instructions.add( new TargetState( CompletionState.WON, solutionId,
                instructions.size() ) );
        }

        return new Solution( solutionId, instructions );
    }

    private static Instruction makeInstruction(
        String instructionString,
        int solutionId,
        int instructionIndex )
    {
        if ( COMPLETION_STATES.contains( instructionString ) )
        {
            return new TargetState(
                CompletionState.valueOf( instructionString ), solutionId,
                instructionIndex );
        }
        else if ( instructionString.matches( WAIT_REGEX ) )
        {
            return new WaitInstruction( Integer.valueOf( instructionString ) );
        }
        else if ( TOKEN_TYPES.contains( instructionString ) )
        {
            return new SelectInstruction( Type.valueOf( instructionString ) );
        }
        else if ( instructionString.matches( PLACE_TOKEN_REGEX ) )
        {
            String[] xAndY = instructionString.replace( "(", "" )
                .replace( ")", "" ).split( "," );
            return new PlaceTokenInstruction( Integer.valueOf( xAndY[0] ),
                Integer.valueOf( xAndY[1] ) );
        }
        throw new InvalidInstruction( instructionString );
    }

}
