package rabbitescape.engine.solution;

public class SolutionTimeStep
{
    /*public final Instruction[] instructions;

    public SolutionTimeStep( Instruction[] instructions )
    {
        this.instructions = instructions;
    }*/

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof SolutionTimeStep ) )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return 0;
    }
}
