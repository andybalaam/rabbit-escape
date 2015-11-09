package rabbitescape.engine.solution;

public interface InstructionTypeSwitch
{
    void caseWaitInstruction( WaitInstruction waitInstruction );
    void caseSelectInstruction( SelectInstruction selectInstruction );
    void caseTargetState( TargetState targetState );

    void casePlaceTokenInstruction(
        PlaceTokenInstruction placeTokenInstruction );
    void caseUntilInstruction( UntilInstruction untilInstruction );
}
