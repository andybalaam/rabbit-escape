package rabbitescape.engine.solution;

import rabbitescape.engine.Token;

public class SelectInstruction implements Instruction
{
    public final Token.Type type;

    public SelectInstruction( Token.Type type )
    {
        this.type = type;
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

    @Override
    public void typeSwitch( InstructionTypeSwitch instructionTypeSwitch )
    {
        instructionTypeSwitch.caseSelectInstruction( this );
    }
}
