package rabbitescape.engine.solution;

public class WaitInstruction implements Instruction
{
    public final int steps;

    public WaitInstruction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public String relFormat( boolean firstInStep )
    {
        if ( firstInStep )
        {
            return String.valueOf( steps ) + SolutionFactory.STEP_DELIMITER;
        }
        else if ( steps == 1 )
        {
            return SolutionFactory.STEP_DELIMITER;
        }
        else if ( steps > 1 )
        {
            return SolutionFactory.STEP_DELIMITER + String.valueOf( steps - 1 )
                + SolutionFactory.STEP_DELIMITER;
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
