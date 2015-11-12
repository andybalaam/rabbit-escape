package rabbitescape.engine.solution;

public interface ActionTypeSwitch
{
    void caseWaitAction( WaitAction action );
    
    void caseUntilAction( UntilAction action );

    void caseSelectAction( SelectAction selectAction );

    void caseAssertStateAction( AssertStateAction targetStateAction )
        throws SolutionExceptions.UnexpectedState;

    void casePlaceTokenAction( PlaceTokenAction placeTokenAction );
}
