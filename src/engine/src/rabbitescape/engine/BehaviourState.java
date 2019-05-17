package rabbitescape.engine;

import java.util.Map;

public class BehaviourState
{
    public static void addToStateIfNotDefault(
        Map<String, String> saveState, String key, String value, String def
    )
    {
        if ( !def.equals( value  ) )
        {
            saveState.put( key, value );
        }
    }

    public static void addToStateIfGtZero(
        Map<String, String> saveState, String key, int value
    )
    {
        if ( value > 0 )
        {
            saveState.put( key, Integer.toString( value ) );
        }
    }

    public static void addToStateIfTrue(
        Map<String, String> saveState, String key, boolean value
    )
    {
        if ( value )
        {
            saveState.put( key, Boolean.toString( value ) );
        }
    }

    public static String restoreFromState(
        Map<String, String> saveState, String key, String defaultValue )
    {
        String val = saveState.get( key );
        if ( val != null )
        {
            return val;
        }
        else
        {
            return defaultValue;
        }
    }

    public static int restoreFromState(
        Map<String, String> saveState, String key, int defaultValue )
    {
        String val = saveState.get( key );
        if ( val != null )
        {
            try
            {
                return Integer.valueOf( val );
            }
            catch( NumberFormatException e )
            {
                throw new BadSavedState( e, saveState );
            }
        }
        else
        {
            return defaultValue;
        }
    }

    public static boolean restoreFromState(
        Map<String, String> saveState, String key, boolean defaultValue )
    {
        String val = saveState.get( key );
        if ( val != null )
        {
            return Boolean.valueOf( val );
        }
        else
        {
            return defaultValue;
        }
    }
}
