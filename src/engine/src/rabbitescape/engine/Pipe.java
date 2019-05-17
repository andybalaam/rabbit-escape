package rabbitescape.engine;

import static rabbitescape.engine.ChangeDescription.State.PIPE;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.util.WaterUtil;

public class Pipe extends Thing
{
    private int rate;

    public Pipe( int x, int y )
    {
        super( x, y, PIPE );
        rate = WaterUtil.SOURCE_RATE;
    }

    @Override
    public void calcNewState( World world )
    {
        // Deliberately left empty.
    }

    @Override
    public void step( World world )
    {
        WaterRegion waterRegion = world.waterTable.getItemAt( x, y );
        if ( waterRegion != null )
        {
            waterRegion.addContents( rate );
        }
    }

    @Override
    public Map<String, String> saveState( boolean runtimeMeta )
    {
        return new HashMap<String, String>();
    }

    @Override
    public void restoreFromState( Map<String, String> state )
    {
        // Deliberately left empty.
    }

    @Override
    public String overlayText()
    {
        return "Pipe";
    }
}
