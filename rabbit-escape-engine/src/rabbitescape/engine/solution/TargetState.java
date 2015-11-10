package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class TargetState implements ValidationInstruction
{
    public final CompletionState targetState;

    public TargetState( CompletionState targetState )
    {
        this.targetState = targetState;
    }

    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            targetState.name();
        }
        return SolutionFactory.INSTRUCTION_DELIMITER + targetState.name();
    }

    @Override
    public String toString()
    {
        return "TargetState( " + targetState.name() + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof TargetState ) )
        {
            return false;
        }
        TargetState other = (TargetState)otherObj;

        return ( targetState == other.targetState );
    }

    @Override
    public int hashCode()
    {
        return targetState.hashCode();
    }

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.caseTargetState( this );
    }
}
