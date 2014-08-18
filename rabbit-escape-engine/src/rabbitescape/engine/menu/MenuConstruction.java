package rabbitescape.engine.menu;

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

    public static MenuItem item( String name, MenuDefinition.ItemType type )
    {
        return new MenuItem( name, type );
    }
}
