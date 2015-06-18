package rabbitescape.ui.android.sound;

import android.content.res.Resources;
import android.media.SoundPool;

class SoundEffectsLoadingThread extends Thread
{
    private final Resources resources;
    private final SoundPool soundPool;
    private final LoadedClips clips;

    private boolean running = false;
    private boolean stopping = false;

    public SoundEffectsLoadingThread(
        SoundPool soundPool, Resources resources, LoadedClips clips )
    {
        this.soundPool = soundPool;
        this.resources = resources;
        this.clips = clips;
    }

    @Override
    public void run()
    {
        running = true;

        try
        {
            String[] sounds = resources.getAssets().list( "sounds" );
            for ( String sound : sounds )
            {
                if ( stopping )
                {
                    break;
                }

                if ( sound.endsWith( ".ogg" ) )
                {
                    String resourcePath = "sounds/" + sound;
                    int clipId = soundPool.load( resources.getAssets().openFd( resourcePath ), 1 );
                    clips.put( resourcePath, clipId );
                }
            }
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
        finally
        {
            running = false;
        }
    }

    public void pleaseStop()
    {
        stopping = true;
    }

    public boolean isRunning()
    {
        return running;
    }
}
