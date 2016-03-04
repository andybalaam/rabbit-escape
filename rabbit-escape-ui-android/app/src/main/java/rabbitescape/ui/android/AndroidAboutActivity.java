package rabbitescape.ui.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.artificialworlds.rabbitescape.R;

import rabbitescape.engine.menu.AboutText;

import static rabbitescape.engine.i18n.Translation.t;

public class AndroidAboutActivity extends RabbitEscapeActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        sound.setMusic( "tryad-let_them_run" );

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
        sound.setMusic( "tryad-let_them_run" );
    }

    @Override
    public void updateMuteButton( boolean muted )
    {
        // No mute button on this activity, so no need to update it
    }

    public void onMoreClicked( View view )
    {
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( AboutText.url ) );
        startActivity( intent );
    }

    public void onOkClicked( View view )
    {
        finish();
    }
}
