package rabbitescape.render;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class Physics
{
    /**
     * Everything that modifies the world goes through here, with
     * synchronization.
     */
    public static class WorldModifier
    {
        private final World world;

        public WorldModifier( World world )
        {
            this.world = world;
        }

        public synchronized void step()
        {
            world.step();
        }

        public synchronized void addToken( int x, int y, Token.Type type )
        {
            world.changes.addToken( x, y, type );
        }
    }
}
