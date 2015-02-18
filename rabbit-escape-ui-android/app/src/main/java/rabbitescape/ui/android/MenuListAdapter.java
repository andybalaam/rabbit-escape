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

    public MenuListAdapter( Context context, Menu menu )
    {
        super( context, android.R.layout.simple_list_item_1, menu.items );
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
        ret.setEnabled( item.enabled );

        return ret;
    }
}
