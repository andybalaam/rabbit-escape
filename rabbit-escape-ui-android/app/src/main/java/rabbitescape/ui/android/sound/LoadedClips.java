package rabbitescape.ui.android.sound;

import java.util.HashMap;
import java.util.Map;

public class LoadedClips
{
    private final Map<String, Integer> map = new HashMap<String, Integer>();

    public void put( String resourcePath, int clipId )
    {
        map.put( resourcePath, clipId );
    }

    public Integer get( String resourcePath )
    {
        return map.get( resourcePath );
    }

    public void clear()
    {
        map.clear();
    }
}
