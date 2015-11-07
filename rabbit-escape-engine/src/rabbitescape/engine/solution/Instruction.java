package rabbitescape.engine.solution;

public interface Instruction
{
    void typeSwitch( InstructionTypeSwitch instructionTypeSwitch );

    String relFormat( boolean firstInStep );
}
