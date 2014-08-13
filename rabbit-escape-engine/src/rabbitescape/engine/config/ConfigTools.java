package rabbitescape.engine.config;

public class ConfigTools
{
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
}
