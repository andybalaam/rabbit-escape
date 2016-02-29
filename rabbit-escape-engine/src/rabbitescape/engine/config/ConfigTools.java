package rabbitescape.engine.config;

import static rabbitescape.engine.util.Util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rabbitescape.engine.err.RabbitEscapeException;

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

        String jsonish = cfg.get( configKey );

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

        cfg.set( key, val.toString() );
    }
}
