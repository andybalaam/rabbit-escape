package rabbitescape.ui.android;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuDefinition;
import rabbitescape.engine.menu.MenuItem;

import static rabbitescape.engine.i18n.Translation.t;

public class AndroidMenuActivity extends ActionBarActivity
{
    private static final String POSITION = "rabbitescape.position";

    private int[] positions;

    private Menu mainMenu = MenuDefinition.mainMenu( new AndroidPreferencesBasedLevelsCompleted() );

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_android_menu );

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

        final Menu menu = navigateToMenu( positions );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, itemsAsStrings( menu ) );

        ListView listView = (ListView)findViewById( R.id.listView );
        listView.setAdapter( adapter );

        addItemListener( menu, listView );
    }

    private String[] itemsAsStrings( Menu menu )
    {
        String[] ret = new String[ menu.items.length ];

        for ( int i = 0; i < menu.items.length; ++i )
        {
            MenuItem item = menu.items[i];

            ret[i] = t( item.name, item.nameParams );
        }

        return ret;
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
                    MenuItem item = menu.items[position];
                    if ( item.type != MenuItem.Type.MENU )
                    {
                        Log.i( "artific", item.type.name() );
                    }
                    else
                    {
                        Intent intent = new Intent( parentActivity, AndroidMenuActivity.class );
                        intent.putExtra( POSITION, appendToArray( positions, position ) );
                        startActivity( intent );
                    }
                }
            }
        );
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

    private Menu navigateToMenu( int[] positions )
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
