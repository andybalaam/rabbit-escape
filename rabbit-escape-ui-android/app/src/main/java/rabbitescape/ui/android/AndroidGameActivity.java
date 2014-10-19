package rabbitescape.ui.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.Set;

import rabbitescape.engine.CompletedLevelWinListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.LevelsCompleted;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.RealFileSystem;
import rabbitescape.render.BitmapCache;

import static android.text.TextUtils.join;
import static android.text.TextUtils.split;


public class AndroidGameActivity extends ActionBarActivity implements NumLeftListener
{
    // Constants
    public static final String INTENT_LEVEL        = "rabbitescape.level";
    public static final String INTENT_LEVEL_NUMBER = "rabbitescape.levelnumber";
    public static final String PREFS_MUTED = "rabbitescape.muted";
    public static final String STATE_CHECKED_ABILITY_INDEX = "rabbitescape.checkedAbilityIndex";

    // System
    private SharedPreferences prefs;
    private LevelsCompleted levelsCompleted;

    // Saved state
    private boolean muted;


    private ImageView muteButton;
    private ImageView pauseButton;
    private LinearLayout topLayout;
    private RadioGroup abilitiesGroup;

    private GameSurfaceView gameSurface;
    private Token.Type[] abilities;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Intent intent = getIntent();
        String levelFileName = intent.getStringExtra( INTENT_LEVEL );
        int    levelNum      = intent.getIntExtra( INTENT_LEVEL_NUMBER, 0 );

        staticInit();
        World world = loadWorld( levelFileName, savedInstanceState );
        buildDynamicUi( getResources(), world, levelFileName, levelNum, savedInstanceState );
        restoreFromState( savedInstanceState );
    }

    private World loadWorld( String levelFileName, Bundle savedInstanceState )
    {
        if ( savedInstanceState != null )
        {
            String savedWorld = savedInstanceState.getString( AndroidGameLoop.STATE_WORLD );
            if ( savedWorld != null )
            {
                return TextWorldManip.createWorld( split( savedWorld, "\n" ) );
            }
        }

        return new LoadWorldFile( new RealFileSystem() ).load( levelFileName );
    }

    private void buildDynamicUi(
        Resources resources,
        World world,
        String levelFileName,
        int levelNum,
        Bundle savedInstanceState
    )
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( metrics );

        createAbilities( world, resources );

        gameSurface = new GameSurfaceView(
            this,
            this,
            createBitmapCache( resources ),
            world,
            new CompletedLevelWinListener( levelFileName, levelNum, levelsCompleted ),
            metrics.density,
            savedInstanceState
        );

        topLayout.addView( gameSurface );
    }

    private void staticInit()
    {
        prefs = getPreferences( MODE_PRIVATE );
        levelsCompleted = new AndroidPreferencesBasedLevelsCompleted();

        setContentView( R.layout.activity_android_game );

        muteButton = (ImageView)findViewById( R.id.muteButton );
        pauseButton = (ImageView)findViewById( R.id.pauseButton );
        topLayout = (LinearLayout)findViewById( R.id.topLayout );
        abilitiesGroup = (RadioGroup)findViewById( R.id.abilitiesGroup );
    }

    private void createAbilities( World world, Resources resources )
    {
        Set<Token.Type> keys = world.abilities.keySet();
        abilities = new Token.Type[ keys.size() ];
        int i = 0;
        for ( Token.Type ability : keys )
        {
            abilities[i] = ability;
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
                    gameSurface.chooseAbility( abilities[buttonIndex] );

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

        prefs.edit().putBoolean( PREFS_MUTED, muted ).commit();

        updateMuteButton();
    }

    private void updateMuteButton()
    {
        muteButton.setImageDrawable(
            getResources().getDrawable( muted ? R.drawable.menu_muted : R.drawable.menu_unmuted ) );
        muteButton.invalidate();
    }

    public void onPauseClicked( View view )
    {
        updatePauseButton( gameSurface.togglePaused() );
    }

    private void updatePauseButton( boolean paused )
    {
        pauseButton.setImageDrawable(
            getResources().getDrawable( paused ? R.drawable.menu_unpause : R.drawable.menu_pause )
        );
        pauseButton.invalidate();
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        // The super call will call onSaveInstanceState on:
        // - GameSurfaceView
        // - each AbilityButton (but we do the saving here because it's easier

        outState.putInt( STATE_CHECKED_ABILITY_INDEX, checkedAbilityIndex() );

        if ( gameSurface != null )
        {
            gameSurface.onSaveInstanceState( outState );
        }

        // Mute state is stored in a preference, so no need to store it here.
    }

    private void restoreFromState( Bundle savedInstanceState )
    {
        muted = prefs.getBoolean( PREFS_MUTED, false );
        updateMuteButton();

        if ( savedInstanceState != null )
        {
            int checkedAbility = savedInstanceState.getInt( STATE_CHECKED_ABILITY_INDEX, -1 );
            if ( checkedAbility != -1 )
            {
                abilitiesGroup.check( checkedAbility );
            }

            // Cheating here by pulling the pause state out, even though it is
            // pulled out in AndroidGameLoop.  That happens later, so is inconvenient
            // for us to wait for.
            updatePauseButton(
                savedInstanceState.getBoolean( AndroidGameLoop.STATE_PAUSED, false ) );
        }
    }

    private int checkedAbilityIndex()
    {
        for ( int i = 0; i < abilitiesGroup.getChildCount(); i++ )
        {
            AbilityButton button = (AbilityButton)( abilitiesGroup.getChildAt( i ) );

            if ( button.isChecked() )
            {
                return i;
            }
        }

        return -1;
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

    @Override
    public void numLeft( Token.Type ability, int numLeft )
    {
        if ( numLeft == 0 )
        {
            AbilityButton button = buttonForAbility( ability );
            button.disable();
            button.setChecked( false );
        }
    }

    public static class UnrecognisedAbility extends RabbitEscapeException
    {
        public Token.Type ability;

        public UnrecognisedAbility( Token.Type ability )
        {
            this.ability = ability;
        }
    }

    private AbilityButton buttonForAbility( Token.Type ability )
    {
        int i = 0;
        for ( Token.Type ab : abilities )
        {
            if ( ab == ability )
            {
                return (AbilityButton)abilitiesGroup.getChildAt( i );
            }
            ++i;
        }
        throw new UnrecognisedAbility( ability );
    }
}
