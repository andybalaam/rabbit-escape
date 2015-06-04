package rabbitescape.ui.android;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import rabbitescape.engine.menu.AboutText;

import static rabbitescape.engine.i18n.Translation.t;

public class AndroidAboutActivity extends ActionBarActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setVolumeControlStream( AudioManager.STREAM_MUSIC );
        setContentView( R.layout.activity_android_about );

        TextView aboutText = (TextView)findViewById( R.id.aboutText );
        Button aboutMoreButton = (Button)findViewById( R.id.aboutMoreButton );
        Button aboutOkButton = (Button)findViewById( R.id.aboutOkButton );

        aboutText.setText( t( AboutText.text ) );
        aboutMoreButton.setText( t( "Find out more..." ) );
        aboutOkButton.setText( t( "OK" ) );
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GlobalSoundPool.playMusic( this, getResources(), "tryad-let_them_run" );
    }

    @Override
    public void onStop()
    {
        super.onStop();
        GlobalSoundPool.stopIfNotInAnotherActivity( this );
    }

    public void onMoreClicked( View view )
    {
        //GlobalSoundPool.stopMusic();
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( AboutText.url ) );
        startActivity( intent );
    }

    public void onOkClicked( View view )
    {
        finish();
    }
}
