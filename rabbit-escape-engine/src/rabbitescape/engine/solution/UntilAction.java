package rabbitescape.engine.solution;

import rabbitescape.engine.World.CompletionState;

public class UntilAction implements SolutionAction
{
    /** until action will cause an exception to be thrown after this. */
    public static final int maxSteps = 500;

    public CompletionState completionState;

    public UntilAction( String state )
    {
        try 
        {
            completionState = CompletionState.valueOf(state);
        }
        catch ( IllegalArgumentException e)
        {
            throw new InvalidAction( 
                "unknown assertion in until action: " + state );
        }
    }

    @Override
    public void typeSwitch( ActionTypeSwitch actionTypeSwitch )
    {
        actionTypeSwitch.caseUntilAction( this );
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        return "until:" + completionState;
    }

}
