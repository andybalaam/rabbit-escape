package rabbitescape.ui.swing;

import static rabbitescape.engine.util.Util.*;

import javax.sound.sampled.Clip;

import rabbitescape.render.androidlike.Sound;

public class SwingSound implements Sound
{
    private final SwingSoundCache cache;
    private boolean muted;
    private String music;

    public SwingSound( boolean muted )
    {
        this.cache = new SwingSoundCache();
        this.muted = muted;
        this.music = null;
    }

    @Override
    public void play( String name )
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

        uncacheMusic();
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

    private void uncacheMusic()
    {
        if ( music != null )
        {
            cache.remove( "music/" + music );
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
