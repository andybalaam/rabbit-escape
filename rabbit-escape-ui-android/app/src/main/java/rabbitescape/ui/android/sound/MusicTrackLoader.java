package rabbitescape.ui.android.sound;

import android.content.res.Resources;
import android.media.SoundPool;

class MusicTrackLoader extends Thread
{
    private final SoundPool soundPool;
    private final Resources resources;
    private final String trackPath;
    private final MusicPlayer music;
    private boolean running;

    public MusicTrackLoader(
        SoundPool soundPool, Resources resources, String trackPath, MusicPlayer music )
    {
        this.soundPool = soundPool;
        this.resources = resources;
        this.trackPath = trackPath;
        this.music = music;
        this.running = false;
    }

    @Override
    public void run()
    {
        running = true;

        try
        {
            int clipId = soundPool.load( resources.getAssets().openFd( trackPath ), 1 );
            Thread.sleep( 1000 ); // Give the sound pool a chance to settle
            music.addTrack( trackPath, clipId );
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

    public boolean isRunning()
    {
        return running;
    }
}
