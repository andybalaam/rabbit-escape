package rabbitescape.engine.textworld;

import static rabbitescape.engine.util.Util.*;

import java.util.HashMap;
import java.util.Map;

import rabbitescape.engine.Thing;
import rabbitescape.engine.util.Util;
import rabbitescape.engine.util.VariantGenerator;

public class ItemsLineProcessor
{
    private final LineProcessor lineProcessor;
    private final int x;
    private final int y;
    private final String value;

    private String stateString;
    private Thing currentThing;

    public ItemsLineProcessor(
        LineProcessor lineProcessor,
        int x,
        int y,
        String value
    )
    {
        this.lineProcessor = lineProcessor;
        this.x = x;
        this.y = y;
        this.value = value;
        this.currentThing = null;
        this.stateString = null;
    }

    public void process( VariantGenerator variantGen )
    {
        for ( char c : asChars( value ) )
        {
            if ( stateString != null )
            {
                if ( c == '}' )
                {
                    Map<String, String> mp = stateMap( stateString );
                    if ( mp == null )
                    {
                        throw new BadStateMap(
                            stateString,
                            lineProcessor.lines,
                            lineProcessor.lineNum
                        );
                    }
                    currentThing.restoreFromState( mp );
                    stateString = null;
                }
                else
                {
                    stateString += c;
                }
            }
            else if ( c == '{' )
            {
                stateString = "";
            }
            else
            {
                currentThing = lineProcessor.processChar( c, x, y, variantGen );
            }
        }
    }

    public static Map<String, String> stateMap( String str )
    {
        Map<String, String> ret = new HashMap<String, String>();

        if ( Util.isEmpty( str ) )
        {
            return ret;
        }

        for ( String pair : split( str, "," ) )
        {
            String[] spl = split( pair, ":" );
            if ( spl.length != 2 )
            {
                return null;
            }
            ret.put( spl[0], spl[1] );
        }

        return ret;
    }
}
