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

    /**
     * Overridden by subclasses that need to refresh their items when
     * something changes.
     */
    public void refresh()
    {
    }
}
