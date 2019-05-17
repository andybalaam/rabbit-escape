package rabbitescape.engine.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.*;

import rabbitescape.engine.Block;
import rabbitescape.engine.Direction;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Block.Shape;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.TextWorldManip;

public class TestWorldChanges
{
    class AddTokens implements Runnable
    {
        private final World world;

        public AddTokens( World world )
        {
            this.world = world;
        }

        @Override
        public void run()
        {
            for ( int i = 0; i < 100; ++i )
            {
                world.changes.addToken( 3, 0, Token.Type.bash );
            }
        }
    }

    class StepAlot implements Runnable
    {
        private final World world;
        private boolean running;

        public StepAlot( World world )
        {
            this.world = world;
            this.running = true;
        }

        @Override
        public void run()
        {
            while( running )
            {
                world.step();
            }
        }

        public void pleaseStop()
        {
            running = false;
        }
    }

    @Test
    public void Simultaneous_changes_all_register() throws Exception
    {
        final World world = TextWorldManip.createWorld(
            "    ",
            "    ",
            "  r ",
            "####"
        );

        world.abilities.put( Token.Type.bash, 201 );

        // This is what we're testing: add tokens in 2 simultaneous threads
        Thread t1 = new Thread( new AddTokens( world ) );
        Thread t2 = new Thread( new AddTokens( world ) );
        StepAlot stepalot = new StepAlot( world );
        Thread t3 = new Thread( stepalot );
        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        stepalot.pleaseStop();
        t3.join();

        // There should have been no concurrent modification problems

        // Last step to make sure we have registered all changes
        world.step();

        // All 200 adds should have worked
        assertThat( world.abilities.get( Token.Type.bash ), equalTo( 1 ) );
        assertThat( world.things.size(), equalTo( 200 ) );
    }

    @Test
    public void Changes_can_be_reverted()
    {
        String[] worldText = new String[]
        {
            ":name=My Round X",
            ":description=Go and\nreturn",
            ":author_name=dave",
            ":author_url=",
            ":hint.1=",
            ":hint.2=",
            ":hint.3=",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":rabbit_index_count=7",
            ":paused=false",
            ":bash=1",
            ":bridge=3",
            ":dig=2",
            "######",
            "#i   #",
            "# ***#",
            "######",
            ":*=r{index:2}",
            ":*=r{index:4}",
            ":*=t{index:6}"
        };

        World world = TextWorldManip.createWorld( worldText );
        Token tok0 = world.getTokenAt( 1, 1 );
        Rabbit rabbit0 = world.rabbits.get( 0 );
        Rabbit rabbit1 = world.rabbits.get( 1 );
        Rabbit rabbit2 = world.rabbits.get( 2 );

        // One of every type of change
        world.changes.enterRabbit(
            new Rabbit( 1, 2, Direction.RIGHT, Rabbit.Type.RABBIT ) );

        world.changes.killRabbit( rabbit0 );
        world.changes.saveRabbit( rabbit1 );
        world.changes.killRabbit( rabbit2 );
        world.changes.addToken( 2, 1, Token.Type.bash );
        world.changes.removeToken( tok0 );
        world.changes.addBlock( new Block( 1, 1, Block.Material.EARTH, Shape.FLAT, 0 ) );
        world.changes.removeBlockAt( 0, 0 );

        // This is what we are testing - revert the changes
        world.changes.revert();

        assertThat(
            TextWorldManip.renderCompleteWorld( world, true ),
            equalTo( worldText )
        );

        // They should have no effect, even if you apply them
        world.changes.apply();

        assertThat(
            TextWorldManip.renderCompleteWorld( world, true ),
            equalTo( worldText )
        );
    }
}
