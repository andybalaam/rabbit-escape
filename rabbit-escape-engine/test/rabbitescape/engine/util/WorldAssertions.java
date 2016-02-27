package rabbitescape.engine.util;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;
import static rabbitescape.engine.textworld.TextWorldManip.*;
import static rabbitescape.engine.util.Util.*;

import java.util.Collections;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import rabbitescape.engine.World;

public class WorldAssertions
{
    public static void assertWorldEvolvesLike(
        String initialState, String... laterStates )
    {
        doAssertWorldEvolvesLike(         initialState, laterStates, false );
        doAssertMirroredWorldEvolvesLike( initialState, laterStates );
    }

    private static void doAssertMirroredWorldEvolvesLike(
        String initialState, String[] laterStates )
    {
        doAssertWorldEvolvesLike(
            mirror( initialState ), mirror( laterStates ), true );
    }

    public static String[] mirror( String... states )
    {
        return map( new MirrorState(), states, new String[0] );
    }

    public static String mirror( String state )
    {
        return new MirrorState().apply( state );
    }

    private static class MirrorLine implements Function<String, String>
    {
        @Override
        public String apply( String input )
        {
            if ( input.startsWith( ":*=" ) )
            {
                return (
                      input.substring( 0, 3 )
                    + swapChars( input.substring( 3 ) )
                );
            }
            else
            {
                return swapChars(
                    new StringBuilder( input ).reverse().toString() );
            }
        }

        private String swapChars( String input )
        {
            char[] ret = new char[input.length()];

            for( int i = 0; i < ret.length; ++i )
            {
                ret[i] = swapChar( input.charAt( i ) );
            }

            return new String( ret );
        }

        private char swapChar( char c )
        {
            switch ( c )
            {
                case 'r': return 'j';    case 'j': return 'r';
                case 't': return 'y';    case 'y': return 't';
                case '/': return '\\';   case '\\': return '/';
                case '(': return ')';    case ')': return '(';
                case '<': return '>';    case '>': return '<';
                case '|': return '?';    case '?': return '|';
                case '[': return ']';    case ']': return '[';
                case '$': return '^';    case '^': return '$';
                case '\'': return '!';   case '!': return '\'';
                case '-': return '=';    case '=': return '-';
                case '@': return '%';    case '%': return '@';
                case '_': return '+';    case '+': return '_';
                case ',': return '.';    case '.': return ',';
                case '&': return 'm';    case 'm': return '&';
                case 'e': return 's';    case 's': return 'e';
                case 'h': return 'a';    case 'a': return 'h';
                case 'K': return 'W';    case 'W': return 'K';
                case 'I': return 'J';    case 'J': return 'I';
                case 'B': return 'E';    case 'E': return 'B';
                case 'G': return 'T';    case 'T': return 'G';
                case 'Y': return 'F';    case 'F': return 'Y';
                case 'U': return 'L';    case 'L': return 'U';
                case '{': return '}';    case '}': return '{';
                case '~': return '`';    case '`': return '~';
                default: return c;
            }
        }
    }

    private static class MirrorState implements Function<String, String>
    {
        @Override
        public String apply( String state )
        {
            return join(
                "\n",
                map( new MirrorLine(), split( state, "\n" ), new String[0] )
            );
        }
    }

    private static void doAssertWorldEvolvesLike(
        String initialState, String[] laterStates, boolean reverseOrder )
    {
        World world = createWorld( split( initialState, "\n" ) );

        if ( reverseOrder )
        {
            Collections.reverse( world.rabbits );
        }

        for ( IdxObj<String> state : enumerate( laterStates ) )
        {
            world.step();

            assertThat(
                renderWorld( world, true, false ),
                equalToState( split( state.object, "\n" ), state.index + 2 )
            );
        }
    }

    private static Matcher<String[]> equalToState(
        final String[] expected, final int stateNum )
    {
        return new BaseMatcher<String[]>()
        {
            String[] other;

            @Override
            public boolean matches( Object objOther )
            {
                if ( !( objOther instanceof String[] ) )
                {
                    return false;
                }

                other = (String[])objOther;
                return equalTo( expected ).matches( other );
            }

            @Override
            public void describeTo( Description description )
            {
                description.appendText(
                    " (at frame number " + stateNum + ")\n" );

                description.appendText( join( "\n", expected ) );
                description.appendText( "\nActual:\n" );
                description.appendText( join( "\n", other ) );
            }
        };
    }

    /**
     * @brief Note that this method steps the world, changing the input argument
     * that is referenced.
     */
    public static void assertWorldEvolvesLike(
        World world,
        int nSteps,
        String[] finalWorld )
    {
        for ( int i = 0; i < nSteps; i++ )
        {
            world.step();
        }

        String[] steppedWorld = renderCompleteWorld( world, false );

        try
        {
            assertThat( finalWorld.length, equalTo( steppedWorld.length ) );
    
            assertThat(
                finalWorld,
                equalTo( steppedWorld )
                );
        }
        catch ( AssertionError e )
        {
            // Output the stepped world in case it's helpful for building an
            // assertion.
            for ( String s : steppedWorld )
            {
                System.out.println( "\"" + s + "\"," );
            }
            throw e;
        }
    }
}
