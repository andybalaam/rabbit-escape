package rabbitescape.ui.android;

import android.app.Activity;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Map;

public class GlobalSoundPool
{
    private static final int LOADING = -1000;

    private static final SoundPool soundPool = new SoundPool( 3, AudioManager.STREAM_MUSIC, 100 );
    private static final Map<String, Integer> clips = new HashMap<String, Integer>();
    private static final float MUSIC_VOLUME = 0.3f;
    private static final float SOUNDS_VOLUME = 0.5f;
    private static boolean soundsLoaded = false;
    private static int playingMusicId = -1;
    private static Activity currentActivity;
    private static String currentMusicTrack;

    private static class SoundLoader implements Runnable
    {
        private final Resources resources;

        public SoundLoader( Resources resources )
        {
            this.resources = resources;
        }

        @Override
        public void run()
        {
            try
            {
                String[] sounds = resources.getAssets().list( "sounds" );
                for ( String sound : sounds )
                {
                    if ( sound.endsWith( ".ogg" ) )
                    {
                        String resourcePath = "sounds/" + sound;
                        int id = soundPool.load( resources.getAssets().openFd( resourcePath ), 1 );
                        clips.put( resourcePath, id );
                    }
                }
            }
            catch ( Throwable t )
            {
                // Ignore failures caused by sound problems
                t.printStackTrace();
            }
        }
    }

    private static class MusicTrackLoader implements Runnable
    {
        private final Resources resources;
        private final String trackPath;

        public MusicTrackLoader( Resources resources, String trackPath )
        {
            this.resources = resources;
            this.trackPath = trackPath;
        }

        @Override
        public void run()
        {
            try
            {
                int id = soundPool.load( resources.getAssets().openFd( trackPath ), 1 );
                clips.put( trackPath, id );
                Thread.sleep( 1000 ); // Give the sound pool a chance to settle
                switchMusic( trackPath );
            }
            catch ( Throwable t )
            {
                // Ignore failures caused by sound problems
                t.printStackTrace();
            }
        }
    }

    public static void init( Resources resources )
    {
        if ( soundsLoaded )
        {
            return;
        }
        soundsLoaded = true;

        new Thread( new SoundLoader( resources ) ).start();
    }

    public static void playMusic( Activity activity, Resources resources, String track )
    {
        currentActivity = activity;

        try
        {
            if ( track == null || track.equals( currentMusicTrack ) )
            {
                return;
            }
            currentMusicTrack = track;

            String trackPath = "music/" + track + ".ogg";

            if ( clips.get( trackPath ) == null )
            {
                clips.put( trackPath, LOADING );
                new Thread( new MusicTrackLoader( resources, trackPath ) ).start();
            }
            else
            {
                switchMusic( trackPath );
            }
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
    }

    public static void stopIfNotInAnotherActivity( Activity activity )
    {
        if ( currentActivity == activity )
        {
            stopMusic();
            // TODO: release resources, but then we must re-load when resumed
        }
        currentActivity = activity;
    }

    private static synchronized void switchMusic( String trackPath )
    {
        stopMusic();

        Integer id = clips.get( trackPath );
        if ( id != null && id != LOADING )
        {
            playingMusicId = soundPool.play( id, MUSIC_VOLUME, MUSIC_VOLUME, 2, -1, 1 );
        }
    }

    public static void stopMusic()
    {
        if ( playingMusicId != -1 )
        {
            soundPool.stop( playingMusicId );
        }
    }

    public static void play( String sound )
    {
        try
        {
            Integer id = clips.get( "sounds/" + sound + ".ogg" );
            if ( id != null )
            {
                soundPool.play( id, SOUNDS_VOLUME, SOUNDS_VOLUME, 1, 0, 1 );
            }
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
    }
}
