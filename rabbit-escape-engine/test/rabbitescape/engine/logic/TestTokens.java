package rabbitescape.engine.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static rabbitescape.engine.ChangeDescription.State.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestTokens
{
    // TODO: slopes and bridges

    @Test
    public void Tokens_return_their_state_names_lowercase()
    {
        Token t = new Token( 1, 2, Token.Type.bash );
        t.state = TOKEN_BASH_FALLING;
        assertThat(t.stateName(), equalTo("token_bash_falling"));
    }

    @Test
    public void Tokens_fall_slowly_and_stop_on_ground()
    {
        assertWorldEvolvesLike(
            "bdikcp" + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "######",

            "      " + "\n" +
            "bdikcp" + "\n" +
            "ffffff" + "\n" +
            "      " + "\n" +
            "######",

            "      " + "\n" +
            "      " + "\n" +
            "bdikcp" + "\n" +
            "ffffff" + "\n" +
            "######",

            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "bdikcp" + "\n" +
            "######",

            "      " + "\n" +
            "      " + "\n" +
            "      " + "\n" +
            "bdikcp" + "\n" +
            "######"
        );
    }

    @Test
    public void Tokens_disappear_when_they_drop_outside_world()
    {
        World world = createWorld(
            "bdikc",
            "     "
        );

        // Sanity - we have 5 things
        assertThat( world.things.size(), equalTo( 5 ) );

        world.step();

        // Still 5 things, not off bottom yet
        assertThat( world.things.size(), equalTo( 5 ) );

        world.step();

        // Now off bottom - all gone
        assertThat( world.things.size(), equalTo( 0 ) );
    }

    @Test
    public void Can_add_tokens_on_empty_and_sloping_blocks()
    {
        World world = createWorld(
            "\\) (/",  // 2 slopes, 2 bridges
            "#####",
            ":dig=5"
        );

        // Sanity - no tokens yet
        assertThat( world.things.size(), equalTo( 0 ) );
        assertThat( world.abilities.get( Token.Type.dig ), equalTo( 5 ) );

        // This is what we are testing: add tokens on slopes, bridges, space
        world.changes.addToken( 0, 0, Token.Type.dig );
        world.changes.addToken( 1, 0, Token.Type.dig );
        world.changes.addToken( 2, 0, Token.Type.dig );
        world.changes.addToken( 3, 0, Token.Type.dig );
        world.changes.addToken( 4, 0, Token.Type.dig );
        world.step();

        // All 4 tokens were added
        assertThat( world.things.size(), equalTo( 5 ) );
        assertThat( world.abilities.get( Token.Type.dig ), equalTo( 0 ) );
    }

    @Test
    public void Cant_add_tokens_on_solid_blocks()
    {
        World world = createWorld(
            "\\) (/",  // 2 slopes, 2 bridges
            "#####",
            ":dig=5"
        );

        // Sanity - no tokens yet
        assertThat( world.things.size(), equalTo( 0 ) );
        assertThat( world.abilities.get( Token.Type.dig ), equalTo( 5 ) );

        // This is what we are testing: add tokens on solid blocks
        world.changes.addToken( 0, 1, Token.Type.dig );
        world.changes.addToken( 1, 1, Token.Type.dig );
        world.changes.addToken( 2, 1, Token.Type.dig );
        world.changes.addToken( 3, 1, Token.Type.dig );
        world.changes.addToken( 4, 1, Token.Type.dig );
        world.step();

        // None of them were were added
        assertThat( world.things.size(), equalTo( 0 ) );
        assertThat( world.abilities.get( Token.Type.dig ), equalTo( 5 ) );
    }

    @Test
    public void Tokens_do_not_fall_through_half_built_bridges_from_down_slope()
    {
        assertWorldEvolvesLike(
            "r d" + "\n" +
            "#* " + "\n" +
            ":*=i\\",      // Bridging token on down slope

            "   " + "\n" +
            "#rB",         // Dig token hits bridge

            "   " + "\n" +
            "#r[",

            "   " + "\n" +
            "#r{",

            "   " + "\n" +
            "#\\D"         // Starts digging
        );
    }

    @Test
    public void Tokens_do_not_fall_through_half_built_bridges_from_flat()
    {
        assertWorldEvolvesLike(
            "  d" + "\n" +
            "ri " + "\n" +
            "## ",         // Bridging token on flat

            "   " + "\n" +
            " rB" + "\n" +
            "## ",         // Dig token hits bridge

            "   " + "\n" +
            " r[" + "\n" +
            "## ",

            "   " + "\n" +
            " r{" + "\n" +
            "## ",

            "   " + "\n" +
            "  D" + "\n" +
            "## "          // Starts digging
        );
    }

    @Test
    public void Tokens_do_not_fall_through_half_built_bridges_from_up_slope()
    {
        assertWorldEvolvesLike(
            "  d" + "\n" +
            "   " + "\n" +
            "r* " + "\n" +
            "## " + "\n" +
            ":*=i/",       // Bridging token on up slope

            "   " + "\n" +
            "  B" + "\n" +
            " r " + "\n" +
            "## ",         // Dig token hits bridge

            "   " + "\n" +
            "  [" + "\n" +
            " r " + "\n" +
            "## ",

            "   " + "\n" +
            "  {" + "\n" +
            " r " + "\n" +
            "## ",

            "   " + "\n" +
            "  D" + "\n" +  // Starts digging
            " / " + "\n" +
            "## "
        );
    }

    @Test
    public void Tokens_do_not_fall_through_half_built_bridges_in_tight_corners()
    {
        assertWorldEvolvesLike(
            " d " + "\n" +
            "#r#" + "\n" +
            "#*#" + "\n" +
            "###" + "\n" +
            ":*=i/",       // Bridging token on up slope in hole

            "   " + "\n" +
            "#B#" + "\n" + // Dig token hits bridge
            "#r#" + "\n" +
            "###",

            "   " + "\n" +
            "#[#" + "\n" +
            "#r#" + "\n" +
            "###",

            "   " + "\n" +
            "#{#" + "\n" +
            "#r#" + "\n" +
            "###",

            "   " + "\n" +
            "#D#" + "\n" +  // Starts digging
            "#/#" + "\n" +
            "###"
        );
    }

    @Test
    public void Tokens_falling_onto_bridgers_in_corner_take_effect()
    {
        // This looks like the rabbit catches it when it's off to the side,
        // because really the rabbit is stuck in a hole, so it's not too
        // bad, but inconsistent with
        // Tokens_do_not_fall_through_half_built_bridges_in_tight_corners

        assertWorldEvolvesLike(
            " d#" + "\n" +
            "  #" + "\n" +
            "ri#" + "\n" +
            "###",         // Bridging token next to wall

            "  #" + "\n" +
            " d#" + "\n" +
            " f#" + "\n" +
            "###",

            "  #" + "\n" +
            "  #" + "\n" +
            " r#" + "\n" + // Dig token hits bridge and converts rabbit
            "#D#"
        );
    }

    @Test
    public void Rabbits_falling_to_death_do_not_consume_tokens()
    {
        World world = createWorld(
            " r       ",
            "  j      ",
            "   r     ",
            "    j    ",
            "     r   ",
            "      j  ",
            "       r ",
            "         ",
            "         ",
            "         ",
            "         ",
            "#bdikcp*#",
            "####)(*/#",
            "#########",
            ":*=db",
            ":*=\\"
        );

        assertWorldEvolvesLike(
            world,
            8,
            new String[] {
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "#bdi    #",
                "####****#",
                "#########",
                ":*=)k",
                ":*=(c",
                ":*=\\p",
                ":*=/db"
            });
    }

    @Test
    public void Rabbits_falling_and_living_do_consume_tokens()
    {
        World world = createWorld(
            " r       ",
            "   r   r ",
            "     r   ",
            "#bpbpcpdp",
            "####)(\\/#",
            "#########"
        );

        assertWorldEvolvesLike(
            world,
            8,
            new String[] {
                "         ",
                "         ",
                "         ",
                "#       p",
                "####)(\\ #",
                "####### #"
            });
    }

    @Test
    public void Tokens_start_off_in_non_falling_states()
    {
        // See https://github.com/andybalaam/rabbit-escape/issues/447

        World world = createWorld(
            "  ",
            " /",
            "##"
        );

        Token inAir = new Token( 0, 0, Token.Type.brolly, world );
        Token onSlope = new Token( 1, 1, Token.Type.brolly, world );

        // Until a time step passes, these are in non-moving states
        assertThat( inAir.state, is( TOKEN_BROLLY_STILL ) );
        assertThat( onSlope.state, is( TOKEN_BROLLY_ON_SLOPE ) );
    }
}
