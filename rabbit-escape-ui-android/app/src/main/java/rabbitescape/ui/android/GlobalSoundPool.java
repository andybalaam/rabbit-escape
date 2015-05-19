package rabbitescape.ui.android;

import android.content.res.Resources;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GlobalSoundPool
{
    public static final SoundPool soundPool = new SoundPool( 3, AudioManager.STREAM_MUSIC, 100 );
    private static final Map<String, Integer> clips = new HashMap<String, Integer>();
    private static boolean soundsLoaded = false;

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
                        clips.put( sound, id );
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

    public static void init( Resources resources )
    {
        if ( soundsLoaded )
        {
            return;
        }
        soundsLoaded = true;

        new Thread( new SoundLoader( resources ) ).start();
    }

    public static void play( String sound )
    {
        try
        {
            Integer id = clips.get( sound + ".ogg" );
            if ( id != null )
            {
                soundPool.play( id, 0.5f, 0.5f, 1, 0, 1 );
            }
        }
        catch ( Throwable t )
        {
            // Ignore failures caused by sound problems
            t.printStackTrace();
        }
    }
}
