package rabbitescape.render;

import static org.junit.Assert.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.render.Physics.WorldModifier;

public class TestPhysics
{
    @Test
    public void Many_threads_can_manipulate_World_simultaneously()
        throws Exception
    {
        final World world = TextWorldManip.createWorld(
            "#      #",
            "# /) r #",
            "########",
            ":climb=1000000"
        );
        world.setIntro( false );

        final int num_threads = 100;
        final int num_iters   = 20;

        final WorldModifier modifier = new WorldModifier( world );

        class Stepper implements Runnable
        {
            public boolean failed = false;

            @Override
            public void run()
            {
                try
                {
                    for ( int i = 0; i < num_iters; ++i )
                    {
                        modifier.step();
                    }
                }
                catch ( Throwable e )
                {
                    failed = true;
                    e.printStackTrace();
                }
            }
        };

        class TokenAdder implements Runnable
        {
            public boolean failed = false;

            @Override
            public void run()
            {
                try
                {
                    for ( int i = 0; i < num_iters; ++i )
                    {
                        modifier.addToken( 5, 0, Token.Type.climb );
                    }
                }
                catch ( Throwable e )
                {
                    failed = true;
                    e.printStackTrace();
                }
            }
        };

        // ---

        final Stepper stepper = new Stepper();
        final TokenAdder tokenAdder = new TokenAdder();

        Thread[] threads = new Thread[num_threads];
        for ( int i = 0; i < num_threads; i += 2 )
        {
            threads[i    ] = new Thread( stepper );
            threads[i + 1] = new Thread( tokenAdder );
        }

        for ( int i = 0; i < num_threads; ++i )
        {
            threads[i].start();
        }

        for ( int i = 0; i < num_threads; ++i )
        {
            threads[i].join();
        }

        assertFalse( stepper.failed );
        assertFalse( tokenAdder.failed );
    }
}
