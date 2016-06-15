package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;

import javax.sound.sampled.Clip;

import rabbitescape.render.androidlike.Sound;

public class SwingSound implements Sound
{
    private final SwingSoundCache cache;
    private boolean muted;
    private String music;

    public static Sound create( boolean muted )
    {
        // Disable all sound in OpenJDK.  It causes frequent crashes.
        // See https://github.com/andybalaam/rabbit-escape/issues/95
        if ( System.getProperty( "java.vm.name" ).contains( "OpenJDK" ) )
        {
            return new SwingNullSound();
        }
        else
        {
            return new SwingSound( muted );
        }
    }

    private SwingSound( boolean muted )
    {
        this.cache = new SwingSoundCache();
        this.muted = muted;
        this.music = null;
    }

    @Override
    public void playSound( String name )
    {
        if ( muted || name == null )
        {
            return;
        }

        try
        {
            cache.get( "sounds/" + name ).start();
        }
        catch ( Throwable t )
        {
            // All problems with sound, we just ignore
            t.printStackTrace();
        }
    }

    @Override
    public void mute( boolean muted )
    {
        this.muted = muted;
        if ( muted )
        {
            cache.stopAll();
        }
        else
        {
            playMusic();
        }
    }

    @Override
    public void setMusic( String name )
    {
        if ( equalsOrBothNull( name, music ) )
        {
            return;
        }

        stopMusic();   // Could uncache here, but Java gets annoyed if you
                       // keep loading and unloading clips.
        music = name;

        if ( !muted )
        {
            playMusic();
        }
    }

    @Override
    public void dispose()
    {
        stopMusic();
        cache.dispose();
    }

    private void playMusic()
    {
        Clip clip = musicClip();
        if ( clip != null )
        {
            clip.loop( Clip.LOOP_CONTINUOUSLY );
        }
    }

    private void stopMusic()
    {
        Clip clip = musicClip();
        if ( clip != null )
        {
            clip.stop();
        }
    }

    private Clip musicClip()
    {
        if ( music == null )
        {
            return null;
        }

        try
        {
            return cache.get( "music/" + music );
        }
        catch ( Throwable t )
        {
            // All problems with sound, we just ignore
            t.printStackTrace();
            return null;
        }
    }
}
