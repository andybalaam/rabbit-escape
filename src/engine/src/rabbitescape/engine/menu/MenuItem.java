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
        LOAD,
        GITHUB_ISSUE
    }

    public final Menu menu;
    public final String name;
    public final Type type;
    public final Map<String, Object> nameParams;

    public boolean enabled;
    public boolean hidden;

    public MenuItem( String name, Menu target, boolean enabled, boolean hidden )
    {
        this.name = name;
        this.menu = target;
        this.type = Type.MENU;
        this.nameParams = new HashMap<String, Object>();
        this.enabled = enabled;
        this.hidden = hidden;
    }

    public MenuItem( String name, Type type, boolean enabled, boolean hidden )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = new HashMap<String, Object>();
        this.enabled = enabled;
        this.hidden = hidden;
    }

    public MenuItem(
        String name,
        Type type,
        Map<String, Object> nameParams,
        boolean enabled,
        boolean hidden
    )
    {
        this.name = name;
        this.menu = null;
        this.type = type;
        this.nameParams = nameParams;
        this.enabled = enabled;
        this.hidden = hidden;
    }
}
