package rabbitescape.engine.solution;

/**
 * Example actions are wait X time steps, select a token and place a token.
 */
public interface SolutionAction
{
    void typeSwitch( ActionTypeSwitch actionTypeSwitch );

    /*
     * Return the string representing this action to form part of the solution 
     * in a .rel file.
     */
    String relFormat( boolean firstInCommand );
}
