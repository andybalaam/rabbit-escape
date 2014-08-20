package rabbitescape.engine.menu;

import rabbitescape.engine.menu.MenuItem.Type;

public class MenuConstruction
{
    public static Menu menu( String intro, MenuItem... items )
    {
        return new Menu( intro, items );
    }

    public static MenuItem item( String name, Menu target )
    {
        return new MenuItem( name, target );
    }

    public static MenuItem item( String name, Type type )
    {
        return new MenuItem( name, type );
    }
}
