package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class UntilAction implements CommandAction
{
    public final CompletionState targetState;

    public UntilAction( CompletionState targetState )
    {
        this.targetState = targetState;
    }

    @Override
    public String toString()
    {
        return "UntilAction( " + targetState.name() + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof UntilAction ) )
        {
            return false;
        }
        UntilAction other = (UntilAction)otherObj;

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
        actionTypeSwitch.caseUntilAction( this );
    }
}
