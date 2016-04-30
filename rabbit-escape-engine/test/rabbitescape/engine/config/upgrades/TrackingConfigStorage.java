package rabbitescape.engine.config.upgrades;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.IConfigStorage;
import rabbitescape.engine.util.Util;

class TrackingConfigStorage implements IConfigStorage
{
    public final List<String> log;

    private final Map<String, String> existingConfig;

    public TrackingConfigStorage( String... keysAndValues )
    {
        log = new ArrayList<String>();
        existingConfig = new HashMap<String, String>();

        Util.reAssert(
            keysAndValues.length % 2 == 0, "Must have a value for each key" );

        for ( int i = 0; i < keysAndValues.length; i += 2 )
        {
            existingConfig.put( keysAndValues[i], keysAndValues[i+1] );
        }
    }

    @Override
    public void set( String key, String value )
    {
        log.add( String.format( "set( %s, %s )", key, value ) );
        existingConfig.put( key, value );
    }

    @Override
    public String get( String key )
    {
        String value = existingConfig.get( key );
        log.add( String.format( "get( %s ) = %s", key, value ) );
        return value;
    }

    @Override
    public void save( Config config )
    {
        log.add( "save" );
    }
}
