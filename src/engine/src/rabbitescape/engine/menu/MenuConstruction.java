package rabbitescape.engine.menu;

import rabbitescape.engine.menu.MenuItem.Type;

public class MenuConstruction
{
    public static Menu menu( String intro, MenuItem... items )
    {
        return new Menu( intro, items );
    }

    public static MenuItem item( 
        String name, 
        Menu target,
        boolean enabled, 
        boolean hidden 
    )
    {
        return new MenuItem( name, target, enabled, hidden );
    }

    public static MenuItem item( 
        String name, 
        Type type,
        boolean enabled,
        boolean hidden  
     )
    {
        return new MenuItem( name, type, enabled, hidden  );
    }

    public static MenuItem item( String name, Type type, boolean enabled  )
    {
        return new MenuItem( name, type, enabled, false  );
    }

    public static MenuItem maybeItem( 
        boolean included, 
        String name, 
        Type type,
        boolean enabled, 
        boolean hidden 
    )
    {
        return included ? item( name, type, enabled, hidden ) : null;
    }

    public static MenuItem maybeItem( 
        boolean included, 
        String name,
        Menu target, 
        boolean enabled,
        boolean hidden 
    )
    {
        return included ? item( name, target, enabled, hidden ) : null;
    }

    public static MenuItem maybeItem(
        boolean included, 
        String name, 
        Menu target, 
        boolean enabled 
    )
    {
        return maybeItem( included, name, target, enabled, false );
    }
}
