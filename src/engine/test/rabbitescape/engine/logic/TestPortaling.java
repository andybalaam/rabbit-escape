// TestPortaling.java

package rabbitescape.engine.logic;

import static org.hamcrest.MatcherAssert.*;
import static rabbitescape.engine.Tools.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestPortaling {

    @Test
    public void Rabbit_teleports_between_two_portals() {
        World world = createWorld(
            "   ru     u  O ",
            "###############",
            ":portal=2"
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "    ~     u  O ",
                "###############"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "          r> O ",
                "###############"
            )
        );

        world.step();

        // to check both portals are gone
        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "           r>O ",
                "###############"
            )
        );
    }

    @Test
    public void Rabbit_does_not_teleport_with_one_portal() {
        World world = createWorld(
            "   ru        O ",
            "###############",
            ":portal=1"
        );

        world.step();

        // The rabbit should not teleport since there's only one portal
        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "    u>       O ",
                "###############"
            )
        );

        world.step();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "    ur>      O ",
                "###############"
            )
        );
    }

    @Test
    public void Only_two_portals_exist_at_a_time() {
        World world = createWorld(
            " r           O ",
            "###############",
            ":portal=3"
        );

        // place three portal tokens quickly
        world.changes.addToken(3, 0, Token.Type.portal);
        world.changes.addToken(5, 0, Token.Type.portal);
        world.changes.addToken(7, 0, Token.Type.portal);

        // apply changes
        world.changes.apply();

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                " r>  u u     O ",
                "###############"
            )
        );

    }


    @Test
    public void Rabbits_do_not_teleport_if_portals_are_removed() {
        World world = createWorld(
            " r  u    u   O ",
            "###############",
            ":portal=2"
        );

        // Manually remove portals before rabbit reaches them
        world.changes.removeToken(world.getTokenAt(4, 0));
        world.changes.removeToken(world.getTokenAt(9, 0));
        world.changes.apply();

        // Step the rabbit forward
        for (int i = 0; i < 9; i++) {
            world.step();
        }

        assertThat(
            renderWorld(world, true, false),
            equalTo(
                "          r> O ",
                "###############"
            )
        );
    }
}
