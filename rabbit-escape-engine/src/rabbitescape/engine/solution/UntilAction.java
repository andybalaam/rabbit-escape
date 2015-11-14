package rabbitescape.engine.solution;

import rabbitescape.engine.World;
import rabbitescape.engine.World.CompletionState;

public class UntilAction implements SolutionAction
{
    /** until action will throw an exception after this. */
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
    public void perform( SandboxGame sbg, World w )
    {
        for ( int i = 0; w.completionState() != this.completionState; i++ )
        {
            w.step();
            if ( i >= UntilAction.maxSteps ) {
                throw new InvalidAction( 
                    "action " + this.relFormat( false ) + 
                    " got to " + i + " steps" 
                );
            }
        } 
    }
    
    @Override
    public String relFormat( boolean firstInCommand )
    {
        return "until:" + completionState;
    }

}
