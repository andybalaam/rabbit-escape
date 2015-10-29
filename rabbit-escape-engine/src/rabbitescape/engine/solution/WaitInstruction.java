package rabbitescape.engine.solution;

import rabbitescape.engine.World;

public class WaitInstruction implements Instruction
{
    private int steps;

    public WaitInstruction( int steps )
    {
        this.steps = steps;
    }

    @Override
    public void performOn( World world )
    {
        for ( int i = 0; i < steps; i++ )
        {
            world.step();
        }
    }

}
