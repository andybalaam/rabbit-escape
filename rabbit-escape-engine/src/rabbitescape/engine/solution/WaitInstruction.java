package rabbitescape.engine.solution;

public class WaitInstruction implements Instruction
{
    private final int steps;

    public WaitInstruction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public void performOn( SandboxGame sandboxGame )
    {
        for ( int i = 0; i < steps; i++ )
        {
            sandboxGame.getWorld().step();
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
}
