package rabbitescape.engine.i18n;

import static rabbitescape.engine.util.Util.*;

import java.util.*;

import rabbitescape.engine.util.NamedFieldFormatter;
import rabbitescape.engine.util.Util.Function;

public class Translation
{
    public static class Instance
    {
        private final String bundleName;
        private final Locale locale;

        public Instance( String bundleName, Locale locale )
        {
            this.bundleName = bundleName;
            this.locale = locale;
        }

        public String t( String key )
        {
            try
            {
                ResourceBundle bundle =
                    ResourceBundle.getBundle( bundleName, locale );

                return bundle.getString( sanitise( key ) );
            }
            catch( MissingResourceException e )
            {
                return key;
            }
        }

        public static String sanitise( String key )
        {
            return stringFromChars( map( sanitiseChar(), asChars( key ) ) );
        }

        private static Function<Character, Character> sanitiseChar()
        {
            return new Function<Character, Character>()
            {
                final List<Character> ALLOWED = list(
                    asChars( "abcdefghijklmnopqrstuvwxyz0123456789" ) );

                @Override
                public Character apply( Character t )
                {
                    char lct = Character.toLowerCase( t );
                    if ( ALLOWED.contains( lct ) )
                    {
                        return lct;
                    }
                    else
                    {
                        return '_';
                    }
                }
            };
        }

        public String t( String key, Map<String, Object> params )
        {
            return NamedFieldFormatter.format( t( key ), params );
        }
    }

    private static Instance instance = new Instance(
        "rabbitescape.engine.translations", Locale.getDefault() );

    public static void init( Locale locale )
    {
        init( instance.bundleName, locale );
    }

    public static void init( String bundleName, Locale locale )
    {
        instance = new Instance( bundleName, locale );
    }

    public static String t( String key )
    {
        return instance.t( key );
    }

    public static String t( String key, Map<String, Object> params )
    {
        return instance.t( key, params );
    }
}
