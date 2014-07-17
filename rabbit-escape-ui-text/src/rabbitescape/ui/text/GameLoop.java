package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.*;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;

public class GameLoop
{
    private final World world;

    public GameLoop( World world )
    {
        this.world = world;
    }

    public void run()
    {
        while( !world.finished() )
        {
            try
            {
                System.out.println(
                    join("\n", TextWorldManip.renderWorld( world, false ) ) );
                Thread.sleep( 200 );

                System.out.println(
                    join("\n", TextWorldManip.renderWorld( world, true ) ) );
                Thread.sleep( 200 );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }

            world.step();
        }
    }
}
