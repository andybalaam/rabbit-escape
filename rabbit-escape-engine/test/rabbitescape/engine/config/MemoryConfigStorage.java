package rabbitescape.engine.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryConfigStorage implements IConfigStorage
{
    private final Map<String, String> values = new HashMap<String, String>();
    public List<String> saves = new ArrayList<String>();

    @Override
    public void set( String key, String value )
    {
        values.put( key, value );
    }

    @Override
    public String get( String key )
    {
        return values.get( key );
    }

    @Override
    public void save( Config config )
    {
        saves.add( values.get( Config.CFG_VERSION ) );
    }

}
