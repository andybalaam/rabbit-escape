package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class TargetState implements ValidationInstruction
{
    public final static int INSTRUCTION_INDEX_NOT_SPECIFIED = -1;

    public final CompletionState targetState;
    public final int instructionIndex;

    public TargetState(
        CompletionState targetState,
        int instructionIndex )
    {
        this.targetState = targetState;
        this.instructionIndex = instructionIndex;
    }

    public TargetState(
        CompletionState targetState
    )
    {
        this( targetState, INSTRUCTION_INDEX_NOT_SPECIFIED );
    }

    public String relFormat( boolean firstInStep )
    {
        if ( firstInStep )
        {
            targetState.name();
        }
        return SolutionFactory.INSTRUCTION_DELIMITER + targetState.name();
    }

    @Override
    public String toString()
    {
        return "TargetState( "
            + targetState.name()
            + ", " + instructionIndex
            + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof TargetState ) )
        {
            return false;
        }
        TargetState other = (TargetState)otherObj;

        return (
               targetState      == other.targetState
            && instructionIndex == other.instructionIndex
        );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + instructionIndex;
        result = prime * result + targetState.hashCode();
        return result;
    }

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.caseTargetState( this );
    }
}
