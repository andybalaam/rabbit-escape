package rabbitescape.engine.menu;

import java.util.HashMap;
import java.util.Map;

public class MenuItem
{
    public static enum Type
    {
        MENU,
        ABOUT,
        DEMO,
        QUIT,
        LEVEL,
    }

    public final Menu menu;
    public final String name;
    public final Type type;
    public final Map<String, Object> nameParams;

    public MenuItem( String name, Menu target )
    {
        this.name = name;
        this.menu = target;
        this.type = Type.MENU;
        this.nameParams = new HashMap<String, Object>();
    }

    public MenuItem( String name, Type type )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = new HashMap<String, Object>();
    }

    public MenuItem( String name, Type type, Map<String, Object> nameParams )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = nameParams;
    }
}
