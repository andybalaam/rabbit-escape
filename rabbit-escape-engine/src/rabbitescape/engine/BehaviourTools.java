package rabbitescape.engine;

import static rabbitescape.engine.Direction.*;

import java.util.Map;

import rabbitescape.engine.ChangeDescription.State;

public class BehaviourTools
{
    public static State rl( Rabbit rabbit, State rightState, State leftState )
    {
        return rabbit.dir == RIGHT ? rightState : leftState;
    }

    static void addToStateIfGtZero(
        Map<String, String> saveState, String key, int value )
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
