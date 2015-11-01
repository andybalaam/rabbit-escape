package rabbitescape.engine.solution;

public class WaitInstruction implements Instruction
{
    private int steps;

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

}
