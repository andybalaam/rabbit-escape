package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;

public class AssertStateAction implements ValidationAction
{
    public final CompletionState targetState;

    public AssertStateAction( CompletionState targetState )
    {
        this.targetState = targetState;
    }

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
        return "TargetState( " + targetState.name() + " )";
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
    public void perform( SandboxGame sbg, World w )
    {
        if ( w.completionState() != this.targetState )
        {
            if ( this.targetState == CompletionState.WON )
            {
                throw new SolutionExceptions.DidNotWin(
                    w.completionState() );
            }
            else
            {
                throw new SolutionExceptions.UnexpectedState(
                    this.targetState,
                    w.completionState()
                );
            }
        }
    }

}
