package rabbitescape.engine.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Unit tests for the MegaCoder. */
public class TestMegaCoder
{
    /** Test encoding a simple string. */
    @Test
    public void Encode_hello_world()
    {
        String output = MegaCoder.encode( "Hello World" );
        assertEquals( "Igjj*z{j={4", output );

        String decoded = MegaCoder.decode( output );
        assertEquals( "Hello World", decoded );
    }

    /**
     * Test encoding a slightly different string. (Nb. The length difference is
     * the important factor in obfuscation here).
     */
    @Test
    public void Encode_hello_world_exclamation()
    {
        String output = MegaCoder.encode( "Hello World!" );
        assertEquals( " yyy\\z,\\4tve", output );

        String decoded = MegaCoder.decode( output );
        assertEquals( "Hello World!", decoded );
    }

    /** Test encoding a long string with different unicode characters. */
    @Test
    public void Encode_including_unicode()
    {
        String output = MegaCoder.encode( "\u263A Mr Happy" );
        assertEquals( "3TOzTig\u263A\u263A2", output );

        String decoded = MegaCoder.decode( output );
        assertEquals( "\u263A Mr Happy", decoded );
    }
}
