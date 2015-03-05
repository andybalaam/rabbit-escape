package rabbitescape.ui.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import rabbitescape.engine.menu.Menu;
import rabbitescape.engine.menu.MenuItem;

import static rabbitescape.engine.i18n.Translation.t;

public class MenuListAdapter extends ArrayAdapter<MenuItem>
{
    private final MenuItem[] items;
    private final AndroidMenuActivity menuActivity;

    public MenuListAdapter( AndroidMenuActivity menuActivity, Menu menu )
    {
        super( menuActivity, android.R.layout.simple_list_item_1, menu.items );
        this.menuActivity = menuActivity;
        items = menu.items;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public boolean isEnabled( int position )
    {
        return items[position].enabled;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup viewGroup )
    {
        TextView ret;
        if ( convertView != null && convertView instanceof TextView )
        {
            ret = (TextView)convertView;
        }
        else
        {
            ret = (TextView)super.getView( position, convertView, viewGroup );
        }

        MenuItem item = items[ position ];
        ret.setText( t( item.name, item.nameParams ) );
        ret.setTypeface( null, menuActivity.selectedItemPosition == position ? 1 : 0 );
        ret.setEnabled( item.enabled );

        return ret;
    }
}
