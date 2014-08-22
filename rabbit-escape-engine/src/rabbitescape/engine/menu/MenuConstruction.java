package rabbitescape.engine.menu;

import rabbitescape.engine.menu.MenuItem.Type;

public class MenuConstruction
{
    public static Menu menu( String intro, MenuItem... items )
    {
        return new Menu( intro, items );
    }

    public static MenuItem item( String name, Menu target, boolean enabled )
    {
        return new MenuItem( name, target, enabled );
    }

    public static MenuItem item( String name, Type type, boolean enabled )
    {
        return new MenuItem( name, type, enabled );
    }
}
