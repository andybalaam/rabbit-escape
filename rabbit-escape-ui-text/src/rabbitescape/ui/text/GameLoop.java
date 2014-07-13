package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.TextWorldManip;
import rabbitescape.engine.World;

public class GameLoop
{
    private final World world;

    public GameLoop( World world )
    {
        this.world = world;
    }

    public void run()
    {
        while( true )
        {
            try
            {
                System.out.println(
                    join("\n", TextWorldManip.renderWorld( world, false ) ) );
                Thread.sleep( 500 );

                System.out.println(
                    join("\n", TextWorldManip.renderWorld( world, true ) ) );
                Thread.sleep( 500 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
            world.step();
        }
    }
}
