package rabbitescape.engine.solution;

import rabbitescape.engine.Token;

public class SelectInstruction implements Instruction
{
    private Token.Type type;

    public SelectInstruction( Token.Type type )
    {
        this.type = type;
    }

    @Override
    public void performOn( SandboxGame sandboxGame )
    {
        sandboxGame.setSelectedType( type );
    }
}
