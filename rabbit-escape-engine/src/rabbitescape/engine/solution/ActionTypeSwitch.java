package rabbitescape.engine.solution;

public interface ActionTypeSwitch
{
    void caseWaitAction( WaitAction action );

    void caseSelectAction( SelectAction selectAction );

    void caseAssertStateAction( AssertStateAction targetStateAction )
        throws SolutionExceptions.UnexpectedState;

    void casePlaceTokenAction( PlaceTokenAction placeTokenAction );

    void caseAssertStateAction( UntilAction untilAction );
}
