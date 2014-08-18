package rabbitescape.engine.menu;

public class Menu
{
    public final String intro;
    public final MenuItem[] items;

    public Menu( String intro, MenuItem[] items )
    {
        this.intro = intro;
        this.items = items;
    }
}
