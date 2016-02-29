package rabbitescape.engine.config;

import java.util.HashMap;
import java.util.Map;

public class MemoryConfigStorage implements IConfigStorage
{
    private final Map<String, String> values = new HashMap<String, String>();

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
        throw new UnsupportedOperationException();
    }

}
