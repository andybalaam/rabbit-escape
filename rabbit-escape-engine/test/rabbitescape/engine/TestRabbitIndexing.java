package rabbitescape.engine;

import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.*;
import static rabbitescape.engine.Tools.equalTo;
import static rabbitescape.engine.textworld.TextWorldManip.createWorld;
import static rabbitescape.engine.textworld.TextWorldManip.renderCompleteWorld;

public class TestRabbitIndexing
{

    @Test
    public void Rabbit_index_round_trips()
    {
        String[] lines = {
            ":name=Rabbit indexing test",
            ":description=",
            ":author_name=Ronny",
            ":author_url=http://rabbit.com",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=2",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":rabbit_index_count=7",
            ":paused=false",
            "Q ",
            " k",
            "##"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( lines )
        );
    }

    @Test
    public void Index_counter_increments_on_rabbit_entrance()
    {
        String[] lines = {
            ":name=Rabbit indexing test",
            ":description=",
            ":author_name=Ronny",
            ":author_url=http://rabbit.com",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=1",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=16",
            ":rabbit_index_count=7",
            ":paused=false",
            "Q ",
            " k",
            "##"
        };

        String[] expected = {
            ":name=Rabbit indexing test",
            ":description=",
            ":author_name=Ronny",
            ":author_url=http://rabbit.com",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=1",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=15",
            ":rabbit_index_count=8",
            ":paused=false",
            "Q ",
            "*k",
            "##",
            ":*=r{index:8}"
        };

        World w = createWorld( lines );

        w.step();

        assertThat(
            renderCompleteWorld( w , true ),
            equalTo( expected )
        );
    }

    @Test
    public void Counted_if_counter_not_given()
    {
        String[] lines = {
            ":name=Rabbit indexing test",
            ":description=",
            ":author_name=Ronny",
            ":author_url=http://rabbit.com",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=5",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=4",
            ":paused=false",
            "Qr",
            "jk",
            "##"
        };

        String[] expected = {
            ":name=Rabbit indexing test",
            ":description=",
            ":author_name=Ronny",
            ":author_url=http://rabbit.com",
            ":num_rabbits=25",
            ":num_to_save=4",
            ":rabbit_delay=5",
            ":music=",
            ":num_saved=5",
            ":num_killed=4",
            ":num_waiting=4",
            ":rabbit_index_count=2",
            ":paused=false",
            "Q*",
            "*k",
            "##",
            ":*=r{index:1}",
            ":*=j{index:2}"
        };

        assertThat(
            renderCompleteWorld( createWorld( lines ), true ),
            equalTo( expected )
        );
    }
}
