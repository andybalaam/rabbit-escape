package rabbitescape.ui.android;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.LightingColorFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.util.Map;

import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;


public class AndroidGameActivity extends ActionBarActivity
{
    private boolean muted;
    private SharedPreferences prefs;
    private ImageView muteButton;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_android_game );
        Resources resources = getResources();

        prefs = getPreferences( MODE_PRIVATE );
        muted = prefs.getBoolean( "muted", false );

        muteButton = (ImageView)findViewById( R.id.muteButton );
        updateMuteButton();

        World world = new LoadWorldFile( new RealFileSystem() ).load( "test/level_01.rel" );

        createAbilityButtons( world, resources );
        BitmapCache<AndroidBitmap> bitmapCache = createBitmapCache( resources );

        LinearLayout topLayout = (LinearLayout)findViewById( R.id.topLayout );
        topLayout.addView( new MySurfaceView( this, bitmapCache, world ) );
    }

    private void createAbilityButtons( World world, Resources resources )
    {
        RadioGroup abilitiesGroup = (RadioGroup)findViewById( R.id.abilitiesGroup );

        int i = 0;
        for ( Token.Type ability : world.abilities.keySet() )
        {
            abilitiesGroup.addView(
                new AbilityButton( this, resources, abilitiesGroup, ability.name(), i ) );

            ++i;
        }

        abilitiesGroup.setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged( RadioGroup radioGroup, int buttonIndex )
                {
                    for ( int i = 0; i < radioGroup.getChildCount(); i++ )
                    {
                        AbilityButton button = (AbilityButton)( radioGroup.getChildAt( i ) );

                        button.setChecked( i == buttonIndex );
                    }
                }
            }
        );
    }

    private BitmapCache<AndroidBitmap> createBitmapCache( Resources resources )
    {
        return new BitmapCache<AndroidBitmap>( new AndroidBitmapLoader( resources ), 500 );
    }

    public void onMuteClicked( View view )
    {
        muted = !muted;

        prefs.edit().putBoolean( "muted", muted ).commit();

        updateMuteButton();
    }

    private void updateMuteButton()
    {
        muteButton.setImageDrawable(
            getResources().getDrawable( muted ? R.drawable.menu_muted : R.drawable.menu_unmuted ) );
        muteButton.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
