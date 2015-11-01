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
}
