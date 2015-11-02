package rabbitescape.engine.solution;

public class WaitInstruction implements Instruction
{
    public final int steps;

    public WaitInstruction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public String toString()
    {
        return "WaitInstruction( " + steps + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof WaitInstruction ) )
        {
            return false;
        }
        WaitInstruction other = (WaitInstruction)otherObj;

        return ( steps == other.steps );
    }

    @Override
    public int hashCode()
    {
        return steps;
    }

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.caseWaitInstruction( this );
    }
}
