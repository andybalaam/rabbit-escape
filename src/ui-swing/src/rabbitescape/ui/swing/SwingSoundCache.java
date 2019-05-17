package rabbitescape.ui.swing;

import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

/**
 * For now, caches all sounds you ask for, and disposes them when asked.
 */
public class SwingSoundCache
{
    private final Map<String, Clip> clips = new HashMap<String, Clip>();

    /**
     * Could throw lots of different exceptions - we will ignore them all,
     * since audio is non-critical.
     */
    public Clip get( String name ) throws Exception
    {
        Clip ret = clips.get( name );
        if ( ret == null )
        {
            ret = loadClip( name );
            clips.put( name, ret );
        }
        else
        {
            ret.stop();
            ret.setFramePosition( 0 );
        }
        return ret;
    }

    private Clip loadClip( String name ) throws Exception
    {
        AudioInputStream stream = AudioSystem.getAudioInputStream(
            getClass().getResource(
                "/rabbitescape/ui/swing/" + name + ".wav"
            )
        );

        Clip clip = (Clip)AudioSystem.getLine(
            new DataLine.Info( Clip.class, stream.getFormat() ) );

        clip.open( stream );

        return clip;
    }

    public void dispose()
    {
        for ( Clip clip : clips.values() )
        {
            clip.stop();
            clip.flush();
        }
        clips.clear();
    }

    public void stopAll()
    {
        for ( Clip clip : clips.values() )
        {
            clip.stop();
        }
    }

    public void remove( String name )
    {
        Clip clip = clips.remove( name );
        if ( clip != null )
        {
            clip.stop();
            clip.flush();
        }
    }
}
