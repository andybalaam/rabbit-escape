package rabbitescape.ui.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import rabbitescape.engine.CompletedLevelWinListener;
import rabbitescape.engine.LevelWinListener;
import rabbitescape.engine.LoadWorldFile;
import rabbitescape.engine.MultiLevelWinListener;
import rabbitescape.engine.Token;
import rabbitescape.engine.World;
import rabbitescape.engine.WorldStatsListener;
import rabbitescape.engine.err.RabbitEscapeException;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.LevelsCompleted;
import rabbitescape.engine.menu.LevelsList;
import rabbitescape.engine.menu.LoadLevelsList;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.textworld.TextWorldManip;
import rabbitescape.engine.util.RealFileSystem;

import static android.text.TextUtils.split;
import static rabbitescape.engine.i18n.Translation.t;

import net.artificialworlds.rabbitescape.R;

public class AndroidGameActivity extends RabbitEscapeActivity
    implements NumLeftListener, WorldStatsListener
{
    // Constants
    public static final String INTENT_LEVELS_DIR   = "rabbitescape.levelsdir";
    public static final String INTENT_LEVEL        = "rabbitescape.level";
    public static final String INTENT_LEVEL_NUMBER = "rabbitescape.levelnumber";
    public static final String STATE_CHECKED_ABILITY_INDEX = "rabbitescape.checkedAbilityIndex";

    public AlertDialog currentDialog;
    public RadioGroup abilitiesGroup;

    // System
    private LevelsCompleted levelsCompleted;

    private Button muteButton;
    private Button pauseButton;
    private Button speedButton;
    private LinearLayout topLayout;

    public GameSurfaceView gameSurface;
    private Token.Type[] abilities;
    private TextView worldStatsTextView;
    public World world;
    public Game game;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        Intent intent = getIntent();
        String levelsDir     = intent.getStringExtra( INTENT_LEVELS_DIR );
        String levelFileName = intent.getStringExtra( INTENT_LEVEL );
        int    levelNum      = intent.getIntExtra( INTENT_LEVEL_NUMBER, 0 );

        Resources resources = getResources();

        staticInit();
        world = loadWorld( levelFileName, savedInstanceState );

        sound.setMusic( world.music );

        LevelWinListener winListener = new MultiLevelWinListener(
            new CompletedLevelWinListener( levelsDir, levelNum, levelsCompleted ),
            new AndroidAlertWinListener( this )
        );

        game = new Game(
            SingletonBitmapCache.instance( resources ),
            getConfig(),
            world,
            winListener,
            savedInstanceState
        );

        buildDynamicUi( resources, game, world, savedInstanceState );

        if ( savedInstanceState == null )
        {
            Dialogs.intro( this, world );
        }
        else
        {
            restoreFromState( savedInstanceState );
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sound.setMusic( world.music );
    }

    private World loadWorld( String levelFileName, Bundle savedInstanceState )
    {
        if ( savedInstanceState != null )
        {
            String savedWorld = savedInstanceState.getString( AndroidGameLaunch.STATE_WORLD );
            if ( savedWorld != null )
            {
                return TextWorldManip.createWorld( this, split( savedWorld, "\n" ) );
            }
        }

        return new LoadWorldFile( new RealFileSystem() ).load( this, levelFileName );
    }

    private void buildDynamicUi(
        Resources resources,
        Game game,
        World world,
        Bundle savedInstanceState
    )
    {
        createAbilities( world, resources );
        updatePauseButton( world.paused );

        gameSurface = new GameSurfaceView( this, this, sound, game, world );

        if ( savedInstanceState != null )
        {
            // Get the "fast" state from savedInstanceState because we have no
            // game yet - that won't be created until we have a surface.

            updateSpeedButton(
                savedInstanceState.getBoolean( AndroidGameLaunch.STATE_FAST_PRESSED, false ) );
        }

        topLayout.addView( gameSurface );
    }

    private void staticInit()
    {
        LevelsList levelsList = LoadLevelsList.load( MenuDefinition.allLevels );
        levelsCompleted = new ByNameConfigBasedLevelsCompleted( getConfig(), levelsList );

        setContentView( R.layout.activity_android_game );

        muteButton = (Button)findViewById( R.id.muteButton );
        pauseButton = (Button)findViewById( R.id.pauseButton );
        speedButton = (Button)findViewById( R.id.speedButton );
        topLayout = (LinearLayout)findViewById( R.id.topLayout );
        abilitiesGroup = (RadioGroup)findViewById( R.id.abilitiesGroup );
        worldStatsTextView = (TextView)findViewById( R.id.worldStats );
    }

    private void createAbilities( World world, Resources resources )
    {
        Set<Map.Entry<Token.Type, Integer>> entries = world.abilities.entrySet();
        abilities = new Token.Type[ entries.size() ];

        int i = 0;
        for ( Map.Entry<Token.Type, Integer> entry : entries )
        {
            abilities[i] = entry.getKey();
            abilitiesGroup.addView(
                new AbilityButton(
                    this,
                    resources,
                    abilitiesGroup,
                    entry.getKey().name(),
                    entry.getValue(),
                    i
                )
            );
            ++i;
        }

        abilitiesGroup.setOnCheckedChangeListener(
            new RadioGroup.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged( RadioGroup radioGroup, int buttonIndex )
                {
                    game.gameLaunch.chosenAbility = abilities[buttonIndex];

                    for ( int i = 0; i < radioGroup.getChildCount(); i++ )
                    {
                        AbilityButton button = (AbilityButton)( radioGroup.getChildAt( i ) );

                        button.setChecked( i == buttonIndex );
                    }
                }
            }
        );
    }

    @Override
    public void updateMuteButton( boolean muted )
    {
        muteButton.setCompoundDrawablesWithIntrinsicBounds(
            getResources().getDrawable( muted ? R.drawable.menu_muted : R.drawable.menu_unmuted ),
            null,
            null,
            null
        );
        muteButton.invalidate();
    }

    public void onPauseClicked( View view )
    {
        updatePauseButton( gameSurface.togglePaused() );
    }

    private void updatePauseButton( boolean paused )
    {
        pauseButton.setCompoundDrawablesWithIntrinsicBounds(
            getResources().getDrawable( paused ? R.drawable.menu_unpause : R.drawable.menu_pause ),
            null,
            null,
            null
        );
        pauseButton.invalidate();

        // Show Overlay
        game.gameLaunch.graphics.redraw();
    }

    public void onSpeedClicked( View view )
    {
        updateSpeedButton( gameSurface.toggleSpeed() );
    }

    private void updateSpeedButton( boolean fast )
    {
        int speedRes = fast ? R.drawable.menu_speedup_active : R.drawable.menu_speedup_inactive;

        speedButton.setCompoundDrawablesWithIntrinsicBounds(
            getResources().getDrawable( speedRes ), null, null, null );

        speedButton.invalidate();
    }

    public void onExplodeAllClicked( View view )
    {
        World world = game.gameLaunch.physics.world;

        switch ( world.completionState() )
        {
            case RUNNING:
            case PAUSED:
            {
                Dialogs.explode( this, world );
                break;
            }
            default:
            {
                // Don't do anything if we've finished already
                break;
            }
        }
    }

    public void setPaused( World world, boolean paused )
    {
        world.setPaused( paused );
        updatePauseButton( paused );
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        // The super call will call onSaveInstanceState on:
        // - GameSurfaceView
        // - each AbilityButton (but we do the saving here because it's easier

        outState.putInt( STATE_CHECKED_ABILITY_INDEX, checkedAbilityIndex() );

        game.gameLaunch.onSaveInstanceState( outState );

        // Mute state is stored in a preference, so no need to store it here.
    }

    private void restoreFromState( Bundle savedInstanceState )
    {
        int checkedAbility = savedInstanceState.getInt( STATE_CHECKED_ABILITY_INDEX, -1 );
        if ( checkedAbility != -1 )
        {
            abilitiesGroup.check( checkedAbility );
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
    public boolean onCreateOptionsMenu( Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.game, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if ( id == R.id.action_settings )
        {
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void numLeft( Token.Type ability, int numLeft )
    {
        AbilityButton button = buttonForAbility( ability );

        button.setNumLeft( numLeft );

        if ( numLeft == 0 )
        {
            button.disable();
            button.setChecked( false );
        }
    }

    @Override
    public void worldStats( int num_saved, int num_to_save )
    {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put( "num_saved", num_saved );
        values.put( "num_to_save", num_to_save );

        this.runOnUiThread(
            new Runnable()
            {
                @Override
                public void run()
                {
                    worldStatsTextView.setText( t( "${num_saved} / ${num_to_save}", values ) );
                }
            }
        );
    }

    public static class UnrecognisedAbility extends RabbitEscapeException
    {
        public final Token.Type ability;

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
