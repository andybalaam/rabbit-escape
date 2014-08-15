package rabbitescape.engine.i18n;

import java.util.Map;

import rabbitescape.engine.util.NamedFieldFormatter;

public class Translation
{
    public static String t( String key )
    {
        // Placeholder for translations
        return key;
    }

    public static String t( String key, Map<String, Object> items )
    {
        return NamedFieldFormatter.format( t( key ), items );
    }
}
