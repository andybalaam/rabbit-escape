package rabbitescape.engine.config;

import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.util.Util.Function;

public class ConfigTools
{
    public static class ConfigParsingError extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;
        public String configValue;
        public int charNumber;
    }

    public static class InvalidValueType extends ConfigParsingError
    {
        private static final long serialVersionUID = 1L;

        public final Class<?> expectedClass;
        public final Class<?> actualClass;

        public InvalidValueType(
            Class<?> expectedClass,
            Class<?> actualClass
        )
        {
            this.expectedClass = expectedClass;
            this.actualClass = actualClass;
        }
    }

    public static class InvalidValue extends ConfigParsingError
    {
        private static final long serialVersionUID = 1L;

        public final String value;
        public final Class<?> clazz;

        public InvalidValue( String value, Class<?> clazz )
        {
            this.value = value;
            this.clazz = clazz;
        }
    }

    public static class UnexpectedCharacter extends ConfigParsingError
    {
        private static final long serialVersionUID = 1L;

        public final String expectedChars;
        public final char actualChar;

        public UnexpectedCharacter( String expectedChars, char actualChar )
        {
            this.expectedChars = expectedChars;
            this.actualChar = actualChar;
        }
    }

    public static class UnknownValueType extends RabbitEscapeException
    {
        private static final long serialVersionUID = 1L;

        public final Class<?> clazz;

        public UnknownValueType( Class<?> clazz )
        {
            this.clazz = clazz;
        }
    }

    public static void setInt( Config config, String key, int value )
    {
        config.set( key, String.valueOf( value ) );
    }

    public static int getInt( Config config, String key )
    {
        return Integer.parseInt( config.get( key ) );
    }

    public static void setBool( Config config, String key, boolean value )
    {
        config.set( key, String.valueOf( value ) );
    }

    public static boolean getBool( Config config, String key )
    {
        return Boolean.parseBoolean( config.get( key ) );
    }

    public static void setString( Config config, String key, String value )
    {
        config.set( key, value );
    }

    public static String getString( Config config, String key )
    {
        return config.get(  key );
    }

    // TODO: break into separate class file
    public static <T> Map<String, T> getMap(
        Config cfg, String configKey, Class<T> clazz )
    {
        return stringToMap( cfg.get( configKey ), clazz );
    }

    public static <T> Map<String, T> stringToMap(
        String jsonish, Class<T> clazz )
    {
        final int open_bracket = 0;
        final int comma_or_close_bracket = 1;
        final int end = 2;
        final int key_open_quote_or_close_bracket = 3;
        final int key_char_or_close_quote = 4;
        final int colon = 5;
        final int value_open_quote_or_digit = 6;
        final int value_char_or_close_quote = 7;
        final int value_digit_or_comma_or_close_bracket = 8;
        final int key_open_quote = 9;

        int mode = open_bracket;

        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        Map<String, T> ret = new TreeMap<String, T>();

        int i = 0;
        for ( char ch : asChars( jsonish ) )
        {
            try
            {
                switch ( mode )
                {
                    case open_bracket:
                        expect( "{", ch );
                        mode = key_open_quote_or_close_bracket;
                        break;
                    case key_open_quote:
                        expect( "\"", ch );
                        mode = key_char_or_close_quote;
                        key.setLength( 0 );
                        value.setLength( 0 );
                        break;
                    case key_open_quote_or_close_bracket:
                        expect( "\"}", ch );
                        if ( ch == '"' )
                        {
                            mode = key_char_or_close_quote;
                            key.setLength( 0 );
                            value.setLength( 0 );
                        }
                        else
                        {
                            mode = end;
                        }
                        break;
                    case key_char_or_close_quote:
                        if ( ch == '"' )
                        {
                            mode = colon;
                        }
                        else
                        {
                            key.append( ch );
                        }
                        break;
                    case colon:
                        expect( ":", ch );
                        mode = value_open_quote_or_digit;
                        break;
                    case value_open_quote_or_digit:
                        expect( "\"0123456789", ch );
                        if ( ch == '"' )
                        {
                            expectValueTypeIs( clazz, String.class );
                            mode = value_char_or_close_quote;
                        }
                        else
                        {
                            expectValueTypeIs( clazz, Integer.class );
                            mode = value_digit_or_comma_or_close_bracket;
                            value.append( ch );
                        }
                        break;
                    case value_char_or_close_quote:
                        if ( ch == '"' )
                        {
                            mode = comma_or_close_bracket;
                        }
                        else
                        {
                            value.append( ch );
                        }
                        break;
                    case comma_or_close_bracket:
                        expect( ",}", ch );
                        foundValue( clazz, key, value, ret );
                        if ( ch == ',' )
                        {
                            mode = key_open_quote;
                        }
                        else // '}'
                        {
                            mode = end;
                        }
                        break;
                    case value_digit_or_comma_or_close_bracket:
                        if ( ch == ',' )
                        {
                            foundValue( clazz, key, value, ret );
                            mode = key_open_quote;
                        }
                        else if ( ch == '}' )
                        {
                            foundValue( clazz, key, value, ret );
                            mode = end;
                        }
                        else
                        {
                            expect( "0123456789", ch );
                            value.append( ch );
                        }
                        break;
                    case end:
                        expect( "", ch ); // Throw - should have ended!
                        break;
                }

                ++i;
            }
            catch( ConfigParsingError e )
            {
                e.charNumber = i;
                e.configValue = jsonish;
                throw e;
            }
        }
        return ret;
    }

    private static <T> void foundValue(
        Class<T> clazz,
        StringBuilder key,
        StringBuilder value,
        Map<String, T> ret )
    {
        ret.put( key.toString(), makeValue( value, clazz ) );
    }

    private static void expectValueTypeIs(
        Class<?> expectedClass, Class<?> actualClass )
    {
        if ( expectedClass != actualClass )
        {
            throw new InvalidValueType( expectedClass, actualClass );
        }
    }

    private static void expect( String exp, char act )
    {
        if ( exp.indexOf( act ) == -1 )
        {
            throw new UnexpectedCharacter( exp, act );
        }
    }

    @SuppressWarnings( "unchecked" )
    private static <T> T makeValue( StringBuilder value, Class<T> clazz )
    {
        String v = value.toString();

        if ( clazz == String.class )
        {
            return (T)v;
        }
        else if ( clazz == Integer.class )
        {
            try
            {
                return (T)new Integer( v );
            }
            catch ( NumberFormatException e )
            {
                throw new InvalidValue( v, clazz );
            }
        }
        else
        {
            throw new UnknownValueType( clazz );
        }
    }

    public static <T> void setMap(
        Config cfg, String key, Map<String, T> value )
    {
        cfg.set( key, mapToString( value ) );
    }

    public static <T> String mapToString( Map<String, T> value )
    {
        StringBuilder val = new StringBuilder();
        val.append( '{' );

        boolean stringVals = true;
        boolean first = true;
        List<String> ks = new ArrayList<String>( value.keySet() );
        Collections.sort( ks );
        for ( String k : ks )
        {
            T rhs = value.get( k );

            if ( first )
            {
                first = false;
                stringVals = rhs instanceof String;
            }
            else
            {
                val.append( ',' );
            }

            val.append( '"' );
            val.append( k );
            val.append( "\":" );
            if ( stringVals ) val.append( '"' );
            val.append( rhs );
            if ( stringVals ) val.append( '"' );
        }

        val.append( '}' );

        return val.toString();
    }

    /**
     * Note: does not support negative numbers.
     */
    public static <T> Set<T> getSet(
        Config cfg, String configKey, Class<T> clazz )
    {
        return stringToSet( cfg.get( configKey ), clazz );
    }

    /**
     * Note: does not support negative numbers.
     */
    public static <T> void setSet( Config cfg, String configKey, Set<T> value )
    {
        cfg.set( configKey, setToString( new TreeSet<T>( value ) ) );
    }

    public static <T> String setToString( SortedSet<T> ret )
    {
        return "[" + join( ",", map( quoteString( ret ), ret ) ) + "]";
    }

    private static <T> Function<T, String> quoteString( SortedSet<T> set )
    {
        return new Function<T, String>()
        {
            @Override
            public String apply( T t )
            {
                if ( t instanceof String )
                {
                    return "\"" + t.toString() + "\"";
                }
                else
                {
                    return t.toString();
                }
            }
        };
    }

    private static <T> Set<T> stringToSet( String jsonish, Class<T> clazz )
    {
        final int open_square_bracket = 0;
        final int open_quote_or_digit_or_close_square_bracket = 1;
        final int char_or_close_quote = 2;
        final int end = 3;
        final int digit_or_comma_or_close_square_bracket = 4;
        final int comma_or_close_square_bracket = 5;
        final int digit_or_open_quote = 6;

        int mode = open_square_bracket;

        boolean foundValue = false;
        StringBuilder value = new StringBuilder();
        Set<T> ret = new TreeSet<T>();

        int i = 0;
        for ( char ch : asChars( jsonish ) )
        {
            try
            {
                switch ( mode )
                {
                    case open_square_bracket:
                        expect( "[", ch );
                        mode = open_quote_or_digit_or_close_square_bracket;
                        break;
                    case open_quote_or_digit_or_close_square_bracket:
                        expect( "\"0123456789]", ch );
                        if ( ch == '"' )
                        {
                            expectValueTypeIs( clazz, String.class );
                            foundValue = true;
                            value.setLength( 0 );
                            mode = char_or_close_quote;
                        }
                        else if ( ch == ']' )
                        {
                            maybeFoundValue( clazz, value, foundValue, ret );
                            mode = end;
                        }
                        else
                        {
                            expectValueTypeIs( clazz, Integer.class );
                            foundValue = true;
                            value.setLength( 0 );
                            value.append( ch );
                            mode = digit_or_comma_or_close_square_bracket;
                        }
                        break;
                    case char_or_close_quote:
                        if ( ch == '"' )
                        {
                            maybeFoundValue( clazz, value, foundValue, ret );
                            mode = comma_or_close_square_bracket;
                        }
                        else
                        {
                            value.append( ch );
                        }
                        break;
                    case comma_or_close_square_bracket:
                        expect( ",]", ch );
                        if ( ch == ',' )
                        {
                            maybeFoundValue( clazz, value, foundValue, ret );
                            mode = digit_or_open_quote;
                        }
                        else
                        {
                            mode = end;
                        }
                        break;
                    case digit_or_comma_or_close_square_bracket:
                        expect( "0123456789,]", ch );
                        if ( ch == ',' )
                        {
                            maybeFoundValue( clazz, value, foundValue, ret );
                            mode = digit_or_open_quote;
                        }
                        else if ( ch == ']' )
                        {
                            maybeFoundValue( clazz, value, foundValue, ret );
                            mode = end;
                        }
                        else
                        {
                            value.append( ch );
                        }
                        break;
                    case digit_or_open_quote:
                        expect( "0123456789\"", ch );
                        if ( ch == '"' )
                        {
                            expectValueTypeIs( clazz, String.class );
                            foundValue = true;
                            value.setLength( 0 );
                            mode = char_or_close_quote;
                        }
                        else
                        {
                            expectValueTypeIs( clazz, Integer.class );
                            foundValue = true;
                            value.setLength( 0 );
                            value.append( ch );
                            mode = digit_or_comma_or_close_square_bracket;
                        }
                        break;
                    case end:
                        expect( "", ch ); // Throw - should have ended!
                        break;
                }

                ++i;
            }
            catch( ConfigParsingError e )
            {
                e.charNumber = i;
                e.configValue = jsonish;
                throw e;
            }
        }
        return ret;
    }

    @SuppressWarnings( "unchecked" )
    private static <T> void maybeFoundValue(
        Class<T> clazz, StringBuilder value, boolean foundValue, Set<T> ret )
    {
        if ( foundValue )
        {
            ret.add( makeValue( value, clazz ) );
        }
    }
}
