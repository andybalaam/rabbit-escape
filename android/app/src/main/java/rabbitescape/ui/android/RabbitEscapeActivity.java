package rabbitescape.ui.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.androidutil.Lifecycle2SoundEvents;
import rabbitescape.ui.android.sound.AndroidSound;

public abstract class RabbitEscapeActivity extends AppCompatActivity
{
    protected static final AndroidSound sound = Globals.sound;
    private static final Lifecycle2SoundEvents<Activity> soundEvents = Globals.soundEvents;

    private Config config;
    private boolean muted;
    private NoisyReceiver noisyReceiver;

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

        config = AndroidConfigSetup.createConfig(
            getSharedPreferences( "rabbitescape", MODE_PRIVATE )  );
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        soundEvents.onResume( this );

        muted = ConfigTools.getBool( config, AndroidConfigSetup.CFG_MUTED );
        sound.mute( muted );
        updateMuteButton( muted );

        listenForAudioBecomingNoisy();
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

        stopListeningForAudioBecomingNoisy();
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

    public Config getConfig()
    {
        return config;
    }

    public void onMuteClicked( View view )
    {
        setMuted( !getMuted() );
    }

    public void setMuted( boolean newMuted )
    {
        muted = newMuted;

        sound.mute( muted );

        ConfigTools.setBool( config, AndroidConfigSetup.CFG_MUTED, muted );

        updateMuteButton( muted );
    }

    public boolean getMuted()
    {
        return muted;
    }

    public abstract void updateMuteButton( boolean muted );

    private class NoisyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
            setMuted( true );
        }
    }

    private void listenForAudioBecomingNoisy()
    {
        noisyReceiver = new NoisyReceiver();
        registerReceiver( noisyReceiver, new IntentFilter( "android.media.AUDIO_BECOMING_NOISY" ) );

    }

    private void stopListeningForAudioBecomingNoisy()
    {
        unregisterReceiver( noisyReceiver );
    }
}
