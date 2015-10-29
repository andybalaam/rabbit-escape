package rabbitescape.engine.solution;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class SandboxGame
{
    private Token.Type selectedType = null;
    private World world;

    public SandboxGame( World world )
    {
        this.world = world;
    }

    public Token.Type getSelectedType()
    {
        return selectedType;
    }

    public void setSelectedType( Token.Type selectedType )
    {
        this.selectedType = selectedType;
    }

    public World getWorld()
    {
        return world;
    }
}
