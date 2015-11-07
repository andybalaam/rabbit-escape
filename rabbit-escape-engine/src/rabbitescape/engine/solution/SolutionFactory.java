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
    public static final String STEP_DELIMITER = ";";
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
        List<SolutionStep> expandedSteps = new ArrayList<SolutionStep>();

        for ( IdxObj<SolutionStep> step : enumerate( solution.steps ) )
        {
            // Wait one step after every semicolon (unless the last instruction
            // was a wait instruction).

            SolutionStep newStep = step.object;
            Instruction last = newStep.lastInstruction();

            if (
                   ! ( last instanceof WaitInstruction )
                && (
                       ( step.index < solution.steps.length - 1 )
                    || ! ( last instanceof TargetState )
                )
            )
            {
                newStep = new SolutionStep(
                    concat(
                        step.object.instructions,
                        new Instruction[] { new WaitInstruction( 1 ) }
                    )
                );
            }

            expandedSteps.add( newStep );
        }

        // If the last instruction is not a validation step then assume this was
        // a 'normal' winning solution.
        if ( expandedSteps.size() > 0
            && !(
                expandedSteps.get( expandedSteps.size() - 1 ).lastInstruction()
                    instanceof ValidationInstruction )
            )
        {
            expandedSteps.add(
                new SolutionStep( new TargetState( CompletionState.WON ) ) );
        }

        return new Solution(
            expandedSteps.toArray( new SolutionStep[ expandedSteps.size() ] ) );
    }

    public static Solution parse( String solution )
    {
        String[] stringSteps = split( solution, STEP_DELIMITER );

        List<SolutionStep> steps = new ArrayList<>();

        for ( int i = 0; i < stringSteps.length; i++ )
        {
            steps.add( createStep( stringSteps[i] ) );
        }

        return new Solution(
            steps.toArray( new SolutionStep[ steps.size() ] ) );
    }

    public static SolutionStep createStep( String stepString )
    {
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();

        String[] instructionStrings = split(
            stepString, INSTRUCTION_DELIMITER );

        for ( int j = 0; j < instructionStrings.length; j++ )
        {
            if ( !instructionStrings[j].equals( "" ) )
            {
                instructions.add( makeInstruction( instructionStrings[j] ) );
            }
        }

        return new SolutionStep(
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
