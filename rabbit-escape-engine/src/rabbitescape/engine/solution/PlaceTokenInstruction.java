package rabbitescape.engine.solution;

import rabbitescape.engine.Token.Type;

public class PlaceTokenInstruction implements Instruction
{
    int x;
    int y;

    public PlaceTokenInstruction( int x, int y )
    {
        this.x = x;
        this.y = y;
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
    public void performOn( SandboxGame sandboxGame )
    {
        Type type = sandboxGame.getSelectedType();
        sandboxGame.getWorld().changes.addToken( x, y, type );
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
}
