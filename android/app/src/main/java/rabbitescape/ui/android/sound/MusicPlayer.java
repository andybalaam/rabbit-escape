package rabbitescape.ui.android.sound;

import android.content.res.Resources;
import android.media.SoundPool;

public class MusicPlayer
{
    private static final int LOADING = -1000;
    private static final float VOLUME = 0.5f;

    private final LoadedClips clips;
    private final SoundPool soundPool;
    private final Resources resources;
    private String currentTrack;
    private Integer nowPlayingClipId;
    private int nowPlayingPlayId;
    private boolean paused;
    private MusicTrackLoader trackLoader;

    public MusicPlayer( SoundPool soundPool, Resources resources, MusicPlayer previousPlayer )
    {
        this.clips = new LoadedClips();
        this.soundPool = soundPool;
        this.resources = resources;

        this.currentTrack = previousPlayer != null
            ? previousPlayer.currentTrack
            : null;

        nowPlayingClipId = null;
        nowPlayingPlayId = -1;
        paused = false;

        trackLoader = null;
    }

    public void ensureLoadedAndPlay()
    {
        if ( currentTrack == null || soundPool == null || resources == null )
        {
            return;
        }

        String currentTrackPath = "music/" + currentTrack + ".ogg";
        Integer currentTrackId = clips.get( currentTrackPath );
        if ( nowPlayingClipId != null )
        {
            if ( nowPlayingClipId.equals( currentTrackId ) )
            {
                if ( paused )
                {
                    try
                    {
                        soundPool.resume( nowPlayingPlayId );
                        paused = false;
                    }
                    catch ( Throwable t )
                    {
                        // Ignore failures caused by sound problems
                        t.printStackTrace();
                    }
                }
                return; // We're already playing the right thing
            }
            else
            {
                stop();
            }
        }

        if ( currentTrackId == null )
        {
            clips.put( currentTrackPath, LOADING );

            trackLoader = new MusicTrackLoader(
                soundPool, resources, currentTrackPath, this );

            trackLoader.start();
        }
        else if ( currentTrackId != LOADING )
        {
            playId( currentTrackId );
        }
    }

    public void pause()
    {
        if ( nowPlayingPlayId != -1 )
        {
            try
            {
                soundPool.pause( nowPlayingPlayId );
                paused = true;
            }
            catch ( Throwable t )
            {
                // Ignore failures caused by sound problems
                t.printStackTrace();
            }
        }
    }

    public void stop()
    {
        if ( nowPlayingPlayId != -1 )
        {
            try
            {
                soundPool.stop( nowPlayingPlayId );
                nowPlayingClipId = null;
                nowPlayingPlayId = -1;
                paused = false;
            }
            catch ( Throwable t )
            {
                // Ignore failures caused by sound problems
                t.printStackTrace();
            }
        }
    }

    public void clear()
    {
        waitForLoadingThread();
        clips.clear();
    }

    public void switchTrack( String track, boolean muted )
    {
        if ( track == null )
        {
            stop();
        }
        else if ( !track.equals( currentTrack ) )
        {
            currentTrack = track;
            if ( !muted )
            {
                ensureLoadedAndPlay();
            }
        }
    }

    public void addTrack( String trackPath, int clipId )
    {
        clips.put( trackPath, clipId );
        playId( clipId );
    }

    public void playId( Integer clipId )
    {
        try
        {
            nowPlayingPlayId = soundPool.play( clipId, VOLUME, VOLUME, 2, -1, 1 );
            nowPlayingClipId = clipId;
            paused = false;
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
    }

    private void waitForLoadingThread()
    {
        if ( trackLoader == null )
        {
            return;
        }

        while( trackLoader.isRunning() )
        {
            try
            {
                trackLoader.join();
            }
            catch ( InterruptedException e )
            {
                // Nothing to do
            }
        }
    }
}
