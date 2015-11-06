package rabbitescape.engine.solution;

public interface InstructionTypeSwitch
{
    void caseWaitInstruction( WaitInstruction waitInstruction );

    void caseSelectInstruction( SelectInstruction selectInstruction );

    void caseTargetState( TargetState targetState )
        throws SolutionExceptions.UnexpectedState;

    void casePlaceTokenInstruction(
        PlaceTokenInstruction placeTokenInstruction );
}
