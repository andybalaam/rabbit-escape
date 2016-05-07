package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class TestCellDebugPrint
{
    @Test
    public void Printed_in_the_correct_location() throws UnsupportedEncodingException
    {
        CellDebugPrint cdp = new CellDebugPrint();
        cdp.addString( 0, -1, 1, "(0,-1)" );
        cdp.addString( 1, 1, 0, "(1,1)" );
        cdp.addString( 1, 2, 1, "item 1" );
        cdp.addString( 2, 2, 0, "(2,2)" );
        String expected = 
                "      |      |      |      |" + "\n" +
                "      |(0,-1)|      |      |" + "\n" +
                "-" + "\n" +
                "      |      |      |      |" + "\n" +
                "      |      |      |      |" + "\n" +
                "-" + "\n" +
                "      |      | (1,1)|      |" + "\n" +
                "      |      |      |      |" + "\n" +
                "-" + "\n" +
                "      |      |      | (2,2)|" + "\n" +
                "      |      |item 1|      |" + "\n" +
                "-" + "\n";
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        PrintStream p = new PrintStream(b);
        cdp.print( p );
        String output = b.toString("UTF8");
        assertThat( output, equalTo( expected  ) );
    }

}
