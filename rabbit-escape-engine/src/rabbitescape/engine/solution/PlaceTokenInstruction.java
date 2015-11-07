package rabbitescape.engine.solution;

public class PlaceTokenInstruction implements Instruction
{
    public final int x;
    public final int y;

    public PlaceTokenInstruction( int x, int y )
    {
        this.x = x;
        this.y = y;
    }

    public String relFormat( boolean firstInStep )
    {
        if ( firstInStep )
        {
            return "(" + x + "," + y + ")";
        }
        return SolutionFactory.INSTRUCTION_DELIMITER + "(" + x + "," + y + ")";
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    @Override
    public String toString()
    {
        return "PlaceTokenInstruction( " + x + ", " + y + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof PlaceTokenInstruction ) )
        {
            return false;
        }
        PlaceTokenInstruction other = (PlaceTokenInstruction)otherObj;

        return ( x == other.x && y == other.y );
    }

    @Override
    public int hashCode()
    {
        return x + 31 * ( x + y );
    }

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.casePlaceTokenInstruction( this );
    }
}
