package rabbitescape.engine.util;

import rabbitescape.engine.Direction;
import rabbitescape.engine.Rabbit;
import rabbitescape.engine.Thing;
import rabbitescape.engine.Token;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;

import org.junit.Test;

public class TestUtil
{
    @Test
    public void Chain_3a()
    {
        String[] ab = new String[] {"a", "b"};
        String[] cd = new String[] {"c", "d"};
        String[] ef = new String[] {"e", "f"};
        
        Iterable<String> chained = Util.chain( Arrays.asList( ab ), 
                                               Arrays.asList( cd ), 
                                               Arrays.asList( ef ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ), 
            equalTo( new String[] {"a", "b", "c", "d", "e", "f" } ) );
    }

    @Test
    public void Chain_3b()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        String[] _h = new String[] {};
        
        Iterable<String> chained = Util.chain( Arrays.asList( a ), 
                                               Arrays.asList( _bcd ), 
                                               Arrays.asList( efg ), 
                                               Arrays.asList( _h ) );
        assertThat( Util.list( chained ).toArray( new String[] {} ), 
            equalTo( new String[] {"a", "e", "f", "g" } ) );
    }

    @Test
    public void  Chain_different_classes()
    {
        Token[] tokens = new Token[] {new Token( 0, 0, Token.Type.bash ),
                                      new Token( 1, 1, Token.Type.bridge )};
        Rabbit[] rabbits = new Rabbit[] {new Rabbit( 3, 3, Direction.LEFT )};
        
        Iterable<Thing> chained = Util.chain( Arrays.asList( tokens ), Arrays.asList( rabbits ) );
        
        String s = "";
        
        for ( Thing t: chained ) {
            s = s + t.x;
        }
        
        assertThat( s, equalTo( "013" ) );
    }
    
    @Test
    public void Concat_3()
    {
        String[] a = new String[] {"a"};
        String[] _bcd = new String[] {};
        String[] efg = new String[] {"e", "f", "g"};
        
        String[] concated = Util.concat( a, _bcd, efg );
        
        assertThat( concated, equalTo( new String[] {"a", "e", "f", "g" } ) );
    }
    

    
}
