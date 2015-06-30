package rabbitescape.ui.android.sound;

import android.content.res.Resources;
import android.media.SoundPool;

public class SoundEffects
{
    private static final float VOLUME = 1.0f;

    private final SoundPool soundPool;
    private final Resources resources;

    private LoadedClips clips;
    private SoundEffectsLoadingThread loadingThread;

    public SoundEffects( SoundPool soundPool, Resources resources )
    {
        this.soundPool = soundPool;
        this.resources = resources;
        clips = null;
        loadingThread = null;
    }

    public void ensureLoaded()
    {
        if ( clips == null )
        {
            loadClips();
        }
    }

    public void clear()
    {
        killLoadingThread();
        clips.clear();
    }

    private void killLoadingThread()
    {
        if ( loadingThread == null )
        {
            return;
        }

        loadingThread.pleaseStop();
        while( loadingThread.isRunning() )
        {
            try
            {
                loadingThread.join();
            }
            catch ( InterruptedException e )
            {
                // Nothing to do here
            }
        }
        loadingThread = null;
    }

    public void play( String sound )
    {
        try
        {
            Integer id = clips.get( "sounds/" + sound + ".ogg" );
            if ( id != null )
            {
                soundPool.play( id, VOLUME, VOLUME, 1, 0, 1 );
            }
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
    }

    private void loadClips()
    {
        clips = new LoadedClips();
        loadingThread = new SoundEffectsLoadingThread( soundPool, resources, clips );
        loadingThread.start();
    }
}
