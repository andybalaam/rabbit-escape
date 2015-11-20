package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class UntilAction implements SolutionAction
{
    public final CompletionState targetState;

    public UntilAction( CompletionState targetState )
    {
        this.targetState = targetState;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            targetState.name();
        }
        return SolutionParser.ACTION_DELIMITER + targetState.name();
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
    public void typeSwitch( ActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.caseUntilAction( this );
    }
}
