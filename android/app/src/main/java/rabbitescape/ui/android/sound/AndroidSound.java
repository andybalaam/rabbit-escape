package rabbitescape.ui.android.sound;

import android.app.Activity;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

import rabbitescape.render.androidlike.Sound;
import rabbitescape.render.androidutil.SoundResources;

public class AndroidSound implements SoundResources<Activity>, Sound
{
    private SoundPool soundPool = null;
    private SoundEffects sounds = null;
    private MusicPlayer music = new MusicPlayer( null, null, null );
    private boolean muted = true;
    private Resources resources = null;

    @Override
    public void start( Activity activity )
    {
        activity.setVolumeControlStream( AudioManager.STREAM_MUSIC );
        resources = activity.getResources();
        init();
    }

    @SuppressWarnings( "deprecation" ) // SoundPool constructor deprecated in API level 21
    private void init()
    {
        if ( !muted )
        {
            if( soundPool == null )
            {
                soundPool = new SoundPool( 3, AudioManager.STREAM_MUSIC, 100 );
                sounds = new SoundEffects( soundPool, resources );
                music = new MusicPlayer( soundPool, resources, music );
                sounds.ensureLoaded();
            }
            music.ensureLoadedAndPlay();
        }
    }

    @Override
    public void pause()
    {
        music.pause();
    }

    @Override
    public void dispose()
    {
        if ( soundPool != null )
        {
            soundPool.release();
            soundPool = null;
        }

        if ( sounds != null )
        {
            sounds.clear();
        }

        if ( music != null )
        {
            music.clear();
            // Don't set to null here since we want to hold on to current track
        }
    }

    @Override
    public void setMusic( String track )
    {
        music.switchTrack( track, muted );
    }

    @Override
    public void playSound( String sound )
    {
        if ( sounds != null && !muted )
        {
            sounds.play( sound );
        }
    }

    @Override
    public void mute( boolean muted )
    {
        if ( this.muted == muted )
        {
            return;
        }

        this.muted = muted;
        if ( muted )
        {
            dispose();
        }
        else
        {
            init();
        }
    }
}
