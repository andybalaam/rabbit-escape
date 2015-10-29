package rabbitescape.engine.solution;

import java.util.ArrayList;
import java.util.List;

import rabbitescape.engine.World.CompletionState;
import rabbitescape.engine.util.Util;

public class SolutionFactory
{
    private static final String STAGE_DELIMITER = ";";
    private static final String INSTRUCTION_DELIMITER = "&";
    private static final String WAIT_REGEX = "[1-9][0-9]*";

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
                Instruction instruction = makeInstruction(
                    instructionStrings[j],
                    solutionId, i );
                instructions.add( instruction );
            }
            // Wait one step after every semicolon (unless the last instruction
            // was a wait instruction).
            if ( !( instructions.get( instructions.size() - 1 ) instanceof WaitInstruction )
                && ( i < instructionStages.length - 1 ) )
            {
                instructions.add( new WaitInstruction( 1 ) );
            }
        }

        // If the last instruction is not a validation step then assume this was
        // a 'normal' winning solution.
        if ( !( instructions.get( instructions.size() - 1 ) instanceof ValidationInstruction ) )
        {
            instructions.add( new TargetState( CompletionState.WON, solutionId,
                instructions.size() ) );
        }

        return new Solution( instructions );
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
        throw new InvalidInstruction( instructionString );
    }

}
