package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class AssertStateAction implements ValidationAction
{
    public final CompletionState targetState;

    public AssertStateAction( CompletionState targetState )
    {
        this.targetState = targetState;
    }

    @Override
    public String toString()
    {
        return "AssertStateAction( " + targetState.name() + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof AssertStateAction ) )
        {
            return false;
        }
        AssertStateAction other = (AssertStateAction)otherObj;

        return ( targetState == other.targetState );
    }

    @Override
    public int hashCode()
    {
        return targetState.hashCode();
    }

    @Override
    public void typeSwitch( CommandActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.caseAssertStateAction( this );
    }

    @Override
    public void typeSwitch( TimeStepActionTypeSwitch timeStepActionTypeSwitch )
    {
        timeStepActionTypeSwitch.caseAssertStateAction( this );
    }
}
