package rabbitescape.engine.solution;

public interface TimeStepActionTypeSwitch
{
    void caseSelectAction( SelectAction selectAction );

    void caseAssertStateAction( AssertStateAction targetStateAction )
        throws SolutionExceptions.UnexpectedState;

    void casePlaceTokenAction( PlaceTokenAction placeTokenAction );
}
