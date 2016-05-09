package rabbitescape.ui.android;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import net.artificialworlds.rabbitescape.R;

import rabbitescape.engine.config.TapTimer;
import rabbitescape.engine.menu.ByNameConfigBasedLevelsCompleted;
import rabbitescape.engine.menu.LevelMenuItem;
import rabbitescape.engine.menu.LevelsList;
import rabbitescape.engine.menu.LoadLevelsList;
import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;

public class AndroidMenuActivity extends RabbitEscapeActivity
{
    private static final String POSITION = "rabbitescape.position";
    private static final String STATE_SELECTED_ITEM = "rabbitescape.selected_menu_item";

    private int[] positions;

    private Menu menu = null;
    private ListView listView = null;
    private Button muteButton = null;

    // Saved state
    public int selectedItemPosition = -1;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        sound.setMusic( "tryad-let_them_run" );

        setContentView( R.layout.activity_android_menu );

        muteButton = (Button)findViewById( R.id.menuMuteButton );

        LevelsList levelsList;

        if ( TapTimer.matched )
        {
            levelsList = LoadLevelsList.load( MenuDefinition.allLevels );
        }
        else
        {
            levelsList = LoadLevelsList.load( LevelsList.excludingHidden( MenuDefinition.allLevels ) );
        }

        Menu mainMenu = MenuDefinition.mainMenu(
            new ByNameConfigBasedLevelsCompleted( getConfig(), levelsList ),
            levelsList,
            false
        );

        if ( savedInstanceState != null )
        {
            selectedItemPosition = savedInstanceState.getInt( STATE_SELECTED_ITEM, -1 );
        }

        Intent intent = getIntent();
        positions = intent.getIntArrayExtra( POSITION );
        if ( positions == null )
        {
            positions = new int[ 0 ];
        }

        if ( positions.length > 0 )
        {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        menu = navigateToMenu( positions, mainMenu );

        listView = (ListView)findViewById( R.id.listView );
        listView.setSelection( selectedItemPosition );
        addItemListener( menu, listView );
    }

    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        outState.putInt( STATE_SELECTED_ITEM, listView.getSelectedItemPosition() );
    }

    @Override
    public void onResume()
    {
        super.onResume();
        sound.setMusic( "tryad-let_them_run" );
        menu.refresh();
        listView.setAdapter( new MenuListAdapter( this, menu ) );
        listView.setSelection( selectedItemPosition );
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

    private void addItemListener( final Menu menu, ListView listView )
    {
        final AndroidMenuActivity parentActivity = this;

        listView.setOnItemClickListener(
            new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(
                    AdapterView<?> adapterView, View view, int position, long id )
                {
                    selectedItemPosition = position;
                    MenuItem item = menu.items[position];
                    switch( item.type )
                    {
                        case MENU:
                        {
                            Intent intent = new Intent( parentActivity, AndroidMenuActivity.class );
                            intent.putExtra( POSITION, appendToArray( positions, position ) );
                            startActivity( intent );
                            return;
                        }
                        case ABOUT:
                        {
                            about( parentActivity );
                            return;
                        }
                        case LEVEL:
                        {
                            level( parentActivity, (LevelMenuItem)item );
                            return;
                        }
                        case QUIT:
                        {
                            exit();
                            return;
                        }
                        case DEMO:
                        {
                            return;
                        }
                        default:
                        {
                            throw new UnknownMenuItemType( item );
                        }
                    }
                }
            }
        );
    }

    private void exit()
    {
        finish();
    }

    private void about( AndroidMenuActivity parentActivity )
    {
        Intent intent = new Intent( parentActivity, AndroidAboutActivity.class );
        startActivity( intent );
    }

    private void level( AndroidMenuActivity parentActivity, LevelMenuItem item )
    {
        Intent intent = new Intent( parentActivity, AndroidGameActivity.class );
        intent.putExtra( AndroidGameActivity.INTENT_LEVELS_DIR,   item.levelsDir );
        intent.putExtra( AndroidGameActivity.INTENT_LEVEL,        item.fileName );
        intent.putExtra( AndroidGameActivity.INTENT_LEVEL_NUMBER, item.levelNumber );
        startActivity( intent );
    }

    private int[] appendToArray( int[] positions, int position )
    {
        int[] ret = new int[ positions.length + 1 ];
        System.arraycopy( positions, 0, ret, 0, positions.length );
        ret[positions.length] = position;
        return ret;
    }

    private int[] shrunk( int[] positions )
    {
        int[] ret = new int[ positions.length - 1 ];
        System.arraycopy( positions, 0, ret, 0, positions.length - 1 );
        return ret;
    }

    private Menu navigateToMenu( int[] positions, Menu mainMenu )
    {
        Menu ret = mainMenu;
        for ( int pos : positions )
        {
            if ( pos < 0 || pos >= ret.items.length )
            {
                return mainMenu;
            }

            ret = ret.items[pos].menu;

            if ( ret == null )
            {
                return mainMenu;
            }
        }
        return ret;
    }

    @Override
    public Intent getSupportParentActivityIntent()
    {
        Intent intent = new Intent( this, AndroidMenuActivity.class );
        intent.putExtra( POSITION, shrunk( positions ) );
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu( android.view.Menu menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.android_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( android.view.MenuItem item )
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
}
