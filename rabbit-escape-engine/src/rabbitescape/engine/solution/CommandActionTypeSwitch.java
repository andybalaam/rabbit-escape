package rabbitescape.engine.solution;

public interface CommandActionTypeSwitch
{
    void caseWaitAction( WaitAction waitAction );

    void caseSelectAction( SelectAction selectAction );

    void caseAssertStateAction( AssertStateAction targetStateAction )
        throws SolutionExceptions.UnexpectedState;

    void casePlaceTokenAction( PlaceTokenAction placeTokenAction );

    void caseUntilAction( UntilAction untilAction );
}
