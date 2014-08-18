package rabbitescape.engine.menu;

public class MenuItem
{
    public final Menu menu;
    public final String name;
    public final MenuDefinition.ItemType type;

    public MenuItem( String name, Menu target )
    {
        this.name = name;
        this.menu = target;
        this.type = MenuDefinition.ItemType.MENU;
    }

    public MenuItem( String name, MenuDefinition.ItemType type )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
    }
}
