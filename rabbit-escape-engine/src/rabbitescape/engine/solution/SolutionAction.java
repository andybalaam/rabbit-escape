package rabbitescape.engine.solution;

public interface SolutionAction
{
    void typeSwitch( ActionTypeSwitch actionTypeSwitch );

    String relFormat( boolean firstInCommand );
}
