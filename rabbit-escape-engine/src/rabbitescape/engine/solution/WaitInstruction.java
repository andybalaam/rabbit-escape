package rabbitescape.engine.solution;

public class WaitInstruction implements Instruction
{
    public final int steps;

    public WaitInstruction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public String relFormat( boolean firstInCommand )
    {
        if ( firstInCommand )
        {
            return String.valueOf( steps ) + SolutionFactory.COMMAND_DELIMITER;
        }
        else if ( steps == 1 )
        {
            return SolutionFactory.COMMAND_DELIMITER;
        }
        else if ( steps > 1 )
        {
            return
                  SolutionFactory.COMMAND_DELIMITER
                + String.valueOf( steps - 1 )
                + SolutionFactory.COMMAND_DELIMITER;
        }
        else
        {
            throw new IllegalArgumentException(
                "Waiting for non-positive number of steps: " + steps );
        }
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
