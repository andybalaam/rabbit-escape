package rabbitescape.engine.solution;

public class UntilInstruction implements PassTimeInstruction
{
    public int maximumSteps = 1000;

    public String relFormat( boolean firstInStep )
    {
        if ( firstInStep )
        {
            return "until";
        }
        return SolutionFactory.INSTRUCTION_DELIMITER + "until";
    }

    @Override
    public String toString()
    {
        return "UntilInstruction()";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        return ( otherObj instanceof UntilInstruction );
    }

    @Override
    public int hashCode()
    {
        return 0;
    }

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.caseUntilInstruction( this );
    }
}
