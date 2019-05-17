package rabbitescape.ui.text;

import static rabbitescape.engine.util.Util.split;

import java.io.IOException;

import rabbitescape.engine.IgnoreWorldStatsListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.World;
import rabbitescape.engine.textworld.LineProcessor;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.CommandLineOption;
import rabbitescape.engine.util.MegaCoder;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.engine.util.Util;

public class MegaCoderCLI
{
    public static void codec( CommandLineOption o ) throws IOException
    {

        if ( o.getValue().endsWith( ".rel" ) )
        {
            String val = o.getValue();
            RealFileSystem fs = new RealFileSystem();
            if ( fs.exists( val ))
            {
                codecFile( o, val, fs );
            }
        }
        else
        {
           codecString( o );
        }
    }

    private static void codecFile( 
        CommandLineOption o, 
        String fileName,     
        RealFileSystem fs 
    ) throws IOException
    {
        // Decoded while parsing
        World world = new LoadWorldFile( fs ).load(
            new IgnoreWorldStatsListener(), fileName );
        String[] lines =
            TextWorldManip.renderCompleteWorld( world, true, false );
        String newName;
        if ( o.longForm.equals( "--encode" ))
        {
            newName = codeName( fileName );
            encodeLines( lines );
        }
        else // Decode
        {
            newName = uncodeName( fileName );
        }
        System.out.println( "Writing " + newName );
        fs.write( newName, Util.join( "\n", lines ) + "\n" );
    }

    private static void encodeLines( String[] lines )
    {
        for ( int i = 0; i < lines.length; i++ )
        {
            String line = lines[i];
            if(    line.startsWith( ":" + TextWorldManip.hint )
                || line.startsWith( ":" + TextWorldManip.solution ) )
            {
                String[] splitLine = split( line.substring( 1 ), "=", 1 );

                String key   = splitLine[0];
                String value = splitLine[1];

                lines[i] = ":"+key + LineProcessor.CODE_SUFFIX +
                           "=" +MegaCoder.encode( value );
            }
        }
    }
    private static void codecString ( CommandLineOption o )
    {
        if ( o.longForm.equals( "--encode" ))
        {
            System.out.println( MegaCoder.encode( o.getValue() ) );
        }
        else // Decode
        {
            System.out.println( MegaCoder.decode( o.getValue() ) );
        }
    }

    private static String uncodeName( String codeName )
    {
        if ( codeName.endsWith( ".code.rel" ) )
        {
            return codeName.replace( ".code", ".uncode" );
        }
        else
        {
            return codeName.replace( ".rel", ".uncode.rel" );
        }
    }

    private static String codeName( String uncodeName )
    {
        if ( uncodeName.endsWith( ".uncode.rel" ) )
        {
            return uncodeName.replace( ".uncode" , ".code" );
        }
        else
        {
            return uncodeName.replace( ".rel", ".code.rel" );
        }
    }

}
