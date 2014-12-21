package rabbitescape.engine.logic;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.WorldAssertions.*;

import org.junit.Test;

import rabbitescape.engine.Token;
import rabbitescape.engine.World;

public class TestTokens
{
    // TODO: slopes and bridges

    @Test
    public void Tokens_fall_slowly_and_stop_on_ground()
    {
        assertWorldEvolvesLike(
            "bdikc" + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "#####",

            "     " + "\n" +
            "bdikc" + "\n" +
            "fffff" + "\n" +
            "     " + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "fffff" + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "#####",

            "     " + "\n" +
            "     " + "\n" +
            "     " + "\n" +
            "bdikc" + "\n" +
            "#####"
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
}
