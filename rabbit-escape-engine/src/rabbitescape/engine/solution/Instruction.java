package rabbitescape.engine.solution;

import rabbitescape.engine.World;

public interface Instruction
{
    void performOn( World world );
}
