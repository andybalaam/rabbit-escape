package rabbitescape.engine.solution;

import rabbitescape.engine.Token;

public class SelectInstruction implements Instruction
{
    private final Token.Type type;

    public SelectInstruction( Token.Type type )
    {
        this.type = type;
    }

    @Override
    public void performOn( SandboxGame sandboxGame )
    {
        sandboxGame.setSelectedType( type );
    }

    @Override
    public String toString()
    {
        return "SelectInstruction( " + type.name() + " )";
    }

    @Override
    public boolean equals( Object otherObj )
    {
        if ( ! ( otherObj instanceof SelectInstruction ) )
        {
            return false;
        }
        SelectInstruction other = (SelectInstruction)otherObj;

        return ( type == other.type );
    }

    @Override
    public int hashCode()
    {
        return type.hashCode();
    }
}
