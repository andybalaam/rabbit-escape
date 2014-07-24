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
}
