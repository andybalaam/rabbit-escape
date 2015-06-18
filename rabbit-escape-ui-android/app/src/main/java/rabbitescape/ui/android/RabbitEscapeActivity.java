package rabbitescape.ui.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import rabbitescape.render.androidutil.Lifecycle2SoundEvents;
import rabbitescape.ui.android.sound.AndroidSound;

public class RabbitEscapeActivity extends ActionBarActivity
{
    protected static final AndroidSound sound = Globals.sound;
    private static final Lifecycle2SoundEvents<Activity> soundEvents = Globals.soundEvents;

    @Override
    protected void onRestart()
    {
        super.onRestart();
        soundEvents.onRestart( this );
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        soundEvents.onStart( this );
    }

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        soundEvents.onCreate( this );
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        soundEvents.onResume( this );
    }

    @Override
    protected void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        soundEvents.onSaveInstanceState( this );
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        soundEvents.onPause( this );
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        soundEvents.onStop( this );
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        soundEvents.onDestroy( this );
    }
}
