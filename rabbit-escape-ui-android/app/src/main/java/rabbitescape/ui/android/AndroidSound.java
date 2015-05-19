package rabbitescape.ui.android;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rabbitescape.render.androidlike.Sound;

public class AndroidSound implements Sound
{
    private boolean muted;

    public AndroidSound( boolean muted )
    {
        this.muted = muted;
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public void play( String name )
    {
        if ( muted || name == null )
        {
            return;
        }
        GlobalSoundPool.play( name );
    }

    @Override
    public void mute( boolean muted )
    {
        this.muted = muted;
    }

    @Override
    public void setMusic( String s )
    {
    }
}
