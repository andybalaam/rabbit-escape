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
    public final boolean enabled;

    public MenuItem( String name, Menu target, boolean enabled )
    {
        this.name = name;
        this.menu = target;
        this.type = Type.MENU;
        this.nameParams = new HashMap<String, Object>();
        this.enabled = enabled;
    }

    public MenuItem( String name, Type type, boolean enabled )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = new HashMap<String, Object>();
        this.enabled = enabled;
    }

    public MenuItem(
        String name,
        Type type,
        Map<String, Object> nameParams,
        boolean enabled
    )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = nameParams;
        this.enabled = enabled;
    }
}
