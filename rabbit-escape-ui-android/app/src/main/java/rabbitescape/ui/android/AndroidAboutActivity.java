package rabbitescape.ui.android;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import rabbitescape.engine.menu.AboutText;
import rabbitescape.engine.menu.Menu;

import static rabbitescape.engine.i18n.Translation.t;

public class AndroidAboutActivity extends ActionBarActivity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_android_about );

        TextView aboutText = (TextView)findViewById( R.id.aboutText );
        Button aboutMoreButton = (Button)findViewById( R.id.aboutMoreButton );
        Button aboutOkButton = (Button)findViewById( R.id.aboutOkButton );

        aboutText.setText( t( AboutText.text ) );
        aboutMoreButton.setText( t( "Find out more..." ) );
        aboutOkButton.setText( t( "OK" ) );
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
